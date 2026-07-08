package com.iluha168.mc4d.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DebugLevelSource;

/**
 * Implemented by {@link DebugLevelSource}.
 */
public interface DebugLevelSource4 {
	/**
	 * The debug world consists of a single 3D slice for convenience.
	 * Not configurable, do not change, I am just avoiding magic values.
	 */
	int GRID_TRENGTH = 1;

	static BlockState getBlockStateFor(int worldX, int worldZ, int worldW) {
		BlockState state = DebugLevelSource.AIR;
		if (worldX > 0 && worldZ > 0 && worldW > 0 && worldX % 2 != 0 && worldZ % 2 != 0 && worldW % 2 != 0) {
			worldX /= 2;
			worldZ /= 2;
			worldW /= 2;
			if (worldX <= DebugLevelSource.GRID_WIDTH && worldZ <= DebugLevelSource.GRID_HEIGHT && worldW <= GRID_TRENGTH) {
				int index = Mth.abs(worldX * DebugLevelSource.GRID_WIDTH + worldZ);
				if (index < DebugLevelSource.ALL_BLOCKS.size()) {
					state = DebugLevelSource.ALL_BLOCKS.get(index);
				}
			}
		}

		return state;
	}
}
