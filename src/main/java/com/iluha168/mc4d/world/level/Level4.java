package com.iluha168.mc4d.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

/**
 * All {@link net.minecraft.world.level.Level} instances implement {@link Level4}.
 */
public interface Level4 {
	int MAX_LEVEL_SIZE = 100000;

	LevelChunk getChunk(int chunkX, int chunkZ, int chunkW);

	BlockPos getBlockRandomPos(int xo, int yo, int zo, int wo, int yMask);

	void playSeededSound(
		final @Nullable Entity except,
		final double x, final double y, final double z, final double w,
		final Holder<SoundEvent> sound,
		final SoundSource source,
		final float volume,
		final float pitch,
		final long seed
	);

	void playSeededSound(@Nullable Entity except, double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch, long seed);
	void playSound(@Nullable Entity except, double x, double y, double z, double w, SoundEvent sound, SoundSource source);
	void playSound(@Nullable Entity except, double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch);
	void playSound(@Nullable Entity except, double x, double y, double z, double w, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch);

	default void playLocalSound(double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
	}

	void addParticle(
		ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow,
		double x, double y, double z, double w,
		double xd, double yd, double zd, double wd
	);

	void addAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double w, double xd, double yd, double zd, double wd);
	void addAlwaysVisibleParticle(ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double w, double xd, double yd, double zd, double wd);
}
