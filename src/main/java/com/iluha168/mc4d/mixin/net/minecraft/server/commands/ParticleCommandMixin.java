package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.api.net.minecraft.server.level.ServerLevel4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ParticleCommand.class)
class ParticleCommandMixin {
	@Redirect(method = "sendParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/particles/ParticleOptions;ZZDDDIDDDD)Z"
	))
	private static boolean sendParticles(
		ServerLevel level,
		ServerPlayer player,
		ParticleOptions particle,
		boolean overrideLimiter,
		boolean alwaysShow,
		double x, double y, double z,
		int count,
		double xDist, double yDist, double zDist,
		double speed,
		@Local(argsOnly = true, name = "pos") Vec3 pos,
		@Local(argsOnly = true, name = "delta") Vec3 delta
	) {
		return ((ServerLevel4) level).sendParticles(
			player, particle, overrideLimiter, alwaysShow,
			x, y, z, ((Vec4) pos).w,
			count,
			xDist, yDist, zDist, ((Vec4) delta).w,
			speed
		);
	}
}
