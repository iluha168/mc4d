package com.iluha168.mc4d.world.level.block.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;

/**
 * Implemented by {@link BoundingBoxRenderable}.
 */
public interface BoundingBoxRenderable4 {
	/**
	 * Implemented by {@link BoundingBoxRenderable.RenderableBox}.
	 */
	interface RenderableBox {
		static BoundingBoxRenderable.RenderableBox fromCorners(
			int x1, int y1, int z1, int w1,
			int x2, int y2, int z2, int w2
		) {
			final int x = Math.min(x1, x2);
			final int y = Math.min(y1, y2);
			final int z = Math.min(z1, z2);
			final int w = Math.min(w1, w2);
			return new BoundingBoxRenderable.RenderableBox(
				BlockPos4.from(x, y, z, w),
				Vec4i.from(
					Math.max(x1, x2) - x,
					Math.max(y1, y2) - y,
					Math.max(z1, z2) - z,
					Math.max(w1, w2) - w
				)
			);
		}
	}
}
