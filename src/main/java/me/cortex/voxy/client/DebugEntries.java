package me.cortex.voxy.client;

import me.cortex.voxy.client.core.IVoxyRenderSystemHolder;
import me.cortex.voxy.client.core.VoxyRenderSystem;
import me.cortex.voxy.client.core.util.GPUTiming;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds voxy lines to the right side of the F3 screen. Detailed GPU timing statistics are
 * collected while the F3 profiler chart (F3+1) is open.
 */
public class DebugEntries {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(DebugEntries::onDebugText);
    }

    private static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        var debugOverlay = Minecraft.getInstance().getDebugOverlay();
        updateGpuDebug(debugOverlay.showDebugScreen() && debugOverlay.showProfilerChart());

        List<String> lines = event.getRight();
        lines.add("");
        if (!VoxyCommon.isAvailable()) {
            lines.add(ChatFormatting.RED + "voxy-" + VoxyCommon.MOD_VERSION);//Voxy installed, not avalible
            return;
        }
        var instance = VoxyCommon.getInstance();
        if (instance == null) {
            lines.add(ChatFormatting.YELLOW + "voxy-" + VoxyCommon.MOD_VERSION);//Voxy avalible, no instance active
            return;
        }
        VoxyRenderSystem vrs = IVoxyRenderSystemHolder.getNullable();
        lines.add((vrs == null ? ChatFormatting.DARK_GREEN : ChatFormatting.GREEN) + "voxy-" + VoxyCommon.MOD_VERSION);

        List<String> extra = new ArrayList<>();
        instance.addDebug(extra);
        if (vrs != null) {
            vrs.addDebugInfo(extra);
        }
        lines.addAll(extra);
    }

    private static boolean previousGpuDebugEnabled = false;
    private static void updateGpuDebug(boolean enabled) {
        if (enabled != previousGpuDebugEnabled) {
            previousGpuDebugEnabled = enabled;

            GPUTiming.INSTANCE.setEnabled(enabled);
            RenderStatistics.enabled = enabled;
            var renderer = Minecraft.getInstance().levelRenderer;
            if (renderer != null) renderer.allChanged();
        }
    }
}
