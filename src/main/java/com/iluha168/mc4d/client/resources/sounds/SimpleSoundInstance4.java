package com.iluha168.mc4d.client.resources.sounds;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * Implemented by {@link SimpleSoundInstance}.
 */
public interface SimpleSoundInstance4 {
	static SimpleSoundInstance from(
		SoundEvent sound, SoundSource source,
		float volume, float pitch,
		RandomSource random,
		double x, double y, double z, double w
	) {
		return from(sound, source, volume, pitch, random, false, 0, SoundInstance.Attenuation.LINEAR, x, y, z, w);
	}

	static SimpleSoundInstance from(
		SoundEvent sound, SoundSource source,
		float volume, float pitch,
		RandomSource random,
		boolean looping,
		int delay,
		SoundInstance.Attenuation attenuation,
		double x, double y, double z, double w
	) {
		SimpleSoundInstance soundInstance = new SimpleSoundInstance(sound, source, volume, pitch, random, looping, delay, attenuation, x, y, z);
		((SoundInstance4) soundInstance).setW(w);
		return soundInstance;
	}

	static SimpleSoundInstance forAmbientMood(SoundEvent sound, RandomSource random, double x, double y, double z, double w) {
		return SimpleSoundInstance4.from(sound, SoundSource.AMBIENT, 1.0F, 1.0F, random, false, 0, SoundInstance.Attenuation.LINEAR, x, y, z, w);
	}
}
