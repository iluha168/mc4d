package com.iluha168.mc4d.mixin.net.minecraft.client.resources.sounds;

import com.iluha168.mc4d.client.resources.sounds.SimpleSoundInstance4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.AmbientMoodSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeAmbientSoundsHandler.class)
class BiomeAmbientSoundsHandlerMixin {
	@Shadow
	@Final
	private LocalPlayer player;

	@Shadow
	@Final
	private RandomSource random;

	@Redirect(method = "lambda$tick$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick_blockSamplingPos(
		double x, double y, double z,
		@Local(name = "searchSpan") int searchSpan,
		@Local(argsOnly = true, name = "mood") AmbientMoodSettings mood
	) {
		return BlockPos4.containing(x, y, z, ((Entity4) this.player).getW() + this.random.nextInt(searchSpan) - mood.blockSearchExtent());
	}
	@ModifyArg(method = "lambda$tick$1", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sqrt(D)D"
	))
	double tick_blockDistance(
		double sumOfSquares,
		@Local(name = "blockSamplingPos") BlockPos blockSamplingPos,
		@Share("blockDirectionW")LocalDoubleRef blockDirectionW
	) {
		blockDirectionW.set(Vec4i.getW(blockSamplingPos) + 0.5 - ((Entity4) this.player).getW());
		return sumOfSquares + blockDirectionW.get() * blockDirectionW.get();
	}
	@Redirect(method = "lambda$tick$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forAmbientMood(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/util/RandomSource;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
	))
	SimpleSoundInstance tick_forAmbientMood(
		SoundEvent sound, RandomSource random, double x, double y, double z,
		@Share("blockDirectionW")LocalDoubleRef blockDirectionW,
		@Local(name = "blockDistance") double blockDistance,
		@Local(name = "soundSourceDistance") double soundSourceDistance
	) {
		return SimpleSoundInstance4.forAmbientMood(
			sound, random, x, y, z,
			((Entity4) this.player).getW() + blockDirectionW.get() / blockDistance * soundSourceDistance
		);
	}
}
