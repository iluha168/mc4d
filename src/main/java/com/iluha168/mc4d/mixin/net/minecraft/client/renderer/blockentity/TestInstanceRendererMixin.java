package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.api.net.minecraft.world.phys.AABB4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import net.minecraft.client.renderer.blockentity.TestInstanceRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TestInstanceRenderer.class)
class TestInstanceRendererMixin {
	// TODO submitErrorMarker (cull properly)

	@Redirect(method = "submitErrorMarker", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB submitErrorMarker_box(BlockPos pos) {
		return new AABB4(pos);
	}
	@Redirect(method = "submitErrorMarker", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;atLowerCornerWithOffset(Lnet/minecraft/core/Vec3i;DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 submitErrorMarker_text(Vec3i pos, double x, double y, double z) {
		return Vec4.atLowerCornerWithOffset(pos, x, y, z, z);
	}
}
