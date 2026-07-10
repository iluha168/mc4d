package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkullBlockRenderer.class)
class SkullBlockRendererMixin {
	@Redirect(method = "getRenderBoundingBox(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)Lnet/minecraft/world/phys/AABB;", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB getRenderBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(name = "pos") BlockPos pos) {
		final int posW = Vec4i.getW(pos);
		return new AABB4(
			minX, minY, minZ, posW - .75,
			maxX, maxY, maxZ, posW + 1.75
		);
	}
}
