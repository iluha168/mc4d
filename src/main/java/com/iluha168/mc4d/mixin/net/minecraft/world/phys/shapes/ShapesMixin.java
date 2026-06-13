package com.iluha168.mc4d.mixin.net.minecraft.world.phys.shapes;

import com.google.common.collect.Maps;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.math.OctahedralGroup4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.ArrayVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.BitSetDiscreteVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.OctahedralGroup;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Shapes.class)
public class ShapesMixin {
	@Shadow
	static DoubleList flipAxisIfNeeded(DoubleList newAxis, boolean flip, double newRelative, double oldRelative) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	protected static IndexMerger createIndexMerger(int cost, DoubleList first, DoubleList second, boolean firstOnlyMatters, boolean secondOnlyMatters) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public static boolean joinIsNotEmpty(VoxelShape first, VoxelShape second, BooleanOp op) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
	private static void BLOCK(CallbackInfoReturnable<CubeVoxelShape> cir) {
		DiscreteVoxelShape4 shape = new BitSetDiscreteVoxelShape4(1, 1, 1, 1);
		shape.fill(0, 0, 0, 0);
		cir.setReturnValue(new CubeVoxelShape(shape));
	}

	@Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;"))
	private static Vec3 BLOCK_CENTER(double x, double y, double z) {
		return new Vec4(x, y, z, 0.5);
	}

	@Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(III)Lnet/minecraft/world/phys/shapes/BitSetDiscreteVoxelShape;", ordinal = 0))
	private static BitSetDiscreteVoxelShape EMPTY(int xSize, int ySize, int zSize) {
		try {
			DiscreteVoxelShape4.UNSAFE_DISABLE_3D_ERRORS = true;
			return new BitSetDiscreteVoxelShape(xSize, ySize, zSize);
		} finally {
			DiscreteVoxelShape4.UNSAFE_DISABLE_3D_ERRORS = false;
		}
	}

	@Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;)Lnet/minecraft/world/phys/shapes/ArrayVoxelShape;"))
	private static ArrayVoxelShape EMPTY(DiscreteVoxelShape shape, DoubleList xs, DoubleList ys, DoubleList zs) {
		return new ArrayVoxelShape4(
			new BitSetDiscreteVoxelShape4(0, 0, 0, 0),
			new DoubleArrayList(new double[]{0.0}),
			new DoubleArrayList(new double[]{0.0}),
			new DoubleArrayList(new double[]{0.0}),
			new DoubleArrayList(new double[]{0.0})
		);
	}

	// `empty` does not need an override.
	// `block` does not need an override.

	@Overwrite
	public static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		if (minX == minZ && maxX == maxZ) {
			return Shapes4.box(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
		}
		throw Err4.arguments3("Shapes4#box");
	}

	@Overwrite
	public static VoxelShape create(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		throw Err4.arguments3("Shapes4#create");
	}

	@Redirect(method = "create(Lnet/minecraft/world/phys/AABB;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/Shapes;create(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape create(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(name = "aabb", argsOnly = true) AABB aabb
	) {
		if (!(aabb instanceof AABB4 aabb4)) {
			throw Err4.container3();
		}
		return Shapes4.create(
			minX, minY, minZ, aabb4.minW,
			maxX, maxY, maxZ, aabb4.maxW
		);
	}

	@Inject(method = "joinUnoptimized", cancellable = true, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/BitSetDiscreteVoxelShape;join(Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lnet/minecraft/world/phys/shapes/IndexMerger;Lnet/minecraft/world/phys/shapes/IndexMerger;Lnet/minecraft/world/phys/shapes/IndexMerger;Lnet/minecraft/world/phys/shapes/BooleanOp;)Lnet/minecraft/world/phys/shapes/BitSetDiscreteVoxelShape;"
	))
	private static void joinUnoptimized(
		VoxelShape first, VoxelShape second, BooleanOp op, CallbackInfoReturnable<VoxelShape> cir,
		@Local(name = "xMerger") IndexMerger xMerger,
		@Local(name = "yMerger") IndexMerger yMerger,
		@Local(name = "zMerger") IndexMerger zMerger,
		@Local(name = "firstOnlyMatters") boolean firstOnlyMatters,
		@Local(name = "secondOnlyMatters") boolean secondOnlyMatters
	) {
		IndexMerger wMerger = createIndexMerger(
			(xMerger.size() - 1) * (yMerger.size() - 1) * (zMerger.size() - 1),
			first.getCoords(Direction4.Axis.W),
			second.getCoords(Direction4.Axis.W),
			firstOnlyMatters, secondOnlyMatters
		);
		BitSetDiscreteVoxelShape4 voxelShape = BitSetDiscreteVoxelShape4.join(
			(DiscreteVoxelShape4) first.shape, (DiscreteVoxelShape4) second.shape,
			xMerger, yMerger, zMerger, wMerger, op
		);
		cir.setReturnValue(
			xMerger instanceof DiscreteCubeMerger
				&& yMerger instanceof DiscreteCubeMerger
				&& zMerger instanceof DiscreteCubeMerger
				&& wMerger instanceof DiscreteCubeMerger
			? new CubeVoxelShape(voxelShape)
			: new ArrayVoxelShape4(voxelShape, xMerger.getList(), yMerger.getList(), zMerger.getList(), wMerger.getList())
		);
	}

	@Redirect(method = "joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/IndexMerger;Lnet/minecraft/world/phys/shapes/IndexMerger;Lnet/minecraft/world/phys/shapes/IndexMerger;Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"
	))
	private static boolean joinIsNotEmpty(
		IndexMerger xMerger, IndexMerger yMerger, IndexMerger zMerger,
		DiscreteVoxelShape first, DiscreteVoxelShape second, BooleanOp op,
		@Local(argsOnly = true, name = "first") VoxelShape firstVS,
		@Local(argsOnly = true, name = "second") VoxelShape secondVS,
		@Local(name = "firstOnlyMatters") boolean firstOnlyMatters,
		@Local(name = "secondOnlyMatters") boolean secondOnlyMatters
	) {
		IndexMerger wMerger = createIndexMerger(
			(xMerger.size() - 1) * (yMerger.size() - 1) * (zMerger.size() - 1),
			firstVS.getCoords(Direction4.Axis.W),
			secondVS.getCoords(Direction4.Axis.W),
			firstOnlyMatters, secondOnlyMatters
		);
		return joinIsNotEmpty(xMerger, yMerger, zMerger, wMerger, first, second, op);
	}
	@Unique	private static boolean joinIsNotEmpty(
		IndexMerger xMerger, IndexMerger yMerger, IndexMerger zMerger, IndexMerger wMerger,
		DiscreteVoxelShape first, DiscreteVoxelShape second, BooleanOp op
	) {
		DiscreteVoxelShape4 first4 = (DiscreteVoxelShape4) first;
		DiscreteVoxelShape4 second4 = (DiscreteVoxelShape4) second;
		return ! xMerger.forMergedIndexes((x1, x2, xr) ->
			yMerger.forMergedIndexes((y1, y2, yr) ->
				zMerger.forMergedIndexes((z1, z2, zr) ->
					wMerger.forMergedIndexes((w1, w2, wr) ->
						! op.apply(first4.isFullWide(x1, y1, z1, w1), second4.isFullWide(x2, y2, z2, w2))
					)
				)
			)
		);
	}

	@Redirect(
		method = "rotate(Lnet/minecraft/world/phys/shapes/VoxelShape;Lcom/mojang/math/OctahedralGroup;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
		at = @At(value = "NEW", target = "(Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;)Lnet/minecraft/world/phys/shapes/ArrayVoxelShape;")
	)
	private static ArrayVoxelShape rotate(
		DiscreteVoxelShape newDiscreteShape, DoubleList xs, DoubleList ys, DoubleList zs,
		@Local(argsOnly = true, name = "shape") VoxelShape shape,
		@Local(argsOnly = true, name = "rotation") OctahedralGroup rotation,
		@Local(argsOnly = true, name = "rotationPoint") Vec3 rotationPoint
	){
		Direction.Axis newW = rotation.permutation().permuteAxis(Direction4.Axis.W);
		DoubleList newWs = shape.getCoords(newW);
		boolean flipW = rotation.inverts(Direction4.Axis.W);
		return new ArrayVoxelShape4(
			(DiscreteVoxelShape4) newDiscreteShape,
			xs,
			ys,
			zs,
			flipAxisIfNeeded(newWs, flipW, rotationPoint.get(newW), ((Vec4) rotationPoint).w)
		);
	}

	@Overwrite
	public static Map<Direction.Axis, VoxelShape> rotateHorizontalAxis(VoxelShape zAxis, Vec3 rotationCenter) {
		return Maps.newEnumMap(Map.of(
			Direction.Axis.Z , zAxis,
			Direction.Axis.X , Shapes.rotate(zAxis, OctahedralGroup4.BLOCK_ROT_YW_90, rotationCenter), // east
			Direction4.Axis.W, Shapes.rotate(zAxis, OctahedralGroup4.BLOCK_ROT_XY_270, rotationCenter) // ana
		));
	}

	/**
	 * @author iluha168
	 * @reason Could not make the linter shut up. Is there a way to type check this?
	 */
	@Overwrite
	public static Map<Direction.Axis, VoxelShape> rotateAllAxis(VoxelShape north, Vec3 rotationCenter) {
		return Maps.newEnumMap(
			Map.of(
				Direction.Axis.Z,
				north, // Z+
				Direction.Axis.X,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_90, rotationCenter), // east (X+)
				Direction.Axis.Y,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XW_90, rotationCenter), // down (Y-)
				Direction4.Axis.W,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XY_270, rotationCenter) // ana (W+)
			)
		);
	}

	/**
	 * @author iluha168
	 * @reason Could not make the linter shut up. Is there a way to type check this?
	 */
	@Overwrite
	public static Map<Direction, VoxelShape> rotateHorizontal(VoxelShape north, OctahedralGroup initial, Vec3 rotationCenter) {
		return Maps.newEnumMap(
			Map.of(
				Direction.NORTH,
				Shapes.rotate(north, initial),
				Direction.EAST,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_90.compose(initial), rotationCenter),
				Direction.SOUTH,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_180.compose(initial), rotationCenter),
				Direction.WEST,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_270.compose(initial), rotationCenter),
				Direction4.ANA,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XY_270.compose(initial), rotationCenter),
				Direction4.KATA,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XY_90.compose(initial), rotationCenter)
			)
		);
	}

	/**
	 * @author iluha168
	 * @reason Could not make the linter shut up. Is there a way to type check this?
	 */
	@Overwrite
	public static Map<Direction, VoxelShape> rotateAll(VoxelShape north, OctahedralGroup initial, Vec3 rotationCenter) {
		return Maps.newEnumMap(
			Map.of(
				Direction.NORTH,
				Shapes.rotate(north, initial),
				Direction.EAST,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_90.compose(initial), rotationCenter),
				Direction.SOUTH,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_180.compose(initial), rotationCenter),
				Direction.WEST,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_YW_270.compose(initial), rotationCenter),
				Direction.UP,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XW_270.compose(initial), rotationCenter),
				Direction.DOWN,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XW_90.compose(initial), rotationCenter),
				Direction4.ANA,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XY_270.compose(initial), rotationCenter),
				Direction4.KATA,
				Shapes.rotate(north, OctahedralGroup4.BLOCK_ROT_XY_90.compose(initial), rotationCenter)
			)
		);
	}
}
