package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeManager4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeManager.class)
class BiomeManagerMixin implements BiomeManager4 {
	@Shadow
	@Final
	private long biomeZoomSeed;

	@Shadow
	private static double getFiddle(long rval) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	private BiomeManager.NoiseBiomeSource noiseBiomeSource;

	/**
	 * @author iluha168
	 * @reason TODO way too coupled with 3D, split into a thousand mixins.
	 */
	@Overwrite
	public Holder<Biome> getBiome(BlockPos pos) {
		if (!(this.noiseBiomeSource instanceof BiomeManager4.NoiseBiomeSource noiseBiomeSource4)) {
			throw Err4.container3();
		}
		int absX = pos.getX() - 2;
		int absY = pos.getY() - 2;
		int absZ = pos.getZ() - 2;
		int absW = Vec4i.getW(pos) - 2;
		int parentX = absX >> 2;
		int parentY = absY >> 2;
		int parentZ = absZ >> 2;
		int parentW = absW >> 2;
		double fractX = (absX & 3) / 4.0;
		double fractY = (absY & 3) / 4.0;
		double fractZ = (absZ & 3) / 4.0;
		double fractW = (absW & 3) / 4.0;
		int minI = 0;
		double minFiddledDistance = Double.POSITIVE_INFINITY;

		for (int i = 0; i < 16; i++) {
			boolean xEven = (i & 4) == 0;
			boolean yEven = (i & 2) == 0;
			boolean zEven = (i & 1) == 0;
			boolean wEven = (i & 8) == 0;
			int cornerX = xEven ? parentX : parentX + 1;
			int cornerY = yEven ? parentY : parentY + 1;
			int cornerZ = zEven ? parentZ : parentZ + 1;
			int cornerW = wEven ? parentW : parentW + 1;
			double distanceX = xEven ? fractX : fractX - 1.0;
			double distanceY = yEven ? fractY : fractY - 1.0;
			double distanceZ = zEven ? fractZ : fractZ - 1.0;
			double distanceW = wEven ? fractW : fractW - 1.0;
			double next = getFiddledDistance(this.biomeZoomSeed, cornerX, cornerY, cornerZ, cornerW, distanceX, distanceY, distanceZ, distanceW);
			if (minFiddledDistance > next) {
				minI = i;
				minFiddledDistance = next;
			}
		}

		int biomeX = (minI & 4) == 0 ? parentX : parentX + 1;
		int biomeY = (minI & 2) == 0 ? parentY : parentY + 1;
		int biomeZ = (minI & 1) == 0 ? parentZ : parentZ + 1;
		int biomeW = (minI & 8) == 0 ? parentW : parentW + 1;
		return noiseBiomeSource4.getNoiseBiome(biomeX, biomeY, biomeZ, biomeW);
	}

	@Overwrite
	@Deprecated
	public Holder<Biome> getNoiseBiomeAtPosition(double x, double y, double z) {
		throw Err4.arguments3("BiomeManager4#getNoiseBiomeAtPosition");
	}
	public Holder<Biome> getNoiseBiomeAtPosition(double x, double y, double z, double w) {
		int quartX = QuartPos.fromBlock(Mth.floor(x));
		int quartY = QuartPos.fromBlock(Mth.floor(y));
		int quartZ = QuartPos.fromBlock(Mth.floor(z));
		int quartW = QuartPos.fromBlock(Mth.floor(w));
		return this.getNoiseBiomeAtQuart(quartX, quartY, quartZ, quartW);
	}

	@Redirect(method = "getNoiseBiomeAtPosition(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/biome/BiomeManager;getNoiseBiomeAtQuart(III)Lnet/minecraft/core/Holder;"
	))
	Holder<Biome> getNoiseBiomeAtPosition(BiomeManager instance, int quartX, int quartY, int quartZ, @Local(argsOnly = true, name = "blockPos") BlockPos blockPos) {
		final int quartW = QuartPos.fromBlock(Vec4i.getW(blockPos));
		return ((BiomeManager4) instance).getNoiseBiomeAtQuart(quartX, quartY, quartZ, quartW);
	}

	@Overwrite
	@Deprecated
	public Holder<Biome> getNoiseBiomeAtQuart(int quartX, int quartY, int quartZ) {
		throw Err4.arguments3("BiomeManager4#getNoiseBiomeAtQuart");
	}
	@Override
	public Holder<Biome> getNoiseBiomeAtQuart(int quartX, int quartY, int quartZ, int quartW) {
		return ((BiomeManager4.NoiseBiomeSource) this.noiseBiomeSource).getNoiseBiome(quartX, quartY, quartZ, quartW);
	}

	@Overwrite
	@Deprecated
	private static double getFiddledDistance(long seed, int xRandom, int yRandom, int zRandom, double distanceX, double distanceY, double distanceZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static double getFiddledDistance(long seed, int xRandom, int yRandom, int zRandom, int wRandom, double distanceX, double distanceY, double distanceZ, double distanceW) {
		long rval = LinearCongruentialGenerator.next(seed, xRandom);
		rval = LinearCongruentialGenerator.next(rval, yRandom);
		rval = LinearCongruentialGenerator.next(rval, zRandom);
		rval = LinearCongruentialGenerator.next(rval, wRandom);
		rval = LinearCongruentialGenerator.next(rval, xRandom);
		rval = LinearCongruentialGenerator.next(rval, yRandom);
		rval = LinearCongruentialGenerator.next(rval, zRandom);
		rval = LinearCongruentialGenerator.next(rval, wRandom);
		double fiddleX = getFiddle(rval);
		rval = LinearCongruentialGenerator.next(rval, seed);
		double fiddleY = getFiddle(rval);
		rval = LinearCongruentialGenerator.next(rval, seed);
		double fiddleZ = getFiddle(rval);
		rval = LinearCongruentialGenerator.next(rval, seed);
		double fiddleW = getFiddle(rval);
		return Mth.square(distanceW + fiddleW)
			 + Mth.square(distanceZ + fiddleZ)
			 + Mth.square(distanceY + fiddleY)
			 + Mth.square(distanceX + fiddleX);
	}
}
