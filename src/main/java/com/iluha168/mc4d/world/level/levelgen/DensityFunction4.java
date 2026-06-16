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
		static DensityFunction4.FunctionContext as(DensityFunction.SinglePointContext context) {
			return (DensityFunction4.FunctionContext) (Object) context;
		}

		int blockW();
		void setBlockW(int w);
	}

	// TODO NoiseHolder

	/**
	 * Implemented by {@link DensityFunction.SinglePointContext}.
	 */
	interface SinglePointContext {
		static DensityFunction.SinglePointContext of(int blockX, int blockY, int blockZ, int blockW) {
			DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(blockX, blockY, blockZ);
			DensityFunction4.FunctionContext.as(context).setBlockW(blockW);
			return context;
		}
	}
}
