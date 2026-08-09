package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.block.entity.BoundingBoxRenderable4;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BoundingBoxRenderable.class)
interface BoundingBoxRenderableMixin extends BoundingBoxRenderable4 {
	@Mixin(BoundingBoxRenderable.RenderableBox.class)
	class RenderableBoxMixin implements BoundingBoxRenderable4.RenderableBox {
		@Overwrite
		@Deprecated
		public static BoundingBoxRenderable.RenderableBox fromCorners(int x1, int y1, int z1, int x2, int y2, int z2) {
			throw Err4.arguments3("BoundingBoxRenderable4.RenderableBox#fromCorners");
		}
	}
}
