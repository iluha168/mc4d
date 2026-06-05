package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.attribute.GaussianSampler;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GaussianSampler.class)
class GaussianSamplerMixin {
	@Redirect(method = "sample", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 sample(Vec3 position, double x, double y, double z) {
		// TODO this just prevents crash, implement actual logic
		return ((Vec4) position).subtract(x, y, z, z);
	}
}
