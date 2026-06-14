package com.iluha168.mc4d.world.attribute;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.GaussianSampler;

public class GaussianSampler4 extends GaussianSampler {
	public static <V> void sample(Vec4 position, GaussianSampler4.Sampler<V> sampler, GaussianSampler.Accumulator<V> accumulator) {
		position = position.subtract(0.5, 0.5, 0.5, 0.5);
		int integralX = Mth.floor(position.x());
		int integralY = Mth.floor(position.y());
		int integralZ = Mth.floor(position.z());
		int integralW = Mth.floor(position.w());

		double relativeX = position.x() - integralX;
		double relativeY = position.y() - integralY;
		double relativeZ = position.z() - integralZ;
		double relativeW = position.w() - integralW;

		for (int w = 0; w < GAUSSIAN_SAMPLE_BREADTH; w++) {
			double weightW = Mth.lerp(relativeW, GAUSSIAN_SAMPLE_KERNEL[w + 1], GAUSSIAN_SAMPLE_KERNEL[w]);
			int sampleW = integralW - GAUSSIAN_SAMPLE_RADIUS + w;

			for (int z = 0; z < GAUSSIAN_SAMPLE_BREADTH; z++) {
				double weightZ = Mth.lerp(relativeZ, GAUSSIAN_SAMPLE_KERNEL[z + 1], GAUSSIAN_SAMPLE_KERNEL[z]);
				int sampleZ = integralZ - GAUSSIAN_SAMPLE_RADIUS + z;

				for (int x = 0; x < GAUSSIAN_SAMPLE_BREADTH; x++) {
					double weightX = Mth.lerp(relativeX, GAUSSIAN_SAMPLE_KERNEL[x + 1], GAUSSIAN_SAMPLE_KERNEL[x]);
					int sampleX = integralX - GAUSSIAN_SAMPLE_RADIUS + x;

					for (int y = 0; y < GAUSSIAN_SAMPLE_BREADTH; y++) {
						double weightY = Mth.lerp(relativeY, GAUSSIAN_SAMPLE_KERNEL[y + 1], GAUSSIAN_SAMPLE_KERNEL[y]);
						int sampleY = integralY - GAUSSIAN_SAMPLE_RADIUS + y;

						double sampleWeight = weightX * weightY * weightZ * weightW;
						V value = sampler.get(sampleX, sampleY, sampleZ, sampleW);
						accumulator.accumulate(sampleWeight, value);
					}
				}
			}
		}
	}

	@FunctionalInterface
	public interface Sampler<V> {
		V get(int x, int y, int z, int w);
	}
}
