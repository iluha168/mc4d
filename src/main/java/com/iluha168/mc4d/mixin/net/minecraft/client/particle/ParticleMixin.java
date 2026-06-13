package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.IAABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Particle.class)
class ParticleMixin {
	@Shadow
	protected float bbWidth;

	@Redirect(method = "<clinit>", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB INITIAL_AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new AABB4(minX, minY, minZ, 0, maxX, maxY, maxZ, 0);
	}

	@Redirect(method = "setSize", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB setSize(
		double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "w") float w,
		@Local(name = "aabb") AABB aabb
	) {
		IAABB4 previous = (IAABB4) aabb;
		final double newMinW = (previous.minW() + previous.maxW() - w) / 2;
		return new AABB4(minX, minY, minZ, newMinW, maxX, maxY, maxZ, newMinW + this.bbWidth);
	}
}
