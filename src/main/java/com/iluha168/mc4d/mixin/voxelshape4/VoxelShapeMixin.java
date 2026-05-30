package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Position4i;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.shapes.ArrayVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Util;
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
		return ((VoxelShape4) instance).move(dx, dy, dz, ((Position4i) delta).getW());
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public VoxelShape move(double dx, double dy, double dz) {
		// If only one axis is non-zero, the call site intends to move only along one axis, set dw to 0.
		if ((dx == 0 && dz == 0)
			|| (dy == 0 && dz == 0)
			|| (dx == 0 && dy == 0)
		) {
			return this.move(dx, dy, dz, 0);
		}
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use VoxelShape4#move instead."));
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

	// TODO forAllEdges

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public void forAllBoxes(Shapes.DoubleLineConsumer consumer) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use VoxelShape4#forAllBoxes instead."));
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
	// TODO clip
	// TODO closestPointTo

	@ModifyConstant(method = "getFaceShape", constant = @Constant(intValue = 6))
	int getFaceShape(int constant) {
		return 8; // Direction.values().length
	}

	// TODO collideX
	// TODO toString
}
