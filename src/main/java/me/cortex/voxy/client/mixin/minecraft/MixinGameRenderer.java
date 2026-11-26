package me.cortex.voxy.client.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.cortex.voxy.client.core.util.RawProjectionCapture;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getProjectionMatrix(D)Lorg/joml/Matrix4f;", ordinal = 0))
    private Matrix4f voxy$captureRawProjection(Matrix4f projection) {
        RawProjectionCapture.set(projection);
        return projection;
    }
}
