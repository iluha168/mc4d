package com.iluha168.mc4d.world.level.block.grower;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implemented by {@link TreeGrower}.
 */
public interface TreeGrower4 {
	static boolean isTwoByTwoByTwoSapling(BlockState state, BlockGetter level, BlockPos pos, int ox, int oz, int ow) {
		final Block block = state.getBlock();
		final BlockPos4 pos4 = (BlockPos4) pos;
		return level.getBlockState(pos4.offset(ox       , 0, oz       , ow        )).is(block)
			&& level.getBlockState(pos4.offset(ox + 1, 0, oz       , ow        )).is(block)
			&& level.getBlockState(pos4.offset(ox       , 0, oz + 1, ow        )).is(block)
			&& level.getBlockState(pos4.offset(ox + 1, 0, oz + 1, ow        )).is(block)
			&& level.getBlockState(pos4.offset(ox       , 0, oz       , ow + 1)).is(block)
			&& level.getBlockState(pos4.offset(ox + 1, 0, oz       , ow + 1)).is(block)
			&& level.getBlockState(pos4.offset(ox       , 0, oz + 1, ow + 1)).is(block)
			&& level.getBlockState(pos4.offset(ox + 1, 0, oz + 1, ow + 1)).is(block);
	}
}
