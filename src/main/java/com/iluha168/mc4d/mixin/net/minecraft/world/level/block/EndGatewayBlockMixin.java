package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.level.portal.TeleportTransition4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndGatewayBlock.class)
class EndGatewayBlockMixin {
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z"
	))
	boolean animateTick_nextBoolean(RandomSource random, @Share("axis") LocalIntRef axis) {
		axis.set(random.nextInt(3));
		return axis.get() == 1; // Choosing one of 3 horizontal axes instead.
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Local(name = "flip") int flip,
		@Share("axis") LocalIntRef axis
	) {
		final double posW = Vec4i.getW(pos);
		double w = posW + random.nextDouble();
		double wa = (random.nextDouble() - 0.5) * 0.5;
		if (axis.get() == 2) {
			// Rollback vanilla's X branch.
			x = pos.getX() + random.nextDouble();
			xd = (random.nextDouble() - 0.5) * 0.5;

			w = posW + 0.5 + 0.25 * flip;
			wa = random.nextFloat() * 2.0F * flip;
		}
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wa);
	}

	@Redirect(method = "getPortalDestination", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private Vec3 getPortalDestination_ZERO() {
		return Vec4.ZERO;
	}
	@ModifyExpressionValue(method = "getPortalDestination", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFLjava/util/Set;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"
	))
	private TeleportTransition getPortalDestination_new(TeleportTransition transition) {
		final TeleportTransition4 transition4 = TeleportTransition4.as(transition);
		transition4.setWRot(0.0F);
		transition4.setVRot(0.0F);
		return transition;
	}
}
