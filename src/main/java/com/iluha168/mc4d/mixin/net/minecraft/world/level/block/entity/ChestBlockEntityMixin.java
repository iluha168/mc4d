package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
class ChestBlockEntityMixin {
	@Definition(id = "x", local = @Local(type = double.class, name = "x"))
	@Expression("x = ? + 0.5")
	@Inject(method = "playSound", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void playSound_setW(Level level, BlockPos worldPosition, BlockState blockState, SoundEvent event, CallbackInfo ci, @Share("w") LocalDoubleRef w) {
		w.set(Vec4i.getW(worldPosition) + 0.5);
	}
	@Definition(id = "x", local = @Local(type = double.class, name = "x"))
	@Expression("x = x + ? * 0.5")
	@Inject(method = "playSound", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void playSound_addW(
		Level level, BlockPos worldPosition, BlockState blockState, SoundEvent event, CallbackInfo ci,
		@Share("w") LocalDoubleRef w,
		@Local(name = "direction") Direction direction
	) {
		w.set(w.get() + Direction4.as(direction).getStepW() * 0.5);
	}
	@Redirect(method = "playSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	private static void playSound(Level level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, @Share("w") LocalDoubleRef w) {
		((Level4) level).playSound(except, x, y, z, w.get(), sound, source, volume, pitch);
	}
}
