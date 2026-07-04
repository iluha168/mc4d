package com.iluha168.mc4d.world.entity.item;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link net.minecraft.world.entity.item.FallingBlockEntity}.
 */
public interface FallingBlockEntity4 {
	static FallingBlockEntity from(Level level, double x, double y, double z, double w, BlockState blockState) {
		FallingBlockEntity entity = new FallingBlockEntity(level, x, y, z, blockState);
		((FallingBlockEntity4) entity).init_finish(x, y, z, w);
		return entity;
	}
	@ApiStatus.Internal
	void init_finish(double x, double y, double z, double w);
}
