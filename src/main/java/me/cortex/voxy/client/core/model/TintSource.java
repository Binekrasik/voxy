package me.cortex.voxy.client.core.model;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.List;

/** One tint layer of a block or fluid, -1 means untinted. */
@FunctionalInterface
public interface TintSource {
    //Models in 1.21.1 only use small tint indices, flower beds use the second one for their stems
    int MAX_TINT_INDEX = 2;

    int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos);

    /** The tint layers of a block state, or null if it is never tinted. */
    static List<TintSource> of(BlockState state) {
        if (state.getBlock() instanceof LiquidBlock) {//If is pure fluid
            var fluid = state.getFluidState();
            var extensions = IClientFluidTypeExtensions.of(fluid);
            return List.of((s, level, pos) -> {
                int colour = extensions.getTintColor(s.getFluidState(), level, pos);
                return colour == 0xFFFFFFFF ? -1 : colour;
            });
        }
        var blockColors = Minecraft.getInstance().getBlockColors();
        if (!blockColors.blockColors.containsKey(state.getBlock())) {
            return null;
        }
        TintSource[] sources = new TintSource[MAX_TINT_INDEX];
        for (int i = 0; i < MAX_TINT_INDEX; i++) {
            int tintIndex = i;
            sources[i] = (s, level, pos) -> blockColors.getColor(s, level, pos, tintIndex);
        }
        return List.of(sources);
    }
}
