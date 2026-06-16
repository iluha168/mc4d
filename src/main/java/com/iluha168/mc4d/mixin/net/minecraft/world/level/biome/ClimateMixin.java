package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.Climate4;
import com.iluha168.mc4d.world.level.levelgen.DensityFunction4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Climate.class)
abstract class ClimateMixin implements Climate4 {

	@Mixin(Climate.Sampler.class)
	abstract static class SamplerMixin implements Climate4.Sampler {
		@Shadow @Final private DensityFunction temperature;
		@Shadow @Final private DensityFunction humidity;
		@Shadow @Final private DensityFunction continentalness;
		@Shadow @Final private DensityFunction erosion;
		@Shadow @Final private DensityFunction depth;
		@Shadow @Final private DensityFunction weirdness;

		@Overwrite
		@Deprecated
		public Climate.TargetPoint sample(int quartX, int quartY, int quartZ) {
			throw Err4.arguments3("Climate4.Sampler#sample");
		}
		@Override
		public Climate.TargetPoint sample(int quartX, int quartY, int quartZ, int quartW) {
			int blockX = QuartPos.toBlock(quartX);
			int blockY = QuartPos.toBlock(quartY);
			int blockZ = QuartPos.toBlock(quartZ);
			int blockW = QuartPos.toBlock(quartW);
			DensityFunction.SinglePointContext context = DensityFunction4.SinglePointContext.of(blockX, blockY, blockZ, blockW);
			return Climate.target(
				(float)this.temperature.compute(context),
				(float)this.humidity.compute(context),
				(float)this.continentalness.compute(context),
				(float)this.erosion.compute(context),
				(float)this.depth.compute(context),
				(float)this.weirdness.compute(context)
			);
		}
	}

	@Mixin(Climate.SpawnFinder.class)
	static class SpawnFinderMixin {
		@Shadow private Climate.SpawnFinder.Result result;

		@Redirect(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/biome/Climate$SpawnFinder;getSpawnPositionAndFitness(Ljava/util/List;Lnet/minecraft/world/level/biome/Climate$Sampler;II)Lnet/minecraft/world/level/biome/Climate$SpawnFinder$Result;"
		))
		private Climate.SpawnFinder.Result init(List<Climate.ParameterPoint> targetClimates, Climate.Sampler sampler, int blockX, int blockZ) {
			return getSpawnPositionAndFitness(targetClimates, sampler, blockX, blockZ, blockZ);
		}

		// TODO perhaps can be done with a lot of mixins
		@Overwrite
		private void radialSearch(List<Climate.ParameterPoint> targetClimates, Climate.Sampler sampler, float maxRadius, float radiusIncrement) {
			float theta = 0.0F;
			float phi = -(float)(Math.PI / 2);
			float radius = radiusIncrement;
			BlockPos searchOrigin = this.result.location();
			int originW = Vec4i.getW(searchOrigin);

			while (radius <= maxRadius) {
				double horizontal = Math.cos(phi) * radius;
				int x = searchOrigin.getX() + (int)(Math.sin(theta) * horizontal);
				int z = searchOrigin.getZ() + (int)(Math.cos(theta) * horizontal);
				int w = originW + (int)(Math.sin(phi) * radius);
				Climate.SpawnFinder.Result candidate = getSpawnPositionAndFitness(targetClimates, sampler, x, z, w);
				if (candidate.fitness() < this.result.fitness()) {
					this.result = candidate;
				}

				theta += radiusIncrement / radius;
				if (theta > Math.PI * 2) {
					theta = 0.0F;
					phi += radiusIncrement / radius;
					if (phi > Math.PI / 2) {
						phi = -(float)(Math.PI / 2);
						radius += radiusIncrement;
					}
				}
			}
		}

		@Overwrite
		@Deprecated
		private static Climate.SpawnFinder.Result getSpawnPositionAndFitness(List<Climate.ParameterPoint> targetClimates, Climate.Sampler sampler, int blockX, int blockZ) {
			throw Err4.arguments2(null);
		}
		@Unique
		private static Climate.SpawnFinder.Result getSpawnPositionAndFitness(List<Climate.ParameterPoint> targetClimates, Climate.Sampler sampler, int blockX, int blockZ, int blockW) {
			Climate.TargetPoint targetPoint = ((Climate4.Sampler) (Object) sampler).sample(QuartPos.fromBlock(blockX), 0, QuartPos.fromBlock(blockZ), QuartPos.fromBlock(blockW));
			Climate.TargetPoint zeroDepthTargetPoint = new Climate.TargetPoint(
				targetPoint.temperature(), targetPoint.humidity(), targetPoint.continentalness(), targetPoint.erosion(), 0L, targetPoint.weirdness()
			);
			long minFitness = Long.MAX_VALUE;

			for (Climate.ParameterPoint point : targetClimates) {
				minFitness = Math.min(minFitness, point.fitness(zeroDepthTargetPoint));
			}

			long distanceBiasToWorldOrigin = Mth.square((long)blockX) + Mth.square((long)blockZ) + Mth.square((long)blockW);
			long fitnessWithDistance = minFitness * Mth.square(2048L) + distanceBiasToWorldOrigin;
			return new Climate.SpawnFinder.Result(BlockPos4.from(blockX, 0, blockZ, blockW), fitnessWithDistance);
		}
	}
}
