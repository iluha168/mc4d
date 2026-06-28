package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.DensityFunction4;
import com.iluha168.mc4d.world.level.levelgen.synth.NormalNoise4;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DensityFunction.class)
interface DensityFunctionMixin extends DensityFunction4 {
	@Mixin(DensityFunction.NoiseHolder.class)
	class NoiseHolderMixin implements DensityFunction4.NoiseHolder {
		@Shadow
		@Final
		private @Nullable NormalNoise noise;

		@Overwrite
		@Deprecated
		public double getValue(double x, double y, double z) {
			throw Err4.arguments3("DensityFunction4.NoiseHolder#getValue");
		}
		@Override
		public double getValue(double x, double y, double z, double w) {
			return this.noise == null ? 0.0 : ((NormalNoise4) this.noise).getValue(x, y, z, w);
		}
	}

	@Mixin(DensityFunction.SinglePointContext.class)
	class SinglePointContextMixin implements DensityFunction4.SinglePointContext, DensityFunction4.FunctionContext {
		@Unique private int blockW;
		@Unique	private boolean blockWNotSet;

		@Override
		public int blockW() {
			if (this.blockWNotSet) {
				throw Err4.field4missing("w");
			}
			return this.blockW;
		}
		@Override
		public void setBlockW(int w) {
			this.blockW = w;
			this.blockWNotSet = false;
		}

		@Inject(method = "<init>", at = @At("TAIL"))
		void init(int blockX, int blockY, int blockZ, CallbackInfo ci) {
			this.blockWNotSet = true;
		}
	}
}
