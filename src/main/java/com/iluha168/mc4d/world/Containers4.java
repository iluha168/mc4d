package com.iluha168.mc4d.world;

import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Implemented by {@link Containers}.
 */
public interface Containers4 {
	static void dropItemStack(Level level, double x, double y, double z, double w, ItemStack itemStack) {
		double size = EntityType.ITEM.getWidth();
		double centerRange = 1.0 - size;
		double halfSize = size / 2.0;
		RandomSource random = level.getRandom();
		double xo = Math.floor(x) + random.nextDouble() * centerRange + halfSize;
		double yo = Math.floor(y) + random.nextDouble() * centerRange;
		double zo = Math.floor(z) + random.nextDouble() * centerRange + halfSize;
		double wo = Math.floor(w) + random.nextDouble() * centerRange + halfSize;

		while (!itemStack.isEmpty()) {
			ItemEntity entity = ItemEntity4.from(level, xo, yo, zo, wo, itemStack.split(random.nextInt(21) + 10));
			entity.setDeltaMovement(new Vec4(
				random.triangle(0.0, 0.11485000171139836),
				random.triangle(0.2, 0.11485000171139836),
				random.triangle(0.0, 0.11485000171139836),
				random.triangle(0.0, 0.11485000171139836)
			));
			level.addFreshEntity(entity);
		}
	}

	// TODO everything
}
