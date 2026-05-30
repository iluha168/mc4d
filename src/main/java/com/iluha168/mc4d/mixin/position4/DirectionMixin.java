package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4i;
import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(Direction.class)
public enum DirectionMixin implements Direction4 {
	KATA(6, 7, 4, "kata", Direction.AxisDirection.NEGATIVE, Direction4.Axis.W, new Vec4i(0, 0, 0, -1)),
	ANA (7, 6, 5, "ana" , Direction.AxisDirection.POSITIVE, Direction4.Axis.W, new Vec4i(0, 0, 0, 1));

	@Shadow
	DirectionMixin(
		int data3d,
		int oppositeIndex,
		/* TODO Represents rotation around the Y axis. Makes no sense in 4D */ int data2d,
		String name,
		Direction.AxisDirection axisDirection,
		Direction.Axis axis,
		Vec3i normal
	) {}

	@Shadow
	@Final
	private Vec3i normal;

	// TODO orderedByNearest
	// TODO makeDirectionArray
	// TODO rotate
	// TODO getYRot
	// TODO getRotation
	// TODO getFacingAxis
	// TODO getClockWise
	// TODO getCounterClockWise

	@WrapMethod(method = "getClockWise()Lnet/minecraft/core/Direction;")
	Direction getClockWiseYW(Operation<Direction> original) {
		Direction This = (Direction) (Object) this;
		return This == Direction4.KATA || This == Direction4.ANA ? This : original.call();
	}

	// TODO getClockWiseX
	// TODO getCounterClockWiseX
	// TODO getClockWiseZ
	// TODO getCounterClockWiseZ

	@WrapMethod(method = "getCounterClockWise()Lnet/minecraft/core/Direction;")
	Direction getCounterClockWiseYW(Operation<Direction> original) {
		Direction This = (Direction) (Object) this;
		return This == Direction4.KATA || This == Direction4.ANA ? This : original.call();
	}

	@Override
	public int getStepW() {
		return ((Position4i) this.normal).getZ();
	}

	// TODO step

	@WrapMethod(method = "fromAxisAndDirection")
	private static Direction fromAxisAndDirection4(Direction.Axis axis, Direction.AxisDirection direction, Operation<Direction> original) {
		return axis == Direction4.Axis.W
			? (direction == Direction.AxisDirection.POSITIVE ? Direction4.ANA : Direction4.KATA)
			: original.call(axis, direction);
	}

	// TODO getApproximateNearest
	// TODO getApproximateNearest
	// TODO getApproximateNearest
	// TODO getNearest
	// TODO getNearest
	// TODO axisStepOrder
	// TODO getUnitVec3
	// TODO getUnitVec3f
	// TODO isFacingAngle

	@Mixin(Direction.Axis.class)
	enum AxisMixin implements Direction4.Axis {
		W("w") {
			@Override
			public Direction getPositive() {
				return Direction4.ANA;
			}

			@Override
			public Direction getNegative() {
				return Direction4.KATA;
			}

			@Override
			public int choose(int x, int y, int z) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: choose"));
			}

			@Override
			public double choose(double x, double y, double z) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: choose"));
			}

			@Override
			public boolean choose(boolean x, boolean y, boolean z) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: choose"));
			}

			@Override
			public int choose(int x, int y, int z, int w) {
				return w;
			}

			@Override
			public double choose(double x, double y, double z, double w) {
				return w;
			}

			@Override
			public boolean choose(boolean x, boolean y, boolean z, boolean w) {
				return w;
			}
		};

		@Mixin(targets = "net/minecraft/core/Direction$Axis$1")
		static class XAxisMixin implements Direction4.Axis {
			@Override
			public int choose(int x, int y, int z, int w) {
				return x;
			}

			@Override
			public double choose(double x, double y, double z, double w) {
				return x;
			}

			@Override
			public boolean choose(boolean x, boolean y, boolean z, boolean w) {
				return x;
			}
		}

		@Mixin(targets = "net/minecraft/core/Direction$Axis$2")
		static class YAxisMixin implements Direction4.Axis {
			@Override
			public int choose(int x, int y, int z, int w) {
				return y;
			}

			@Override
			public double choose(double x, double y, double z, double w) {
				return y;
			}

			@Override
			public boolean choose(boolean x, boolean y, boolean z, boolean w) {
				return y;
			}
		}

		@Mixin(targets = "net/minecraft/core/Direction$Axis$3")
		static class ZAxisMixin implements Direction4.Axis {
			@Override
			public int choose(int x, int y, int z, int w) {
				return z;
			}

			@Override
			public double choose(double x, double y, double z, double w) {
				return z;
			}

			@Override
			public boolean choose(boolean x, boolean y, boolean z, boolean w) {
				return z;
			}
		}

		@Mixin(targets = {
			"net/minecraft/core/Direction$Axis$1",
			"net/minecraft/core/Direction$Axis$2",
			"net/minecraft/core/Direction$Axis$3",
		})
		static class XYZAxisMixin {
			/**
			 * @author iluha168
			 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
			 */
			@Inject(method = "choose(III)I", at = @At("HEAD"))
			void choose(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use Direction4.axis.choose instead."));
			}

			/**
			 * @author iluha168
			 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
			 */
			@Inject(method = "choose(ZZZ)Z", at = @At("HEAD"))
			void choose(boolean x, boolean y, boolean z, CallbackInfoReturnable<Boolean> cir) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use Direction4.Axis.choose instead."));
			}

			/**
			 * @author iluha168
			 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
			 */
			@Inject(method = "choose(DDD)D", at = @At("HEAD"))
			void choose(double x, double y, double z, CallbackInfoReturnable<Double> cir) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use Direction4.Axis.choose instead."));
			}
		}

		@Shadow
		public abstract Direction getPositive();

		@Shadow
		public abstract Direction getNegative();

		@Shadow
		public abstract int choose(int x, int y, int z);

		@Shadow
		public abstract double choose(double x, double y, double z);

		@Shadow
		public abstract boolean choose(boolean x, boolean y, boolean z);

		@Shadow
		AxisMixin(String name) {}

		@Override
		public abstract int choose(int x, int y, int z, int w);

		@Override
		public abstract boolean choose(boolean x, boolean y, boolean z, boolean w);

		@Override
		public abstract double choose(double x, double y, double z, double w);

		@WrapMethod(method = "isHorizontal")
		boolean isHorizontal(Operation<Boolean> original) {
			return original.call() || ((Object) this) == Direction4.Axis.W;
		}

		@WrapMethod(method = "getPlane")
		Direction.Plane getPlane(Operation<Direction.Plane> original) {
			return ((Object) this) == Direction4.Axis.W ? Direction.Plane.HORIZONTAL : original.call();
		}
	}

	@Mixin(Direction.Plane.class)
	static class PlaneMixin {
		@ModifyArgs(method = "<clinit>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Direction$Plane;<init>(Ljava/lang/String;I[Lnet/minecraft/core/Direction;[Lnet/minecraft/core/Direction$Axis;)V",
			ordinal = 0
		))
		private static void initHorizontalPlane(Args args) {
			Direction[] faces = ArrayUtils.addAll(args.get(2), Direction4.ANA, Direction4.KATA);
			Direction.Axis[] axis = ArrayUtils.addAll(args.get(3), Axis.W);
			args.set(2, faces);
			args.set(3, axis);
		}
	}
}
