package com.iluha168.mc4d.world.level.levelgen.structure.templatesystem;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

/**
 * Implemented by {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate}.
 */
public interface StructureTemplate4 {
	static void updateShapeAtEdge(LevelAccessor level, @Block.UpdateFlags int updateMode, DiscreteVoxelShape shape, int startX, int startY, int startZ, int startW) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		((DiscreteVoxelShape4) shape).forAllFaces((direction, x, y, z, w) -> {
			((BlockPos4.MutableBlockPos) pos).set(startX + x, startY + y, startZ + z, startW + w);
			neighborPos.setWithOffset(pos, direction);
			BlockState state = level.getBlockState(pos);
			BlockState neighborState = level.getBlockState(neighborPos);
			BlockState newState = state.updateShape(level, level, pos, direction, neighborPos, neighborState, level.getRandom());
			if (state != newState) {
				level.setBlock(pos, newState, updateMode & -2);
			}

			BlockState newNeighborState = neighborState.updateShape(level, level, neighborPos, direction.getOpposite(), pos, newState, level.getRandom());
			if (neighborState != newNeighborState) {
				level.setBlock(neighborPos, newNeighborState, updateMode & -2);
			}
		});
	}

	static BlockPos getZeroPositionWithTransform(BlockPos zeroPos, Mirror mirror, Rotation rotation, int sizeX, int sizeZ, int sizeW) {
		sizeX--;
		sizeZ--;
		sizeW--;
		int mirrorDeltaX = mirror == Mirror.FRONT_BACK ? sizeX : 0;
		int mirrorDeltaZ = mirror == Mirror.LEFT_RIGHT ? sizeZ : 0;
		int mirrorDeltaW = 0; // TODO Mirror W
		BlockPos4 zeroPos4 = (BlockPos4) zeroPos;
		return switch (rotation) {
			case COUNTERCLOCKWISE_90 -> zeroPos4.offset(mirrorDeltaZ, 0, sizeX - mirrorDeltaX, mirrorDeltaW);
			case CLOCKWISE_90 -> zeroPos4.offset(sizeZ - mirrorDeltaZ, 0, mirrorDeltaX, mirrorDeltaW);
			case CLOCKWISE_180 -> zeroPos4.offset(sizeX - mirrorDeltaX, 0, sizeZ - mirrorDeltaZ, mirrorDeltaW);
			case NONE -> zeroPos4.offset(mirrorDeltaX, 0, mirrorDeltaZ, mirrorDeltaW);
		};
	}
}
