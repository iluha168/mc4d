package com.iluha168.mc4d.mixin.net.minecraft.world.phys.shapes;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.ArrayVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(VoxelShape.class)
abstract
class VoxelShapeMixin implements VoxelShape4 {

	@Shadow
	public abstract DoubleList getCoords(Direction.Axis axis);

	@Shadow
	@Final
	public DiscreteVoxelShape shape;

	@Shadow
	public abstract double min(Direction.Axis axis);

	@Shadow
	public abstract double max(Direction.Axis axis);

	@Shadow
	public abstract boolean isEmpty();

	@Shadow
	protected abstract int findIndex(Direction.Axis axis, double coord);

	@Shadow
	protected abstract double get(Direction.Axis axis, int i);

	@Redirect(method = "bounds", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"))
	AABB bounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		double minW = this.min(Direction4.Axis.W);
		double maxW = this.max(Direction4.Axis.W);
		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Redirect(method = "singleEncompassing", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/Shapes;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	VoxelShape singleEncompassing(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		double minW = this.min(Direction4.Axis.W);
		double maxW = this.max(Direction4.Axis.W);
		return Shapes4.box(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Redirect(method = "move(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;move(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	VoxelShape move(VoxelShape instance, double dx, double dy, double dz, @Local(argsOnly = true, name = "delta") Vec3 delta) {
		return ((VoxelShape4) instance).move(dx, dy, dz, ((Position4) delta).w());
	}

	@Redirect(method = "move(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;move(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	VoxelShape move(VoxelShape instance, double dx, double dy, double dz, @Local(argsOnly = true, name = "delta") Vec3i delta) {
		return ((VoxelShape4) instance).move(dx, dy, dz, Vec4i.getW(delta));
	}

	@Overwrite
	public VoxelShape move(double dx, double dy, double dz) {
		// If only one axis is non-zero, the call site intends to move only along one axis, set dw to 0.
		if ((dx == 0 && dz == 0)
			|| (dy == 0 && dz == 0)
			|| (dx == 0 && dy == 0)
		) {
			return this.move(dx, dy, dz, 0);
		}
		throw Err4.arguments3("VoxelShape4#move");
	}

	@Override
	public VoxelShape move(double dx, double dy, double dz, double dw) {
		return this.isEmpty() ? Shapes.empty() : new ArrayVoxelShape4(
			(DiscreteVoxelShape4) this.shape,
			new OffsetDoubleList(this.getCoords(Direction.Axis.X), dx),
			new OffsetDoubleList(this.getCoords(Direction.Axis.Y), dy),
			new OffsetDoubleList(this.getCoords(Direction.Axis.Z), dz),
			new OffsetDoubleList(this.getCoords(Direction4.Axis.W), dw)
		);
	}

	@Redirect(method = "optimize", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	void optimize(VoxelShape instance, Shapes.DoubleLineConsumer consumer, @Local(name = "result") VoxelShape[] result) {
		this.forAllBoxes(
			(x1, y1, z1, w1, x2, y2, z2, w2) ->
				result[0] = Shapes.joinUnoptimized(result[0], Shapes4.box(x1, y1, z1, w1, x2, y2, z2, w2), BooleanOp.OR)
		);
	}

	@Overwrite
	public void forAllEdges(Shapes.DoubleLineConsumer consumer) {
		throw Err4.arguments3("VoxelShape4#forAllEdges");
	}
	@Override
	public void forAllEdges(Shapes4.DoubleLineConsumer consumer) {
		((DiscreteVoxelShape4) this.shape).forAllEdges(
			(xi1, yi1, zi1, wi1, xi2, yi2, zi2, wi2) -> consumer.consume(
				this.get(Direction.Axis.X, xi1),
				this.get(Direction.Axis.Y, yi1),
				this.get(Direction.Axis.Z, zi1),
				this.get(Direction4.Axis.W, wi1),
				this.get(Direction.Axis.X, xi2),
				this.get(Direction.Axis.Y, yi2),
				this.get(Direction.Axis.Z, zi2),
				this.get(Direction4.Axis.W, wi2)
			),
			true
		);
	}

	@Overwrite
	public void forAllBoxes(Shapes.DoubleLineConsumer consumer) {
		throw Err4.arguments3("VoxelShape4#forAllBoxes");
	}
	@Override
	public void forAllBoxes(Shapes4.DoubleLineConsumer consumer) {
		DoubleList xCoords = this.getCoords(Direction.Axis.X);
		DoubleList yCoords = this.getCoords(Direction.Axis.Y);
		DoubleList zCoords = this.getCoords(Direction.Axis.Z);
		DoubleList wCoords = this.getCoords(Direction4.Axis.W);
		((DiscreteVoxelShape4) this.shape).forAllBoxes(
			(xi1, yi1, zi1, wi1, xi2, yi2, zi2, wi2) -> consumer.consume(
				xCoords.getDouble(xi1),
				yCoords.getDouble(yi1),
				zCoords.getDouble(zi1),
				wCoords.getDouble(wi1),
				xCoords.getDouble(xi2),
				yCoords.getDouble(yi2),
				zCoords.getDouble(zi2),
				wCoords.getDouble(wi2)
			),
			true
		);
	}

	@Redirect(method = "toAabbs", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	void toAABBs(VoxelShape instance, Shapes.DoubleLineConsumer consumer, @Local(name = "list") List<AABB> list) {
		((VoxelShape4) instance).forAllBoxes((
			x1, y1, z1, w1,
			x2, y2, z2, w2
		) -> list.add(new AABB4(x1, y1, z1, w1, x2, y2, z2, w2)));
	}

	// TODO min
	// TODO max

	@Redirect(method = "clip", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;isFullWide(III)Z"
	))
	boolean clip(
		DiscreteVoxelShape shape, int x, int y, int z,
		@Local(name = "testPoint") Vec3 testPoint,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		if (!(testPoint instanceof Vec4 testPoint4)) {
			throw Err4.container3();
		}
		final int w = this.findIndex(Direction4.Axis.W, testPoint4.w - Vec4i.getW(pos));
		return ((DiscreteVoxelShape4) shape).isFullWide(x, y, z, w);
	}

	// TODO closestPointTo

	@ModifyConstant(method = "getFaceShape", constant = @Constant(intValue = 6))
	int getFaceShape(int constant) {
		return 8; // Direction.values().length
	}

	/**
	 * @author iluha168
	 * @reason Way too tightly coupled with 3D, cant mixin a loop?
	 */
	@Overwrite
	protected double collideX(AxisCycle transform, AABB moving, double distance) {
		if (this.isEmpty()) {
			return distance;
		}
		if (Math.abs(distance) < Shapes.EPSILON) {
			return 0.0;
		}
		
		AxisCycle inverse = transform.inverse();
		Direction.Axis aAxis = inverse.cycle(Direction.Axis.X); // aAxis must always be the axis we are trying to move on!
		Direction.Axis bAxis = inverse.cycle(Direction.Axis.Y);
		Direction.Axis cAxis = inverse.cycle(Direction.Axis.Z);
		Direction.Axis dAxis = inverse.cycle(Direction4.Axis.W);

		double maxA = moving.max(aAxis);
		double minA = moving.min(aAxis);

		int aMin = this.findIndex(aAxis, minA + Shapes.EPSILON);
		int aMax = this.findIndex(aAxis, maxA - Shapes.EPSILON);
		int bMin = Math.max(0, this.findIndex(bAxis, moving.min(bAxis) + Shapes.EPSILON));
		int bMax = Math.min(this.shape.getSize(bAxis), this.findIndex(bAxis, moving.max(bAxis) - Shapes.EPSILON) + 1);
		int cMin = Math.max(0, this.findIndex(cAxis, moving.min(cAxis) + Shapes.EPSILON));
		int cMax = Math.min(this.shape.getSize(cAxis), this.findIndex(cAxis, moving.max(cAxis) - Shapes.EPSILON) + 1);
		int dMin = Math.max(0, this.findIndex(dAxis, moving.min(dAxis) + Shapes.EPSILON));
		int dMax = Math.min(this.shape.getSize(dAxis), this.findIndex(dAxis, moving.max(dAxis) - Shapes.EPSILON) + 1);

		int aSize = this.shape.getSize(aAxis);
		DiscreteVoxelShape4 shape4 = (DiscreteVoxelShape4) this.shape;
		if (distance > 0.0) {
			for (int a = aMax + 1; a < aSize; a++)
				for (int b = bMin; b < bMax; b++)
					for (int c = cMin; c < cMax; c++)
						for (int d = dMin; d < dMax; d++)
							if (shape4.isFullWide(inverse, a, b, c, d)) {
								double newDistance = this.get(aAxis, a) - maxA;
								if (newDistance >= -Shapes.EPSILON) {
									distance = Math.min(distance, newDistance);
								}
								return distance;
							}
		} else if (distance < 0.0) {
			for (int a = aMin - 1; a >= 0; a--)
				for (int b = bMin; b < bMax; b++)
					for (int c = cMin; c < cMax; c++)
						for (int d = dMin; d < dMax; d++)
							if (shape4.isFullWide(inverse, a, b, c, d)) {
								double newDistance = this.get(aAxis, a + 1) - minA;
								if (newDistance <= Shapes.EPSILON) {
									distance = Math.max(distance, newDistance);
								}
								return distance;
							}
		}

		return distance;
	}
}
