package com.iluha168.mc4d.world.level.block.entity;

import net.minecraft.world.level.block.entity.CampfireBlockEntity;

/**
 * Implemented by {@link CampfireBlockEntity}.
 */
public interface CampfireBlockEntity4 {
	/**
	 * {@link CampfireBlockEntity#NUM_SLOTS}. Items sit on horizontal corners, in 3D a plane has 4, in 4D a cube has 8.
	 * OP, I know, but it is mathematically accurate.
	 */
	@SuppressWarnings("JavadocReference")
	int NUM_SLOTS = 8;
}
