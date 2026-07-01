package com.iluha168.mc4d.world.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ItemEntity}.
 */
public interface ItemEntity4 {
	static ItemEntity from(Level level, double x, double y, double z, double w, ItemStack itemStack) {
		ItemEntity item = new ItemEntity(level, x, y, z, itemStack);
		((ItemEntity4) item).init_finish(x, y, z, w);
		return item;
	}
	static ItemEntity from(Level level, double x, double y, double z, double w, ItemStack itemStack, double deltaX, double deltaY, double deltaZ, double deltaW) {
		ItemEntity item = new ItemEntity(level, x, y, z, itemStack, deltaX, deltaY, deltaZ);
		((ItemEntity4) item).init_finish(x, y, z, w, deltaX, deltaY, deltaZ, deltaW);
		return item;
	}

	@ApiStatus.Internal
	void init_finish(double x, double y, double z, double w);
	@ApiStatus.Internal
	void init_finish(double x, double y, double z, double w, double deltaX, double deltaY, double deltaZ, double deltaW);
}
