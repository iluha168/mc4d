package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.core.AxisCycle4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(AxisCycle.class)
enum AxisCycleMixin implements AxisCycle4 {
	// 4D allows for an axis shift by 2. Axis shifts by 1 are already in vanilla.
	TRANSPOSE {
		@Override
		public int cycle(int x, int y, int z, Direction.Axis axis) {
			throw Err4.arguments3("AxisCycle4#cycle");
		}

		@Override
		public double cycle(double x, double y, double z, Direction.Axis axis) {
			throw Err4.arguments3("AxisCycle4#cycle");
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AxisCycle.AXIS_VALUES[Math.floorMod(axis.ordinal() + 2, AxisCycle.AXIS_VALUES.length)];
		}

		@Override
		public int cycle(int x, int y, int z, int w, Direction.Axis axis) {
			return Direction4.Axis.as(axis).choose(z, w, x, y);
		}

		@Override
		public double cycle(double x, double y, double z, double w, Direction.Axis axis) {
			return Direction4.Axis.as(axis).choose(z, w, x, y);
		}

		@Override
		public AxisCycle inverse() {
			return AxisCycle4.TRANSPOSE;
		}
	};

	@Mixin(targets = {
		"net/minecraft/core/AxisCycle$1",
		"net/minecraft/core/AxisCycle$2",
		"net/minecraft/core/AxisCycle$3",
	})
	static class AxisCycleMembersMixin {
		@Inject(method = "cycle(IIILnet/minecraft/core/Direction$Axis;)I", at = @At("HEAD"))
		void cycle(int x, int y, int z, Direction.Axis axis, CallbackInfoReturnable<Integer> cir)  {
			throw Err4.arguments3("AxisCycle4#cycle");
		}

		@Inject(method = "cycle(DDDLnet/minecraft/core/Direction$Axis;)D", at = @At("HEAD"))
		void cycle(double x, double y, double z, Direction.Axis axis, CallbackInfoReturnable<Double> cir)  {
			throw Err4.arguments3("AxisCycle4#cycle");
		}
	}

	@Mixin(targets = "net/minecraft/core/AxisCycle$1")
	static class NoneAxisCycleMixin implements AxisCycle4 {
		@Override
		public int cycle(int x, int y, int z, int w, Direction.Axis axis) {
			return Direction4.Axis.as(axis).choose(x, y, z, w);
		}

		@Override
		public double cycle(double x, double y, double z, double w, Direction.Axis axis) {
			return Direction4.Axis.as(axis).choose(x, y, z, w);
		}
	}

	@Mixin(targets = "net/minecraft/core/AxisCycle$2")
	static class ForwardAxisCycleMixin implements AxisCycle4 {
		@ModifyConstant(method = "cycle(Lnet/minecraft/core/Direction$Axis;)Lnet/minecraft/core/Direction$Axis;", constant = @Constant(intValue = 3))
		int dimensionNumber(int constant) {
			return AxisCycle.AXIS_VALUES.length;
		}

		@Override
		public int cycle(int x, int y, int z, int w, Direction.Axis axis) {
			//noinspection SuspiciousNameCombination
			return Direction4.Axis.as(axis).choose(w, x, y, z);
		}

		@Override
		public double cycle(double x, double y, double z, double w, Direction.Axis axis) {
			//noinspection SuspiciousNameCombination
			return Direction4.Axis.as(axis).choose(w, x, y, z);
		}
	}

	@Mixin(targets = "net/minecraft/core/AxisCycle$3")
	static class BackwardAxisCycleMixin implements AxisCycle4 {
		@ModifyConstant(method = "cycle(Lnet/minecraft/core/Direction$Axis;)Lnet/minecraft/core/Direction$Axis;", constant = @Constant(intValue = 3))
		int dimensionNumber(int constant) {
			return AxisCycle.AXIS_VALUES.length;
		}

		@Override
		public int cycle(int x, int y, int z, int w, Direction.Axis axis) {
			//noinspection SuspiciousNameCombination
			return Direction4.Axis.as(axis).choose(y, z, w, x);
		}

		@Override
		public double cycle(double x, double y, double z, double w, Direction.Axis axis) {
			//noinspection SuspiciousNameCombination
			return Direction4.Axis.as(axis).choose(y, z, w, x);
		}
	}

	@Shadow
	public abstract int cycle(int x, int y, int z, Direction.Axis axis);

	@Shadow
	public abstract double cycle(double x, double y, double z, Direction.Axis axis);

	@Shadow
	public abstract Direction.Axis cycle(Direction.Axis axis);

	@Shadow
	public abstract AxisCycle inverse();

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/AxisCycle;values()[Lnet/minecraft/core/AxisCycle;"
	))
	private static AxisCycle[] VALUES() {
		return new AxisCycle[] { AxisCycle.NONE, AxisCycle.FORWARD, AxisCycle4.TRANSPOSE, AxisCycle.BACKWARD };
	}

	@ModifyConstant(method = "between", constant = @Constant(intValue = 3))
	private static int between(int constant) {
		return 4;
	}
}
