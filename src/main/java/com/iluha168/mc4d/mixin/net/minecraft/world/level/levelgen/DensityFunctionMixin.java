package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.DensityFunction4;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DensityFunction.class)
interface DensityFunctionMixin extends DensityFunction4 {
	// TODO NoiseHolder

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
