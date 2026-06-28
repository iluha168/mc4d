package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.WorldgenRandom4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldgenRandom.class)
abstract class WorldgenRandomMixin extends LegacyRandomSource implements WorldgenRandom4 {
	public WorldgenRandomMixin(long seed) {
		super(seed);
	}

	@Shadow public abstract void setSeed(long seed);

	@Overwrite
	@Deprecated
	public long setDecorationSeed(long seed, int chunkX, int chunkZ) {
		throw Err4.arguments2("WorldgenRandom4#setDecorationSeed");
	}
	@Override
	public long setDecorationSeed(long seed, int chunkX, int chunkZ, int chunkW) {
		this.setSeed(seed);
		long xScale = this.nextLong() | 1L;
		long zScale = this.nextLong() | 1L;
		long wScale = this.nextLong() | 1L;
		long result = chunkX * xScale + chunkZ * zScale + chunkW * wScale ^ seed;
		this.setSeed(result);
		return result;
	}

	@Overwrite
	@Deprecated
	public void setLargeFeatureSeed(long seed, int chunkX, int chunkZ) {
		throw Err4.arguments2("WorldgenRandom4#setLargeFeatureSeed");
	}
	@Override
	public void setLargeFeatureSeed(long seed, int chunkX, int chunkZ, int chunkW) {
		this.setSeed(seed);
		long xScale = this.nextLong();
		long zScale = this.nextLong();
		long wScale = this.nextLong();
		long result = chunkX * xScale ^ chunkZ * zScale ^ chunkW * wScale ^ seed;
		this.setSeed(result);
	}

	@Overwrite
	@Deprecated
	public void setLargeFeatureWithSalt(long seed, int x, int z, int blend) {
		throw Err4.arguments2("WorldgenRandom4#setLargeFeatureWithSalt");
	}
	@Override
	public void setLargeFeatureWithSalt(long seed, int x, int z, int w, int blend) {
		// magic values seem to be some random salt, so I just used a randomizer for the third one 🤷
		long result = x * 341873128712L + z * 132897987541L + w * 433369822011L + seed + blend;
		this.setSeed(result);
	}

	@Overwrite
	@Deprecated
	public static RandomSource seedSlimeChunk(int x, int z, long seed, long salt) {
		throw Err4.arguments2("WorldgenRandom4#seedSlimeChunk");
	}
}
