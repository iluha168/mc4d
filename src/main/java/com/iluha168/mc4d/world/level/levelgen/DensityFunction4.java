package com.iluha168.mc4d.world.level.levelgen;

import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Implemented by {@link DensityFunction}.
 */
public interface DensityFunction4 {

	/**
	 * Implemented by the same classes that implement {@link DensityFunction.FunctionContext}.
	 */
	// TODO NoiseChunk
	interface FunctionContext {
		static DensityFunction4.FunctionContext as(DensityFunction.FunctionContext context) {
			return (DensityFunction4.FunctionContext) context;
		}

		int blockW();
	}

	/**
	 * Implemented by {@link DensityFunction.NoiseHolder}.
	 */
	interface NoiseHolder {
		static DensityFunction4.NoiseHolder as(DensityFunction.NoiseHolder noiseHolder) {
			return (DensityFunction4.NoiseHolder) (Object) noiseHolder;
		}

		double getValue(double x, double y, double z, double w);
	}

	/**
	 * Implemented by {@link DensityFunction.SinglePointContext}.
	 */
	interface SinglePointContext {
		void setBlockW(int w);

		static DensityFunction4.SinglePointContext as(DensityFunction.SinglePointContext context) {
			return (DensityFunction4.SinglePointContext) (Object) context;
		}

		static DensityFunction.SinglePointContext of(int blockX, int blockY, int blockZ, int blockW) {
			DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(blockX, blockY, blockZ);
			DensityFunction4.SinglePointContext.as(context).setBlockW(blockW);
			return context;
		}
	}
}
