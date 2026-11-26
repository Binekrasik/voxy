package me.cortex.voxy.common.util;

import me.cortex.voxy.common.Logger;

//Helpers for shutdown paths that wait on other threads. A shutdown that waits on a stuck thread freezes the game,
// these log where the waited-on threads are so the freeze can be diagnosed from the log alone
public class ThreadStallLog {
    //Waits for the thread to exit, calling wake once a second. The thread's stack is logged after 10 seconds, then
    // at doubling intervals
    public static void joinLoggingStalls(Thread thread, Runnable wake) {
        long start = System.nanoTime();
        long nextLogSeconds = 10;
        try {
            while (thread.isAlive()) {
                wake.run();
                thread.join(1000);
                long waitedSeconds = (System.nanoTime() - start) / 1_000_000_000L;
                if (waitedSeconds >= nextLogSeconds && thread.isAlive()) {
                    nextLogSeconds *= 2;
                    var sb = new StringBuilder("Still waiting for thread '" + thread.getName() + "' (" + thread.getState() + ") to stop after " + waitedSeconds + "s, it is at:");
                    appendFrames(sb, thread.getStackTrace());
                    Logger.warn(sb.toString());
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //Logs the stacks of all threads currently inside the given method, used when a shutdown waits on work running on
    // threads it does not own
    public static void logThreadsIn(String message, Class<?> clazz, String method) {
        StringBuilder sb = new StringBuilder(message);
        for (var entry : Thread.getAllStackTraces().entrySet()) {
            var frames = entry.getValue();
            boolean inMethod = false;
            for (var frame : frames) {
                if (frame.getClassName().equals(clazz.getName()) && frame.getMethodName().equals(method)) {
                    inMethod = true;
                    break;
                }
            }
            if (!inMethod) continue;
            sb.append("\n\"").append(entry.getKey().getName()).append("\" ").append(entry.getKey().getState());
            appendFrames(sb, frames);
        }
        Logger.warn(sb.toString());
    }

    private static void appendFrames(StringBuilder sb, StackTraceElement[] frames) {
        for (var frame : frames) {
            sb.append("\n\tat ").append(frame);
        }
    }
}
