package com.iluha168.mc4d.mixin.position4;

import com.google.common.collect.ImmutableList;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(Direction.class)
public enum DirectionMixin implements Direction4 {
	KATA(6, 7, 4, "kata", Direction.AxisDirection.NEGATIVE, Direction4.Axis.W, Vec4i.from(0, 0, 0, -1)),
	ANA (7, 6, 5, "ana" , Direction.AxisDirection.POSITIVE, Direction4.Axis.W, Vec4i.from(0, 0, 0, 1));

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void normal(CallbackInfo ci) {
		for (Direction direction : Direction.values()) {
			if (direction.getAxis() == Direction4.Axis.W) continue;
			Vec4i.setW(direction.getUnitVec3i(), 0);
		}
	}

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
		return Vec4i.getW(this.normal);
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

	@Overwrite
	public static Direction getNearest(int x, int y, int z, Direction orElse) {
		throw Err4.arguments3("Direction#getNearest(Vec4, Direction)");
	}
	@Redirect(method = "getNearest(Lnet/minecraft/core/Vec3i;Lnet/minecraft/core/Direction;)Lnet/minecraft/core/Direction;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getNearest(IIILnet/minecraft/core/Direction;)Lnet/minecraft/core/Direction;"
	))
	private static Direction getNearest(int x, int y, int z, Direction orElse, @Local(argsOnly = true, name = "vec") Vec3i vec) {
		final int w = Vec4i.getW(vec);
		int absX = Math.abs(x);
		int absY = Math.abs(y);
		int absZ = Math.abs(z);
		int absW = Math.abs(w);
		if (absX > absY && absX > absZ && absX > absW) {
			return x < 0 ? Direction.WEST : Direction.EAST;
		} else if (absZ > absX && absZ > absY && absZ > absW) {
			return z < 0 ? Direction.NORTH : Direction.SOUTH;
		} else if (absY > absX && absY > absZ && absY > absW) {
			return y < 0 ? Direction.DOWN : Direction.UP;
		} else if (absW > absX && absW > absY && absW > absZ) {
			return w < 0 ? Direction4.KATA : Direction4.ANA;
		} else {
			return orElse;
		}
	}

	@WrapMethod(method = "axisStepOrder")
	private static ImmutableList<Direction.Axis> axisStepOrder(Vec3 movement, Operation<ImmutableList<Direction.Axis>> original) {
		if (!(movement instanceof Vec4 vec4)) return original.call(movement);
		double dx = Math.abs(vec4.x);
		double dz = Math.abs(vec4.z);
		double dw = Math.abs(vec4.w);
		if (dx > dz && dx > dw) {
			return dz > dw ? Direction4.YXZW_AXIS_ORDER : Direction4.YXWZ_AXIS_ORDER;
		}
		if (dz > dw) {
			return dx > dw ? Direction4.YZXW_AXIS_ORDER : Direction4.YZWX_AXIS_ORDER;
		}
		return dx > dz ? Direction4.YWXZ_AXIS_ORDER : Direction4.YWZX_AXIS_ORDER;
	}

	// `getUnitVec3` does not need a patch.
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
				throw Err4.arguments3("Direction4.Axis#choose");
			}

			@Override
			public double choose(double x, double y, double z) {
				throw Err4.arguments3("Direction4.Axis#choose");
			}

			@Override
			public boolean choose(boolean x, boolean y, boolean z) {
				throw Err4.arguments3("Direction4.Axis#choose");
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
			@Inject(method = "choose(III)I", at = @At("HEAD"))
			void choose(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
				throw Err4.arguments3("Direction4.Axis#choose");
			}

			@Inject(method = "choose(ZZZ)Z", at = @At("HEAD"))
			void choose(boolean x, boolean y, boolean z, CallbackInfoReturnable<Boolean> cir) {
				throw Err4.arguments3("Direction4.Axis#choose");
			}

			@Inject(method = "choose(DDD)D", at = @At("HEAD"))
			void choose(double x, double y, double z, CallbackInfoReturnable<Double> cir) {
				throw Err4.arguments3("Direction4.Axis#choose");
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
