package com.iluha168.mc4d.world.level.levelgen;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Implemented by {@link net.minecraft.world.level.levelgen.Heightmap}.
 */
public interface Heightmap4 {
	boolean update(int localX, int localY, int localZ, int localW, BlockState state);
	int getFirstAvailable(int x, int z, int w);
	int getHighestTaken(int x, int z, int w);
	void setHeight(int x, int z, int w, int height);
}
