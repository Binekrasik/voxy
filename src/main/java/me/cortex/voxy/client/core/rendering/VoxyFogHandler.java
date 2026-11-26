package me.cortex.voxy.client.core.rendering;

import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IVoxyRenderSystemHolder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Pushes the vanilla render distance fog out so terrain blends into the LoDs instead of the sky, and optionally
 * removes environmental fog, depending on the voxy fog mode.
 */
public final class VoxyFogHandler {
    public static final float REMOVED_FOG_DISTANCE = 99999999f;

    private VoxyFogHandler() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(VoxyFogHandler::onRenderFog);
    }

    private static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) return;
        if (!VoxyConfig.CONFIG.isRenderingEnabled()) return;
        if (IVoxyRenderSystemHolder.getNullable() == null) return;

        //LevelRenderer clamps the terrain fog distance to at least 32 blocks
        float renderDistance = Math.max(event.getRenderer().getRenderDistance(), 32.0F);
        float start = event.getNearPlaneDistance();
        float end = event.getFarPlaneDistance();

        boolean isRenderDistanceFog = event.getType() == FogType.NONE
                && Math.abs(end - renderDistance) < 0.01f
                && Math.abs(start - (renderDistance - Mth.clamp(renderDistance / 10.0F, 4.0F, 64.0F))) < 0.01f;

        boolean remove;
        if (isRenderDistanceFog) {
            remove = true;
        } else {
            boolean fogIsDamnClose = end < 10;
            remove = VoxyConfig.CONFIG.getFogMode().removesVanillaEnvFog && !fogIsDamnClose;
        }

        if (remove) {
            event.setNearPlaneDistance(REMOVED_FOG_DISTANCE);
            event.setFarPlaneDistance(REMOVED_FOG_DISTANCE);
            event.setCanceled(true);
        }

        float[] colour = com.mojang.blaze3d.systems.RenderSystem.getShaderFogColor();
        FogParameters.setLastTerrainFog(remove ? new FogParameters(colour[0], colour[1], colour[2], colour[3], Float.MAX_VALUE, Float.MAX_VALUE)
                : new FogParameters(colour[0], colour[1], colour[2], colour[3], start, end));
    }
}
