package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelEventHandler.class)
class LevelEventHandlerMixin {
	@Redirect(method = "globalLevelEvent", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void globalLevelEvent(
		ClientLevel instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(name = "soundPos") Vec3 soundPos
	) {
		((Level4) instance).playLocalSound(x, y, z, ((Vec4) soundPos).w, sound, source, volume, pitch, distanceDelay);
	}

	// TODO levelEvent

	@Definition(id = "addParticle", method = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
	@Definition(id = "LARGE_SMOKE", field = "Lnet/minecraft/core/particles/ParticleTypes;LARGE_SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;")
	@Expression("?.addParticle(LARGE_SMOKE, ?, ?, ?, ?, ?, ?)")
	@Redirect(method = "levelEvent", at = @At("MIXINEXTRAS:EXPRESSION"))
	void levelEvent_1501(
		ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(name = "random") RandomSource random
	) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, Vec4i.getW(pos) + random.nextDouble(), xd, yd, zd, zd);
	}

	// TODO levelEvent

	@Definition(id = "normalZ", local = @Local(type = int.class, name = "normalZ"))
	@Expression("normalZ = @(?)")
	@Inject(method = "shootParticles", at = @At("MIXINEXTRAS:EXPRESSION"))
	void shootParticles_normalW(
		int data, BlockPos pos, RandomSource random, SimpleParticleType particle, CallbackInfo ci,
		@Local(name = "direction") Direction direction,
		@Share("normalW") LocalIntRef normalW
	) {
		normalW.set(Direction4.as(direction).getStepW());
	}
	@Redirect(method = "shootParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void shootParticles_addParticle(
		ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Share("normalW") LocalIntRef normalWRef,
		@Local(name = "pow") double pow,
		@Local(name = "normalX") int normalX,
		@Local(name = "normalZ") int normalZ
	) {
		final int normalW = normalWRef.get();
		x += (random.nextDouble() - 0.5) * normalW * 0.5;
		z += (random.nextDouble() - 0.5) * normalW * 0.5;
		final double w = Vec4i.getW(pos) + normalW * 0.6 + 0.5 + normalW * 0.01
			+ (random.nextDouble() - 0.5) * normalX * 0.5
			+ (random.nextDouble() - 0.5) * normalZ * 0.5;
		final double velocityW = normalW * pow + random.nextGaussian() * 0.01;
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w, xd, yd, zd, velocityW);
	}

	@Redirect(method = "notifyNearbyEntities", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB notifyNearbyEntities(BlockPos pos) {
		return new AABB4(pos);
	}
}
