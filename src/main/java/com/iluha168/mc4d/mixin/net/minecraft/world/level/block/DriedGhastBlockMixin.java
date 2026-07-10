package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DriedGhastBlock.class)
class DriedGhastBlockMixin {
	// TODO spawnGhastling

	@Inject(method = "animateTick", at = @At("HEAD"))
	void animateTick_w(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci, @Share("w") LocalDoubleRef w) {
		w.set(Vec4i.getW(pos) + 0.5);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void animateTick_playLocalSound(Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, @Share("w") LocalDoubleRef w) {
		((Level4) instance).playLocalSound(x, y, z, w.get(), sound, source, volume, pitch, distanceDelay);
	}
	@Definition(id = "addParticle", method = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
	@Definition(id = "WHITE_SMOKE", field = "Lnet/minecraft/core/particles/ParticleTypes;WHITE_SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;")
	@Expression("?.addParticle(WHITE_SMOKE, ?, ?, ?, ?, ?, ?)")
	@Redirect(method = "animateTick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void animateTick_whiteSmoke(Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, @Share("w") LocalDoubleRef w) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w.get(), xd, yd, zd, zd);
	}

	@Definition(id = "addParticle", method = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
	@Definition(id = "HAPPY_VILLAGER", field = "Lnet/minecraft/core/particles/ParticleTypes;HAPPY_VILLAGER:Lnet/minecraft/core/particles/SimpleParticleType;")
	@Expression("?.addParticle(HAPPY_VILLAGER, ?, ?, ?, ?, ?, ?)")
	@Redirect(method = "animateTick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void animateTick_happyVillager(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Share("w") LocalDoubleRef w,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w.get() + (random.nextFloat() * 2.0F - 1.0F) / 3.0F, xd, yd, zd, zd);
	}
}
