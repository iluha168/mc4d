package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HoneyBlock.class)
class HoneyBlockMixin extends BlockMixin {
	@Definition(id = "dz", local = @Local(type = double.class, name = "dz"))
	@Expression("dz + ? > ?")
	@ModifyExpressionValue(method = "isSlidingDown", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean isSlidingDown(
		boolean original,
		@Local(name = "overlapDistance") double overlapDistance,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "entity") Entity entity
	) {
		final double dw = Math.abs(Vec4i.getW(pos) + 0.5 - ((Entity4) entity).getW());
		return original || dw + AABB4.EPSILON > overlapDistance;
	}

	@Redirect(method = "doSlideMovement", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 0
	))
	Vec3 doSlideMovement_withHorizontalReductionFactor(
		double x, double y, double z,
		@Local(name = "deltaMovement") Vec3 deltaMovement,
		@Local(name = "horizontalReductionFactor") double horizontalReductionFactor
	) {
		return new Vec4(x, y, z, ((Vec4) deltaMovement).w * horizontalReductionFactor);
	}
	@Redirect(method = "doSlideMovement", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 1
	))
	Vec3 doSlideMovement(double x, double y, double z, @Local(name = "deltaMovement") Vec3 deltaMovement) {
		return new Vec4(x, y, z, ((Vec4) deltaMovement).w);
	}

	@Redirect(method = "showParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void showParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "entity") Entity entity
	) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, ((Entity4) entity).getW(), xd, yd, zd, zd);
	}
}
