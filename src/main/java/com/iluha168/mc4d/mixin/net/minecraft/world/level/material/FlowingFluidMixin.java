package com.iluha168.mc4d.mixin.net.minecraft.world.level.material;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
class FlowingFluidMixin extends FluidMixin {
	@Inject(method = "getFlow", at = @At("HEAD"))
	void getFlow_flowW(BlockGetter level, BlockPos pos, FluidState fluidState, CallbackInfoReturnable<Vec3> cir, @Share("flowW") LocalDoubleRef flowW) {
		flowW.set(0.0);
	}
	@Definition(id = "flowX", local = @Local(type = double.class, name = "flowX"))
	@Expression("flowX = flowX + ?")
	@Inject(method = "getFlow", at = @At("MIXINEXTRAS:EXPRESSION"))
	void getFlow_addFlowW(
		BlockGetter level, BlockPos pos, FluidState fluidState, CallbackInfoReturnable<Vec3> cir,
		@Share("flowW") LocalDoubleRef flowW,
		@Local(name = "direction") Direction direction,
		@Local(name = "distance") float distance
	) {
		flowW.set(flowW.get() + Direction4.as(direction).getStepW() * distance);
	}
	@Redirect(method = "getFlow", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getFlow_flow(double x, double y, double z, @Share("flowW") LocalDoubleRef flowW) {
		return new Vec4(x, y, z, flowW.get());
	}
	@Redirect(method = "getFlow", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getFlow_add(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).add(x, y, z, z);
	}

	@Redirect(method = "lambda$getShape$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/Shapes;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape getShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return Shapes4.box(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	@Mixin(FlowingFluid.SpreadContext.class)
	static class SpreadContextMixin {
		@Shadow
		@Final
		private BlockPos origin;

		@Shadow
		@Final
		private BlockGetter level;

		@Shadow
		@Final
		FlowingFluid this$0;

		@Unique private Int2ObjectMap<BlockState> stateCache;
		@Unique private Int2BooleanMap holeCache;

		@Inject(method = "<init>", at = @At("TAIL"))
		void init(FlowingFluid this$0, BlockGetter level, BlockPos origin, CallbackInfo ci) {
			this.stateCache = new Int2ObjectOpenHashMap<>();
			this.holeCache = new Int2BooleanOpenHashMap();
		}

		@Redirect(method = "getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/material/FlowingFluid$SpreadContext;getCacheKey(Lnet/minecraft/core/BlockPos;)S"
		))
		short getBlockState_getCacheKey(FlowingFluid.SpreadContext instance, BlockPos pos) {
			return 0;
		}

		@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/material/FlowingFluid$SpreadContext;getBlockState(Lnet/minecraft/core/BlockPos;S)Lnet/minecraft/world/level/block/state/BlockState;")
		@Expression("this.getBlockState(?, ?)")
		@Redirect(method = "getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("MIXINEXTRAS:EXPRESSION"))
		BlockState getBlockState(FlowingFluid.SpreadContext instance, BlockPos pos, short key) {
			return this.getBlockState(pos, this.getCacheKeyInt(pos));
		}

		@Overwrite
		@Deprecated
		private BlockState getBlockState(BlockPos pos, short key) {
			throw Err4.container3();
		}
		@Unique
		private BlockState getBlockState(BlockPos pos, int key) {
			return this.stateCache.computeIfAbsent(key, _ -> this.level.getBlockState(pos));
		}

		@Redirect(method = "isHole", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/material/FlowingFluid$SpreadContext;getCacheKey(Lnet/minecraft/core/BlockPos;)S"
		))
		short isHole_getCacheKey(FlowingFluid.SpreadContext instance, BlockPos pos) {
			return 0;
		}
		@Redirect(method = "isHole", at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/shorts/Short2BooleanMap;computeIfAbsent(SLit/unimi/dsi/fastutil/shorts/Short2BooleanFunction;)Z"
		))
		boolean isHole_computeIfAbsent(Short2BooleanMap instance, short key, Short2BooleanFunction mappingFunction, @Local(argsOnly = true, name = "pos") BlockPos pos) {
			return this.holeCache.computeIfAbsent(this.getCacheKeyInt(pos), keyInt -> {
				BlockState state = this.getBlockState(pos, keyInt);
				BlockPos below = pos.below();
				BlockState belowState = this.level.getBlockState(below);
				return this$0.isWaterHole(this.level, pos, state, below, belowState);
			});
		}

		@Overwrite
		@Deprecated
		private short getCacheKey(BlockPos pos) {
			throw Err4.return3(null);
		}
		@Unique
		private int getCacheKeyInt(BlockPos pos) {
			final int relativeX = pos.getX() - this.origin.getX();
			final int relativeZ = pos.getZ() - this.origin.getZ();
			final int relativeW = Vec4i.getW(pos) - Vec4i.getW(this.origin);
			return relativeX + 128 & 0xFF
				| (relativeZ + 128 & 0xFF) << 8
				| (relativeW + 128 & 0xFF) << 16;
		}
	}
}
