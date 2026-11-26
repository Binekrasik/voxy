package me.cortex.voxy.client.core.rendering;

import com.mojang.blaze3d.systems.RenderSystem;

/**
 * Fog state voxy applies to LoD terrain. {@code environmentalStart/End} describe fog that does not come from the
 * vanilla render distance (water, lava, blindness, thick biome fog). They are {@link Float#MAX_VALUE} when there is
 * none, or when voxy removed it.
 */
public record FogParameters(float red, float green, float blue, float alpha, float environmentalStart, float environmentalEnd) {
    public static final FogParameters NONE = new FogParameters(0, 0, 0, 0, Float.MAX_VALUE, Float.MAX_VALUE);

    private static volatile FogParameters LAST_TERRAIN_FOG = NONE;

    /** The terrain fog of the most recent frame, for use before this frame's fog has been set up. */
    public static FogParameters lastTerrainFog() {
        return LAST_TERRAIN_FOG;
    }

    static void setLastTerrainFog(FogParameters fog) {
        LAST_TERRAIN_FOG = fog;
    }

    /** Reads the terrain fog currently set in {@link RenderSystem}, after {@code VoxyFogHandler} adjusted it. */
    public static FogParameters fromRenderSystem() {
        float[] colour = RenderSystem.getShaderFogColor();
        float start = RenderSystem.getShaderFogStart();
        float end = RenderSystem.getShaderFogEnd();
        if (start >= VoxyFogHandler.REMOVED_FOG_DISTANCE || end >= VoxyFogHandler.REMOVED_FOG_DISTANCE) {
            start = Float.MAX_VALUE;
            end = Float.MAX_VALUE;
        }
        return new FogParameters(colour[0], colour[1], colour[2], colour[3], start, end);
    }
}
