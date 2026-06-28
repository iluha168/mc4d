package com.iluha168.mc4d.mixin.net.minecraft.client.resources.sounds;

import com.iluha168.mc4d.client.resources.sounds.SimpleSoundInstance4;
import com.iluha168.mc4d.client.resources.sounds.SoundInstance4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleSoundInstance.class)
class SimpleSoundInstanceMixin extends AbstractSoundInstanceMixin implements SimpleSoundInstance4 {
	@Inject(method = "<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
	void init(SoundEvent sound, SoundSource source, float volume, float pitch, RandomSource random, BlockPos pos, CallbackInfo ci) {
		this.setW(Vec4i.getW(pos) + 0.5);
	}

	@ModifyExpressionValue(method = "forUI(Lnet/minecraft/sounds/SoundEvent;FF)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/resources/Identifier;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDDZ)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
	))
	private static SimpleSoundInstance forUI(SimpleSoundInstance original) {
		((SoundInstance4) original).setW(0.0);
		return original;
	}

	@ModifyExpressionValue(method = "forMusic", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/resources/Identifier;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDDZ)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
	))
	private static SimpleSoundInstance forMusic(SimpleSoundInstance original) {
		((SoundInstance4) original).setW(0.0);
		return original;
	}

	@ModifyExpressionValue(method = "forJukeboxSong", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
	))
	private static SimpleSoundInstance forJukeboxSong(SimpleSoundInstance original, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		if (!(pos instanceof Vec4 pos4)) throw Err4.container3();
		((SoundInstance4) original).setW(pos4.w());
		return original;
	}

	@ModifyExpressionValue(method = "forLocalAmbience", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/resources/Identifier;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDDZ)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
	))
	private static SimpleSoundInstance forLocalAmbience(SimpleSoundInstance original) {
		((SoundInstance4) original).setW(0.0);
		return original;
	}

	@Overwrite
	@Deprecated
	public static SimpleSoundInstance forAmbientMood(SoundEvent sound, RandomSource random, double x, double y, double z) {
		throw Err4.arguments3("SimpleSoundInstance4#forAmbientMood");
	}
}
