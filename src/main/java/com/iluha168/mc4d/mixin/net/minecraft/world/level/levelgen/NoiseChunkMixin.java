package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.server.level.ColumnPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.levelgen.DensityFunction4;
import com.iluha168.mc4d.world.level.levelgen.NoiseChunk4;
import com.iluha168.mc4d.world.level.levelgen.blending.Blender4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunk.class)
class NoiseChunkMixin {
	@Shadow
	@Final
	private int cellCountXZ;

	@Shadow
	@Final
	private int noiseSizeXZ;

	@Shadow
	@Final
	private NoiseChunk.FlatCache blendAlpha;

	@Shadow
	@Final
	private NoiseChunk.FlatCache blendOffset;

	@Redirect(method = "forChunk", at = @At(
		value = "NEW",
		target = "(ILnet/minecraft/world/level/levelgen/RandomState;IILnet/minecraft/world/level/levelgen/NoiseSettings;Lnet/minecraft/world/level/levelgen/DensityFunctions$BeardifierOrMarker;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;Lnet/minecraft/world/level/levelgen/blending/Blender;)Lnet/minecraft/world/level/levelgen/NoiseChunk;"
	))
	private static NoiseChunk forChunk(
		int cellCountXZ, RandomState randomState, int chunkMinBlockX, int chunkMinBlockZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifier, NoiseGeneratorSettings settings, Aquifer.FluidPicker globalFluidPicker, Blender blender,
		@Local(name = "pos") ChunkPos pos
	) {
		return new NoiseChunk4(cellCountXZ, randomState, chunkMinBlockX, chunkMinBlockZ, ChunkPos4.as(pos).getMinBlockW(), noiseSettings, beardifier, settings, globalFluidPicker, blender);
	}

	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x = @(0)")
	@Inject(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void init_firstNoiseW(
		int cellCountXZ, RandomState randomState, int chunkMinBlockX, int chunkMinBlockZ, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifier, NoiseGeneratorSettings settings, Aquifer.FluidPicker globalFluidPicker, Blender blender, CallbackInfo ci,
		@Share("firstNoiseW") LocalIntRef firstNoiseW
	) {
		if (!((NoiseChunk) (Object) this instanceof NoiseChunk4 this4)) {
			throw Err4.arguments2("NoiseChunk4::new");
		}
		firstNoiseW.set(this4.firstNoiseW);
	}
	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/blending/Blender;blendOffsetAndFactor(II)Lnet/minecraft/world/level/levelgen/blending/Blender$BlendingOutput;"
	))
	Blender.BlendingOutput init_blendOffsetAndFactor(
		Blender blender, int blockX, int blockZ,
		@Local(name = "x") int x,
		@Local(name = "z") int z,
		@Share("firstNoiseW") LocalIntRef firstNoiseW
	) {
		Blender4 blender4 = (Blender4) blender;
		for (int w = 1; w <= this.noiseSizeXZ; w++) {
			int quartW = firstNoiseW.get() + w;
			int blockW = QuartPos.toBlock(quartW);
			Blender.BlendingOutput blendingOutput = blender4.blendOffsetAndFactor(blockX, blockZ, blockW);
			this.blendAlpha.values[x + (z + w * this.blendAlpha.sizeXZ) * this.blendAlpha.sizeXZ] = blendingOutput.alpha();
			this.blendOffset.values[x + (z + w * this.blendOffset.sizeXZ) * this.blendOffset.sizeXZ] = blendingOutput.blendingOffset();
		}
		// Return value at w = 0 to vanilla
		return blender4.blendOffsetAndFactor(blockX, blockZ, 0);
	}
	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "NEW",
		target = "(II)Lnet/minecraft/world/level/ChunkPos;"
	))
	ChunkPos init_chunkPos(ChunkPos original) {
		ChunkPos4.as(original).setW(this4().chunkW);
		return original;
	}

	@Unique
	private NoiseChunk4 this4() {
		//noinspection DataFlowIssue
		return (NoiseChunk4) (Object) this;
	}

	@ModifyExpressionValue(method = "computePreliminarySurfaceLevel", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/world/level/levelgen/DensityFunction$SinglePointContext;"
	))
	DensityFunction.SinglePointContext computePreliminarySurfaceLevel(DensityFunction.SinglePointContext original, @Local(argsOnly = true, name = "key") long key) {
		DensityFunction4.SinglePointContext.as(original).setBlockW(ColumnPos4.getW(key));
		return original;
	}

	@Definition(id = "cellZIndex", local = @Local(type = int.class, name = "cellZIndex"))
	@Expression("cellZIndex = @(0)")
	@Inject(method = "fillSlice", at = @At("MIXINEXTRAS:EXPRESSION"))
	void fillSlice_resetW(boolean slice0, int cellX, CallbackInfo ci, @Share("cellW") LocalIntRef cellW) {
		cellW.set(0);
	}
	// This does apply properly, IDE is lying
	@Definition(id = "cellZIndex", local = @Local(type = int.class, name = "cellZIndex"))
	@Expression("cellZIndex = cellZIndex + @(1)")
	@ModifyExpressionValue(method = "fillSlice", at = @At("MIXINEXTRAS:EXPRESSION"))
	int fillSlice_incrementW(int one, @Share("cellWIndex") LocalIntRef cellWIndex) {
		cellWIndex.set(cellWIndex.get() + 1);
		if (cellWIndex.get() < this.cellCountXZ + 1) return 0;
		cellWIndex.set(0);
		return 1;
	}
	@Definition(id = "cellZ", local = @Local(type = int.class, name = "cellZ"))
	@Expression("cellZ = @(?)")
	@Inject(method = "fillSlice", at = @At("MIXINEXTRAS:EXPRESSION"))
	void fillSlice_cellStartBlockW(boolean slice0, int cellX, CallbackInfo ci, @Share("cellWIndex") LocalIntRef cellWIndex) {
		NoiseChunk4 self = this4();
		self.cellStartBlockW = (self.firstCellW + cellWIndex.get()) * self.cellWidth;
		self.inCellW = 0;
	}
	@ModifyArg(method = "fillSlice", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/NoiseChunk$NoiseInterpolator;fillArray([DLnet/minecraft/world/level/levelgen/DensityFunction$ContextProvider;)V"
	), index = 0)
	double[] fillSlice_fillArray(
		double[] original,
		@Local(argsOnly = true, name = "slice0") boolean slice0,
		@Local(name = "noiseInterpolator") NoiseChunk.NoiseInterpolator noiseInterpolator,
		@Local(name = "cellZIndex") int cellZIndex,
		@Share("cellWIndex") LocalIntRef cellWIndex
	) {
		NoiseChunk4.NoiseInterpolator noiseInterpolator4 = (NoiseChunk4.NoiseInterpolator) noiseInterpolator;
		return (slice0 ? noiseInterpolator4.slice0() : noiseInterpolator4.slice1())[cellZIndex][cellWIndex.get()];
	}

	@ModifyExpressionValue(method = "forIndex(I)Lnet/minecraft/world/level/levelgen/NoiseChunk;", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;floorMod(II)I",
		ordinal = 0
	))
	int forIndex_zInCell(int wInCell, @Local(argsOnly = true, name = "cellIndex") int cellIndex) {
		NoiseChunk4 self = this4();
		self.inCellW = wInCell;
		return Math.floorMod(Math.floorDiv(cellIndex, self.cellWidth), self.cellWidth);
	}
	@ModifyExpressionValue(method = "forIndex(I)Lnet/minecraft/world/level/levelgen/NoiseChunk;", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;floorDiv(II)I",
		ordinal = 0
	))
	int forIndex_xyIndex(int xyzIndex) {
		return Math.floorDiv(xyzIndex, this4().cellWidth);
	}

	@Definition(id = "zInCell", local = @Local(type = int.class, name = "zInCell"))
	@Expression("zInCell = @(0)")
	@Inject(method = "fillAllDirectly", at = @At("MIXINEXTRAS:EXPRESSION"))
	void fillAllDirectly_w(double[] output, DensityFunction function, CallbackInfo ci) {
		this4().inCellW = 0;
	}
	@Definition(id = "zInCell", local = @Local(type = int.class, name = "zInCell"))
	@Expression("zInCell = zInCell + @(1)")
	@ModifyExpressionValue(method = "fillAllDirectly", at = @At("MIXINEXTRAS:EXPRESSION"))
	int fillAllDirectly_incrementW(int one) {
		NoiseChunk4 self = this4();
		if (++self.inCellW < self.cellWidth) return 0;
		self.inCellW = 0;
		return 1;
	}

	@Overwrite
	@Deprecated
	private Blender.BlendingOutput getOrComputeBlendingOutput(int blockX, int blockZ) {
		throw Err4.arguments2(null);
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.NoiseChunk$BlendAlpha")
	static class BlendAlphaMixin {
		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;getOrComputeBlendingOutput(II)Lnet/minecraft/world/level/levelgen/blending/Blender$BlendingOutput;"
		))
		Blender.BlendingOutput compute(NoiseChunk instance, int blockX, int blockZ, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return ((NoiseChunk4) instance).getOrComputeBlendingOutput(blockX, blockZ, DensityFunction4.FunctionContext.as(context).blockW());
		}
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.NoiseChunk$BlendOffset")
	static class BlendOffsetMixin {
		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;getOrComputeBlendingOutput(II)Lnet/minecraft/world/level/levelgen/blending/Blender$BlendingOutput;"
		))
		Blender.BlendingOutput compute(NoiseChunk instance, int blockX, int blockZ, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return ((NoiseChunk4) instance).getOrComputeBlendingOutput(blockX, blockZ, DensityFunction4.FunctionContext.as(context).blockW());
		}
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.NoiseChunk$Cache2D")
	static class Cache2DMixin {
		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
		))
		long compute(int x, int z, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return ChunkPos4.pack(x, z, DensityFunction4.FunctionContext.as(context).blockW());
		}
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.NoiseChunk$CacheAllInCell")
	static class CacheAllInCellMixin {
		@Shadow
		@Final
		NoiseChunk this$0;

		@Expression("new double[@(?)]")
		@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
		int init(int original) {
			return this$0.cellWidth * original;
		}

		@Definition(id = "z", local = @Local(type = int.class, name = "z"))
		@Expression("z >= 0")
		@ModifyExpressionValue(method = "compute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
		boolean compute_boundsCheck(boolean original) {
			int w = ((NoiseChunk4) this$0).inCellW();
			return original && w >= 0 && w < this$0.cellWidth;
		}
		@Definition(id = "values", field = "Lnet/minecraft/world/level/levelgen/NoiseChunk$CacheAllInCell;values:[D")
		@Expression("this.values[@(?)]")
		@ModifyExpressionValue(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
		int compute_index(int index) {
			int w = ((NoiseChunk4) this$0).inCellW();
			return index * this$0.cellWidth + w;
		}
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.NoiseChunk$FlatCache")
	static class FlatCacheMixin {
		@Shadow
		@Final
		NoiseChunk this$0;

		@Shadow
		@Final
		public int sizeXZ;

		@Expression("new double[@(?)]")
		@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
		int init_values(int original) {
			return original * this.sizeXZ;
		}
		@Definition(id = "z", local = @Local(type = int.class, name = "z"))
		@Expression("z = z + @(1)")
		@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
		int init_incrementW(int one, @Share("w") LocalIntRef w) {
			w.set(w.get() + 1);
			if (w.get() < this.sizeXZ) return 0;
			w.set(0);
			return 1;
		}
		@Definition(id = "values", field = "Lnet/minecraft/world/level/levelgen/NoiseChunk$FlatCache;values:[D")
		@Expression("this.values[@(?)] = ?")
		@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
		int init_index(int index, @Share("w") LocalIntRef w) {
			return index + w.get() * this.sizeXZ * this.sizeXZ;
		}
		@ModifyExpressionValue(method = "<init>", at = @At(
			value = "NEW",
			target = "(III)Lnet/minecraft/world/level/levelgen/DensityFunction$SinglePointContext;"
		))
		DensityFunction.SinglePointContext init_singlePointContext(DensityFunction.SinglePointContext original, @Share("w") LocalIntRef w) {
			int quartW = ((NoiseChunk4) this$0).firstNoiseW + w.get();
			DensityFunction4.SinglePointContext.as(original).setBlockW(QuartPos.toBlock(quartW));
			return original;
		}

		@Definition(id = "z", local = @Local(type = int.class, name = "z"))
		@Expression("z >= 0")
		@ModifyExpressionValue(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
		boolean compute_boundsCheck(boolean original, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			int w = QuartPos.fromBlock(DensityFunction4.FunctionContext.as(context).blockW()) - ((NoiseChunk4) this$0).firstNoiseW;
			return original && w >= 0 && w < this.sizeXZ;
		}
		@Definition(id = "values", field = "Lnet/minecraft/world/level/levelgen/NoiseChunk$FlatCache;values:[D")
		@Expression("this.values[@(?)]")
		@ModifyExpressionValue(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
		int compute_index(int index, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			int w = QuartPos.fromBlock(DensityFunction4.FunctionContext.as(context).blockW()) - ((NoiseChunk4) this$0).firstNoiseW;
			return index + w * this.sizeXZ * this.sizeXZ;
		}
	}

	@Mixin(targets = "net.minecraft.world.level.levelgen.NoiseChunk$NoiseInterpolator")
	static class NoiseInterpolatorMixin implements NoiseChunk4.NoiseInterpolator {
		@Shadow
		@Final
		NoiseChunk this$0;

		@Shadow
		private double noise000;
		@Shadow
		private double noise001;
		@Shadow
		private double noise100;
		@Shadow
		private double noise101;
		@Shadow
		private double noise010;
		@Shadow
		private double noise011;
		@Shadow
		private double noise110;
		@Shadow
		private double noise111;

		@Shadow
		private double value;

		@Unique
		double[][][] slice0, slice1;
		@Unique
		double noise0001, noise0011, noise1001, noise1011, noise0101, noise0111, noise1101, noise1111;
		@Unique
		double valueXZ001, valueXZ101, valueXZ011, valueXZ111;
		@Unique
		double valueZ01, valueZ11;
		@Unique
		double value1;
		@Unique
		double value4;

		@Override
		public double[][][] slice0() {
			return this.slice0;
		}
		@Override
		public double[][][] slice1() {
			return this.slice1;
		}

		@Redirect(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseChunk$NoiseInterpolator;allocateSlice(II)[[D",
			ordinal = 0
		))
		double[][] init_slice0(NoiseChunk.NoiseInterpolator instance, int cellCountY, int cellCountZ) {
			this.slice0 = this.allocateSlice(cellCountY, cellCountZ, cellCountZ);
			return new double[0][];
		}
		@Redirect(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseChunk$NoiseInterpolator;allocateSlice(II)[[D",
			ordinal = 1
		))
		double[][] init_slice1(NoiseChunk.NoiseInterpolator instance, int cellCountY, int cellCountZ) {
			this.slice1 = this.allocateSlice(cellCountY, cellCountZ, cellCountZ);
			return new double[0][];
		}

		@Overwrite
		@Deprecated
		private double[][] allocateSlice(int cellCountY, int cellCountZ) {
			throw Err4.arguments2(null);
		}
		@Unique
		double[][][] allocateSlice(int cellCountY, int cellCountZ, int cellCountW) {
			return new double[cellCountZ + 1][cellCountW + 1][cellCountY + 1];
		}

		@Overwrite
		@Deprecated
		private void selectCellYZ(int cellYIndex, int cellZIndex) {
			throw Err4.arguments2(null);
		}
		@Override
		public void selectCellYZW(int cellYIndex, int cellZIndex, int cellWIndex) {
			this.noise000  = this.slice0[cellZIndex]    [cellWIndex]    [cellYIndex    ];
			this.noise001  = this.slice0[cellZIndex + 1][cellWIndex]    [cellYIndex    ];
			this.noise100  = this.slice1[cellZIndex]    [cellWIndex]    [cellYIndex    ];
			this.noise101  = this.slice1[cellZIndex + 1][cellWIndex]    [cellYIndex    ];
			this.noise010  = this.slice0[cellZIndex]    [cellWIndex]    [cellYIndex + 1];
			this.noise011  = this.slice0[cellZIndex + 1][cellWIndex]    [cellYIndex + 1];
			this.noise110  = this.slice1[cellZIndex]    [cellWIndex]    [cellYIndex + 1];
			this.noise111  = this.slice1[cellZIndex + 1][cellWIndex]    [cellYIndex + 1];
			this.noise0001 = this.slice0[cellZIndex]    [cellWIndex + 1][cellYIndex    ];
			this.noise0011 = this.slice0[cellZIndex + 1][cellWIndex + 1][cellYIndex    ];
			this.noise1001 = this.slice1[cellZIndex]    [cellWIndex + 1][cellYIndex    ];
			this.noise1011 = this.slice1[cellZIndex + 1][cellWIndex + 1][cellYIndex    ];
			this.noise0101 = this.slice0[cellZIndex]    [cellWIndex + 1][cellYIndex + 1];
			this.noise0111 = this.slice0[cellZIndex + 1][cellWIndex + 1][cellYIndex + 1];
			this.noise1101 = this.slice1[cellZIndex]    [cellWIndex + 1][cellYIndex + 1];
			this.noise1111 = this.slice1[cellZIndex + 1][cellWIndex + 1][cellYIndex + 1];
		}

		@Inject(method = "updateForY", at = @At("TAIL"))
		void updateForY_w(double factorY, CallbackInfo ci) {
			this.valueXZ001 = Mth.lerp(factorY, this.noise0001, this.noise0101);
			this.valueXZ101 = Mth.lerp(factorY, this.noise1001, this.noise1101);
			this.valueXZ011 = Mth.lerp(factorY, this.noise0011, this.noise0111);
			this.valueXZ111 = Mth.lerp(factorY, this.noise1011, this.noise1111);
		}

		@Inject(method = "updateForX", at = @At("TAIL"))
		void updateForX_w(double factorX, CallbackInfo ci) {
			this.valueZ01 = Mth.lerp(factorX, this.valueXZ001, this.valueXZ101);
			this.valueZ11 = Mth.lerp(factorX, this.valueXZ011, this.valueXZ111);
		}

		@Inject(method = "updateForZ", at = @At("TAIL"))
		void updateForZ_w(double factorZ, CallbackInfo ci) {
			this.value1 = Mth.lerp(factorZ, this.valueZ01, this.valueZ11);
		}

		@Override
		public void updateForW(double factorW) {
			this.value4 = Mth.lerp(factorW, this.value, this.value1);
		}

		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/Mth;lerp3(DDDDDDDDDDD)D"
		))
		double compute_lerp4(
			double alpha1, double alpha2, double alpha3,
			double x000, double x100, double x010, double x110, double x001, double x101, double x011, double x111
		) {
			double alpha4 = (double) ((NoiseChunk4) this$0).inCellW / this$0.cellWidth;
			return MathHelpers.lerp4(
				alpha1, alpha2, alpha3, alpha4,
				x000, x100, x010, x110, x001, x101, x011, x111,
				this.noise0001, this.noise1001, this.noise0101, this.noise1101, this.noise0011, this.noise1011, this.noise0111, this.noise1111
			);
		}
		@Definition(id = "value", field = "Lnet/minecraft/world/level/levelgen/NoiseChunk$NoiseInterpolator;value:D")
		@Expression("this.value")
		@ModifyExpressionValue(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
		double compute_value(double original) {
			return this.value4;
		}

		@Inject(method = "swapSlices", at = @At("TAIL"))
		void swapSlices_w(CallbackInfo ci) {
			double[][][] tmp = this.slice0;
			this.slice0 = this.slice1;
			this.slice1 = tmp;
		}
	}
}
