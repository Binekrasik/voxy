package me.cortex.voxy.client.core.util;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Holds the level projection matrix as {@code GameRenderer.renderLevel} created it, before view bobbing,
 * hurt shake and nausea are multiplied in.
 */
public final class RawProjectionCapture {
    private static final Matrix4f RAW = new Matrix4f();
    private static boolean captured;

    private RawProjectionCapture() {}

    public static void set(Matrix4fc projection) {
        RAW.set(projection);
        captured = true;
    }

    /** The raw projection, or {@code fallback} when no level frame has been rendered yet. */
    public static Matrix4fc get(Matrix4fc fallback) {
        return captured ? RAW : fallback;
    }
}
