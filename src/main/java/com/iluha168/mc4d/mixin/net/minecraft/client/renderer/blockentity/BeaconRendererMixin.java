package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.phys.AABB4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconRenderer.class)
class BeaconRendererMixin {
	@Redirect(method = "shouldRender", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 shouldRender(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}

	@Redirect(method = "getRenderBoundingBox", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB getRenderBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(name = "pos") BlockPos pos) {
		final int posW = Vec4i.getW(pos);
		return new AABB4(
			minX, minY, minZ, posW,
			maxX, maxY, maxZ, posW + 1.0
		);
	}
}
