package com.iluha168.mc4d.world.level.levelgen;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.NonNull;

/**
 * Implemented by {@link DensityFunctions}.
 */
public interface DensityFunctions4 {
	static DensityFunction shiftedNoise2d(DensityFunction shiftX, DensityFunction shiftZ, DensityFunction shiftW, double xzScale, Holder<NormalNoise.NoiseParameters> noiseData) {
		return DensityFunctions4.ShiftedNoise.from(shiftX, DensityFunctions.zero(), shiftZ, shiftW, xzScale, 0.0, new DensityFunction.NoiseHolder(noiseData));
	}

	static DensityFunction shiftC(Holder<NormalNoise.NoiseParameters> noiseData) {
		return new DensityFunctions4.ShiftC(new DensityFunction.NoiseHolder(noiseData));
	}

	record ShiftC(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
		public static final KeyDispatchDataCodec<DensityFunctions4.ShiftC> CODEC = DensityFunctions.singleArgumentCodec(
			DensityFunction.NoiseHolder.CODEC, DensityFunctions4.ShiftC::new, DensityFunctions4.ShiftC::offsetNoise
		);

		@Override
		@Deprecated
		public double compute(double localX, double localY, double localZ) {
			throw Err4.arguments3("DensityFunctions4.ShiftNoise#compute");
		}

		@Override
		public double compute(DensityFunction.@NonNull FunctionContext context) {
			return DensityFunctions4.ShiftNoise.as(this).compute(
				DensityFunction4.FunctionContext.as(context).blockW(),
				context.blockX(),
				context.blockY(),
				context.blockZ()
			);
		}

		@Override
		public @NonNull DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(new DensityFunctions4.ShiftC(visitor.visitNoise(this.offsetNoise)));
		}

		@Override
		public @NonNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
			return CODEC;
		}
	}

	/**
	 * Implemented by {@link DensityFunctions.ShiftNoise}
	 */
	interface ShiftNoise {
		static DensityFunctions4.ShiftNoise as(DensityFunctions.ShiftNoise shiftNoise) {
			return (DensityFunctions4.ShiftNoise) shiftNoise;
		}

		double compute(double localX, double localY, double localZ, double localW);
	}

	/**
	 * Implemented by {@link DensityFunctions.ShiftedNoise}
	 */
	interface ShiftedNoise {
		static DensityFunctions4.ShiftedNoise as(DensityFunctions.ShiftedNoise shiftedNoise) {
			return (DensityFunctions4.ShiftedNoise) (Object) shiftedNoise;
		}

		static DensityFunctions.ShiftedNoise from(
			DensityFunction shiftX,
			DensityFunction shiftY,
			DensityFunction shiftZ,
			DensityFunction shiftW,
			double xzScale, double yScale,
			DensityFunction.NoiseHolder noise
		) {
			DensityFunctions.ShiftedNoise shiftedNoise = new DensityFunctions.ShiftedNoise(shiftX, shiftY, shiftZ, xzScale, yScale, noise);
			DensityFunctions4.ShiftedNoise.as(shiftedNoise).setShiftW(shiftW);
			return shiftedNoise;
		}

		@NonNull DensityFunction shiftW();
		void setShiftW(@NonNull DensityFunction shiftW);
	}
}
