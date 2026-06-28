package com.iluha168.mc4d.mixin.net.minecraft.client.sounds;

import com.iluha168.mc4d.client.resources.sounds.SoundInstance4;
import com.iluha168.mc4d.com.mojang.blaze3d.audio.Channel4;
import com.iluha168.mc4d.com.mojang.blaze3d.audio.Library4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
abstract class SoundEngineMixin {
	@Shadow	@Final private Library library;
	@Shadow @Final private Listener listener;
	@Shadow	protected abstract float calculateVolume(float volume, SoundSource source);

	/**
	 * Curve that maps a distance in W from [0; {@code saturateDistance}] to an angle [0deg; 90deg] in radians.
	 * After some experiments I found that differences closer to 0deg are more perceivable, therefore, this is a parabola, not a linear function.
	 */
	@Unique
	private static float wOffsetToAngleRadians(double wOffset, double saturateDistance) {
		wOffset -= Channel4.W_NEAR;
		saturateDistance -= Channel4.W_NEAR;
		double t = Mth.clamp(Math.abs(wOffset) / Math.max(saturateDistance, Mth.EPSILON), 0.0, 1.0);
		double degrees = 90.0 * (1.0 - (1.0 - t) * (1.0 - t));
		return (float) Math.toRadians(degrees);
	}

	@Unique
	private void tickDecorrelationMode(SoundInstance instance, ChannelAccess.ChannelHandle handle) {
		if (instance.isRelative()) {
			// UI, ambient music, etc. stay exactly vanilla.
			return;
		}
		Sound sound = instance.getSound();
		if (sound == null) {
			return;
		}
		Vec3 listenerPos3 = this.listener.getTransform().position();
		if (!(listenerPos3 instanceof Vec4 listenerPos4)) throw Err4.field4missing("ListenerTransform#position#w");
		final double dwAbs = Math.abs(((SoundInstance4) instance).getW() - listenerPos4.w);

		// Copies volume logic from vanilla `play` method
		final float instanceVolume = instance.getVolume();
		final float attenuationDistance = Math.max(instanceVolume, 1.0F) * sound.getAttenuationDistance();
		final SoundSource soundSource = instance.getSource();
		final float volume = this.calculateVolume(instanceVolume, soundSource);
			// we are not modifying pitch
		SoundInstance.Attenuation attenuation = instance.getAttenuation();

		if (((Library4) this.library).supportsStereoAngles()
			&& attenuation == SoundInstance.Attenuation.LINEAR
			&& dwAbs >= Channel4.W_NEAR
		) {
			handle.execute(channel -> {
				Channel4 channel4 = (Channel4) channel;
				// AL_POSITION call in setSelfPosition is 3D, so we have to multiply by distance to w to get the same fade effect
				channel.setVolume(volume * (float) Mth.square(Mth.clamp(1.0 - dwAbs / attenuationDistance, 0.0, 1.0)));
				channel4.setStereoAngle(wOffsetToAngleRadians(dwAbs, sound.getAttenuationDistance()));

				if (channel4.setStereoMode(true)) {
					// Disables vanilla behavior, we are replacing attenuation with `setStereoAngle` here :P
					channel.disableAttenuation();
					channel.setSelfPosition(Vec4.ZERO);
					channel.setRelative(true);
				}
			});
		} else {
			// Restores mono mode - this is a partial copy of vanilla `play`
			Vec4 position = new Vec4(instance.getX(), instance.getY(), instance.getZ(), ((SoundInstance4) instance).getW());
			handle.execute(channel -> {
				if (((Channel4) channel).setStereoMode(false)) {
					channel.setVolume(volume);
					if (attenuation == SoundInstance.Attenuation.LINEAR) {
						channel.linearAttenuation(attenuationDistance);
					} else {
						channel.disableAttenuation();
					}

					channel.setSelfPosition(position);
					channel.setRelative(false);
				}
			});
		}
	}

	@Redirect(method = "tickInGameSound", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 tickInGameSound_position(double x, double y, double z, @Local(name = "instance") TickableSoundInstance instance) {
		return new Vec4(x, y, z, ((SoundInstance4) instance).getW());
	}
	@Definition(id = "minDeleteTime", local = @Local(type = int.class, name = "minDeleteTime"))
	@Expression("minDeleteTime = @(?)")
	@Inject(method = "tickInGameSound", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tickInGameSound_everyInstanceToChannel(CallbackInfo ci, @Local(name = "instance") SoundInstance instance, @Local(name = "handle") ChannelAccess.ChannelHandle handle) {
		this.tickDecorrelationMode(instance, handle);
	}

	@Redirect(method = "play", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 play_position(double x, double y, double z, @Local(name = "instance", argsOnly = true) SoundInstance instance) {
		return new Vec4(x, y, z, ((SoundInstance4) instance).getW());
	}
	@Inject(method = "play", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V",
		shift = At.Shift.AFTER
	))
	void play(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir, @Local(name = "handle") ChannelAccess.ChannelHandle handle) {
		this.tickDecorrelationMode(instance, handle);
	}
}
