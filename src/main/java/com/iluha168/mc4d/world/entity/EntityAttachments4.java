package com.iluha168.mc4d.world.entity;

import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;

/**
 * Implemented by {@link EntityAttachments}.
 */
public interface EntityAttachments4 {
	EntityAttachments scale(float x, float y, float z, float w);

	/**
	 * Implemented by {@link EntityAttachments.Builder}.
	 */
	interface Builder {
		EntityAttachments.Builder attach(EntityAttachment attachment, float x, float y, float z, float w);
	}
}
