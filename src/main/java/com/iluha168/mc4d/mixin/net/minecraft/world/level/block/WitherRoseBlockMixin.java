package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherRoseBlock.class)
class WitherRoseBlockMixin {
	@Definition(id = "z", local = @Local(type = double.class, name = "z"))
	@Expression("z = @(?)")
	@Inject(method = "animateTick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void animateTick_w(
		BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci,
		@Share("w") LocalDoubleRef w,
		@Local(name = "shapeCenter") Vec3 shapeCenter
	) {
		w.set(Vec4i.getW(pos) + ((Vec4) shapeCenter).w);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Share("w") LocalDoubleRef w,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w.get() + random.nextDouble() / 5.0, xd, yd, zd, zd);
	}
}
