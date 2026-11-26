package me.cortex.voxy.client.mixin.iris;

import me.cortex.voxy.client.core.rendering.FogParameters;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import me.cortex.voxy.client.core.IVoxyRenderSystemHolder;
import me.cortex.voxy.client.core.util.IrisUtil;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.glViewport;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Inject(method = "renderLevel", at = @At("HEAD"), order = 100)
    private void voxy$injectIrisCompat(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (IrisUtil.irisShaderPackEnabled()) {
            var renderer = IVoxyRenderSystemHolder.getNullableHolder();
            if (renderer != null) {
                //Fixthe fucking viewport dims, fuck iris
                var target = Minecraft.getInstance().getMainRenderTarget();
                glViewport(0,0, target.width, target.height);

                var pos = camera.getPosition();
                IrisUtil.CAPTURED_VIEWPORT_PARAMETERS = new IrisUtil.CapturedViewportParameters(new ChunkRenderMatrices(new Matrix4f(projectionMatrix), new Matrix4f(frustumMatrix)), FogParameters.lastTerrainFog(), target.width, target.height, pos.x, pos.y, pos.z);
            }
        }
    }
}
