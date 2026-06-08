package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin implements BlockPos4 {
	@Shadow
	public abstract BlockPos relative(Direction direction);

	@Shadow
	public abstract BlockPos relative(Direction direction, int steps);

	@ModifyConstant(method = "lambda$static$0", constant = @Constant(intValue = 3))
	private static int CODEC_size4(int constant) {
		return 4;
	}
	@ModifyExpressionValue(method = "lambda$static$1", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos CODEC_createBlockPos(BlockPos original, @Local(argsOnly = true, name = "ints") int[] ints) {
		Vec4i.setW(original, ints[3]);
		return original;
	}
	@ModifyArg(method = "lambda$static$2", at = @At(
		value = "INVOKE",
		target = "Ljava/util/stream/IntStream;of([I)Ljava/util/stream/IntStream;"
	))
	private static int[] CODEC_createStream(int[] values, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ArrayUtils.addAll(values, Vec4i.getW(pos));
	}

	@Definition(id = "ZERO", field = "Lnet/minecraft/core/BlockPos;ZERO:Lnet/minecraft/core/BlockPos;")
	@Expression("ZERO = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos ZERO(BlockPos ZERO) {
		Vec4i.setW(ZERO, 0);
		return ZERO;
	}

	@ModifyArg(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;smallestEncompassingPowerOfTwo(I)I"
	))
	private static int MAX_LEVEL_SIZE(int input) {
		return Level4.MAX_LEVEL_SIZE;
	}

	@Definition(id = "PACKED_HORIZONTAL_LENGTH", field = "Lnet/minecraft/core/BlockPos;PACKED_HORIZONTAL_LENGTH:I")
	@Expression("@(2) * PACKED_HORIZONTAL_LENGTH")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int PACKED_Y_LENGTH(int original) {
		return 3;
	}

	@Inject(method = "<init>(Lnet/minecraft/core/Vec3i;)V", at = @At("TAIL"))
	void initCopyW(Vec3i vec3i, CallbackInfo ci) {
		((Vec4i) this).setW(Vec4i.getW(vec3i));
	}

	@Redirect(method = "offset(JLnet/minecraft/core/Direction;)J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(JIII)J"
	))
	private static long offsetDirection(long blockNode, int stepX, int stepY, int stepZ, @Local(argsOnly = true, name = "offset") Direction offset) {
		return BlockPos4.offset(blockNode, stepX, stepY, stepZ, Direction4.as(offset).getStepW());
	}
	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static long offset(long blockNode, int stepX, int stepY, int stepZ) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#offset instead."));
	}

	@WrapMethod(method = "of")
	private static BlockPos of(long blockNode, Operation<BlockPos> original) {
		BlockPos pos = original.call(blockNode);
		Vec4i.setW(pos, BlockPos4.getW(blockNode));
		return pos;
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static BlockPos containing(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#containing instead."));
	}

	@Redirect(method = "containing(Lnet/minecraft/core/Position;)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos containing(double x, double y, double z, @Local(argsOnly = true, name = "pos") Position pos) {
		if (pos instanceof Vec4 pos4) {
			return BlockPos4.containing(x, y, z, pos4.w());
		}
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#containing instead."));
	}

	@WrapMethod(method = "min")
	private static BlockPos min(BlockPos a, BlockPos b, Operation<BlockPos> original) {
		BlockPos pos = original.call(a, b);
		Vec4i.setW(pos, Math.min(Vec4i.getW(a), Vec4i.getW(b)));
		return pos;
	}

	@WrapMethod(method = "max")
	private static BlockPos max(BlockPos a, BlockPos b, Operation<BlockPos> original) {
		BlockPos pos = original.call(a, b);
		Vec4i.setW(pos, Math.max(Vec4i.getW(a), Vec4i.getW(b)));
		return pos;
	}

	@Redirect(method = "asLong()J", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;asLong(III)J"))
	long asLong_this(int x, int y, int z) {
		return BlockPos4.asLong(x, y, z, ((Vec4i) this).getW());
	}
	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static long asLong(int x, int y, int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#asLong instead."));
	}

	// TODO getFlatIndex? Sets last 4 bits of long to 0, what for?

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public BlockPos offset(int x, int y, int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#offset instead."));
	}

	// `getCenter` handled by Vec4
	// `getBottomCenter` handled by Vec4

	@Redirect(method = "offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos offset(BlockPos This, int x, int y, int z, @Local(argsOnly = true, name = "vec") Vec3i vec) {
		return ((BlockPos4) This).offset(x, y, z, Vec4i.getW(vec));
	}

	@Redirect(method = "subtract(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos subtract(BlockPos This, int x, int y, int z, @Local(argsOnly = true, name = "vec") Vec3i vec) {
		return ((BlockPos4) This).offset(x, y, z, -Vec4i.getW(vec));
	}

	@Redirect(method = "multiply(I)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos multiplyScalar(int x, int y, int z, @Local(argsOnly = true, name = "scale") int scale) {
		return BlockPos4.from(x, y, z, ((Vec4i) this).getW() * scale);
	}

	@Override
	public BlockPos ana() {
		return this.relative(Direction4.ANA);
	}
	@Override
	public BlockPos ana(int steps) {
		return this.relative(Direction4.ANA, steps);
	}
	@Override
	public BlockPos kata() {
		return this.relative(Direction4.KATA);
	}
	@Override
	public BlockPos kata(int steps) {
		return this.relative(Direction4.KATA, steps);
	}

	@WrapMethod(method = "relative(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos;")
	BlockPos relative(Direction direction, Operation<BlockPos> original) {
		BlockPos pos = original.call(direction);
		Vec4i.setW(pos, ((Vec4i) this).getW() + Direction4.as(direction).getStepW());
		return pos;
	}
	@WrapMethod(method = "relative(Lnet/minecraft/core/Direction;I)Lnet/minecraft/core/BlockPos;")
	BlockPos relative(Direction direction, int steps, Operation<BlockPos> original) {
		BlockPos pos = original.call(direction, steps);
		Vec4i.setW(pos, ((Vec4i) this).getW() + Direction4.as(direction).getStepW() * steps);
		return pos;
	}
	@WrapMethod(method = "relative(Lnet/minecraft/core/Direction$Axis;I)Lnet/minecraft/core/BlockPos;")
	BlockPos relative(Direction.Axis axis, int steps, Operation<BlockPos> original) {
		BlockPos pos = original.call(axis, steps);
		final int wStep = axis == Direction4.Axis.W ? steps : 0;
		Vec4i.setW(pos, ((Vec4i) this).getW() + wStep);
		return pos;
	}

	// TODO rotate

	/**
	 * @author iluha168
	 * @reason It is true. Cross product of 2 vectors does not make sense. 3 however...
	 */
	@Overwrite
	public BlockPos cross(Vec3i upVector) {
		throw Util.pauseInIde(new ArithmeticException("No cross product defined for 4D space"));
	}

	@WrapMethod(method = "atY")
	BlockPos atY(int y, Operation<BlockPos> original) {
		BlockPos pos = original.call(y);
		Vec4i.setW(pos, ((Vec4i) this).getW());
		return pos;
	}

	@WrapMethod(method = "mutable")
	BlockPos.MutableBlockPos mutable(Operation<BlockPos.MutableBlockPos> original) {
		BlockPos.MutableBlockPos pos = original.call();
		Vec4i.setW(pos, ((Vec4i) this).getW());
		return pos;
	}

	@Redirect(method = "clampLocationWithin", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 clampLocationWithin(double x, double y, double z, @Local(argsOnly = true, name = "location") Vec3 location) {
		if (!(location instanceof Vec4 location4)) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: supply a Vec4 for location."));
		}
		final int thisW = ((Vec4i) this).getW();
		final double w = Mth.clamp(location4.w, thisW + Mth.EPSILON, thisW + 1 - Mth.EPSILON);
		return new Vec4(x, y, z, w);
	}

	@Redirect(method = "randomInCube", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;randomBetweenClosed(Lnet/minecraft/util/RandomSource;IIIIIII)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> randomInCube(
		RandomSource random, int limit,
		int minX, int minY, int minZ,
		int maxX, int maxY, int maxZ,
		@Local(argsOnly = true, name = "center") BlockPos center,
		@Local(argsOnly = true, name = "sizeToScanInAllDirections") int sizeToScanInAllDirections
	) {
		final int centerW = Vec4i.getW(center);
		return BlockPos4.randomBetweenClosed(
			random, limit,
			minX, minY, minZ, centerW - sizeToScanInAllDirections,
			maxX, maxY, maxZ, centerW + sizeToScanInAllDirections
		);
	}

	// I dont think squareOutSouthEast needs a patch

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Iterable<BlockPos> randomBetweenClosed(RandomSource random, int limit, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#randomBetweenClosed instead."));
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Iterable<BlockPos> withinManhattan(BlockPos origin, int reachX, int reachY, int reachZ) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#withinManhattan instead."));
	}

	@Redirect(method = "findClosestMatch", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;withinManhattan(Lnet/minecraft/core/BlockPos;III)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> findClosestMatch(BlockPos origin, int reachX, int reachY, int reachZ) {
		return BlockPos4.withinManhattan(origin, reachX, reachY, reachZ, reachZ);
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Stream<BlockPos> withinManhattanStream(BlockPos origin, int reachX, int reachY, int reachZ) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#withinManhattanStream instead."));
	}

	@Definition(id = "containing", method = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "minZ", field = "Lnet/minecraft/world/phys/AABB;minZ:D")
	@Expression("containing(?, ?, ?.minZ)")
	@Redirect(method = "betweenClosed(Lnet/minecraft/world/phys/AABB;)Ljava/lang/Iterable;", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos betweenClosed_containingMin(double x, double y, double z, @Local(argsOnly = true, name = "box") AABB box) {
		if (!(box instanceof AABB4 box4)) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: supply an AABB4."));
		}
		return BlockPos4.containing(x, y, z, box4.minW);
	}
	@Definition(id = "containing", method = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "maxZ", field = "Lnet/minecraft/world/phys/AABB;maxZ:D")
	@Expression("containing(?, ?, ?.maxZ)")
	@Redirect(method = "betweenClosed(Lnet/minecraft/world/phys/AABB;)Ljava/lang/Iterable;", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos betweenClosed_containingMax(double x, double y, double z, @Local(argsOnly = true, name = "box") AABB box) {
		return BlockPos4.containing(x, y, z, ((AABB4) box).maxW);
	}

	@Redirect(method = "betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosed(IIIIII)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> betweenClosed(
		int minX, int minY, int minZ,
		int maxX, int maxY, int maxZ,
		@Local(argsOnly = true, name = "a") BlockPos a,
		@Local(argsOnly = true, name = "b") BlockPos b
	) {
		final int aw = Vec4i.getW(a);
		final int bw = Vec4i.getW(b);
		return BlockPos4.betweenClosed(
			minX, minY, minZ, Math.min(aw, bw),
			maxX, maxY, maxZ, Math.max(aw, bw)
		);
	}

	// `betweenClosedStream(a,b)` does not need a patch.
	// TODO betweenClosedStream(BoundingBox boundingBox)

	@Redirect(method = "betweenClosedStream(Lnet/minecraft/world/phys/AABB;)Ljava/util/stream/Stream;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosedStream(IIIIII)Ljava/util/stream/Stream;"
	))
	private static Stream<BlockPos> betweenClosedStream(
		int minX, int minY, int minZ,
		int maxX, int maxY, int maxZ,
		@Local(argsOnly = true, name = "box") AABB box
	) {
		if (!(box instanceof AABB4 box4)) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: supply an AABB4."));
		}
		return BlockPos4.betweenClosedStream(
			minX, minY, minZ, Mth.floor(box4.minW),
			maxX, maxY, maxZ, Mth.floor(box4.maxW)
		);
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Stream<BlockPos> betweenClosedStream(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#betweenClosedStream instead."));
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Iterable<BlockPos> betweenClosed(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#betweenClosed instead."));
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Iterable<BlockPos.MutableBlockPos> spiralAround(BlockPos center, int radius, Direction firstDirection, Direction secondDirection) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#spiralAround instead."));
	}

	// Surprisingly `breadthFirstTraversal` does not need an override.

	@Redirect(method = "betweenCornersInDirection(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/phys/Vec3;)Ljava/lang/Iterable;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenCornersInDirection(IIIIIILnet/minecraft/world/phys/Vec3;)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> betweenCornersInDirection_fromAABB(
		int firstCornerX, int firstCornerY, int firstCornerZ,
		int secondCornerX, int secondCornerY, int secondCornerZ,
		Vec3 direction,
		@Local(name = "minCorner") Vec3 minCorner,
		@Local(name = "maxCorner") Vec3 maxCorner
	) {
		if (!(direction instanceof Vec4 direction4)) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: supply a Vec4 instead."));
		}
		int firstCornerW = Mth.floor(((Position4) minCorner).w());
		int secondCornerW = Mth.floor(((Position4) maxCorner).w());
		return BlockPos4.betweenCornersInDirection(
			firstCornerX, firstCornerY, firstCornerZ, firstCornerW,
			secondCornerX, secondCornerY, secondCornerZ, secondCornerW,
			direction4
		);
	}

	@Redirect(method = "betweenCornersInDirection(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/Vec3;)Ljava/lang/Iterable;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenCornersInDirection(IIIIIILnet/minecraft/world/phys/Vec3;)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> betweenCornersInDirection(
		int firstCornerX, int firstCornerY, int firstCornerZ,
		int secondCornerX, int secondCornerY, int secondCornerZ, Vec3 direction,
		@Local(argsOnly = true, name = "firstCorner") BlockPos firstCorner,
		@Local(argsOnly = true, name = "secondCorner") BlockPos secondCorner
	) {
		if (!(direction instanceof Vec4 direction4)) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: supply a Vec4 instead."));
		}
		return BlockPos4.betweenCornersInDirection(
			firstCornerX, firstCornerY, firstCornerZ, Vec4i.getW(firstCorner),
			secondCornerX, secondCornerY, secondCornerZ, Vec4i.getW(secondCorner),
			direction4
		);
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Iterable<BlockPos> betweenCornersInDirection(
		int firstCornerX, int firstCornerY, int firstCornerZ,
		int secondCornerX, int secondCornerY, int secondCornerZ,
		Vec3 direction
	) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4#betweenCornersInDirection instead."));
	}

	@Mixin(BlockPos.MutableBlockPos.class)
	static class MutableBlockPosMixin implements BlockPos4.MutableBlockPos {
		@Inject(method = "<init>()V", at = @At("TAIL"))
		void emptyConstructor(CallbackInfo ci) {
			((Vec4i) this).setW(0);
		}

		/**
		 * @author iluha168
		 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
		 */
		@Overwrite
		public BlockPos.MutableBlockPos set(int x, int y, int z) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4.MutableBlockPos#set instead."));
		}

		/**
		 * @author iluha168
		 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
		 */
		@Overwrite
		public BlockPos.MutableBlockPos set(double x, double y, double z) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4.MutableBlockPos#set instead."));
		}

		@Redirect(method = "set(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos set(BlockPos.MutableBlockPos This, int x, int y, int z, @Local(argsOnly = true, name = "vec") Vec3i vec) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, Vec4i.getW(vec));
		}

		@Redirect(method = "set(J)Lnet/minecraft/core/BlockPos$MutableBlockPos;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos set(BlockPos.MutableBlockPos This, int x, int y, int z, @Local(argsOnly = true, name = "pos") long pos) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, BlockPos4.getW(pos));
		}

		/**
		 * @author iluha168
		 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
		 */
		@Overwrite
		public BlockPos.MutableBlockPos set(AxisCycle transform, int x, int y, int z) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4.MutableBlockPos#set instead."));
		}

		@Redirect(method = "setWithOffset(Lnet/minecraft/core/Vec3i;Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos setWithOffset(
			BlockPos.MutableBlockPos This, int x, int y, int z,
			@Local(argsOnly = true, name = "pos") Vec3i pos,
			@Local(argsOnly = true, name = "direction") Direction direction
		) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, Vec4i.getW(pos) + Direction4.as(direction).getStepW());
		}

		/**
		 * @author iluha168
		 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
		 */
		@Overwrite
		public BlockPos.MutableBlockPos setWithOffset(Vec3i pos, int x, int y, int z) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4.MutableBlockPos#setWithOffset instead."));
		}

		@Redirect(method = "setWithOffset(Lnet/minecraft/core/Vec3i;Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos setWithOffset(
			BlockPos.MutableBlockPos This, int x, int y, int z,
			@Local(argsOnly = true, name = "pos") Vec3i pos,
			@Local(argsOnly = true, name = "offset") Vec3i offset
		) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, Vec4i.getW(pos) + Vec4i.getW(offset));
		}

		@Redirect(method = "move(Lnet/minecraft/core/Direction;I)Lnet/minecraft/core/BlockPos$MutableBlockPos;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos move(
			BlockPos.MutableBlockPos This, int x, int y, int z,
			@Local(argsOnly = true, name = "direction") Direction direction,
			@Local(argsOnly = true, name = "steps") int steps
		) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, ((Vec4i) this).getW() + Direction4.as(direction).getStepW() * steps);
		}

		/**
		 * @author iluha168
		 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
		 */
		@Overwrite
		public BlockPos.MutableBlockPos move(int x, int y, int z) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4.MutableBlockPos#move instead."));
		}

		@Redirect(method = "move(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos$MutableBlockPos;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos move(BlockPos.MutableBlockPos This, int x, int y, int z, @Local(argsOnly = true, name = "pos") Vec3i pos) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, ((Vec4i) this).getW() + Vec4i.getW(pos));
		}

		@Inject(method = "clamp", cancellable = true, at = @At("HEAD"))
		void clamp_W(Direction.Axis axis, int minimum, int maximum, CallbackInfoReturnable<BlockPos.MutableBlockPos> cir) {
			if (axis == Direction4.Axis.W) {
				BlockPos.MutableBlockPos This = (BlockPos.MutableBlockPos) (Object) this;
				this.set(This.getX(), This.getY(), This.getZ(), Mth.clamp(Vec4i.getW(This), minimum, maximum));
				cir.cancel();
			}
		}
		@Redirect(method = "clamp", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos clamp_3(BlockPos.MutableBlockPos This, int x, int y, int z) {
			return ((BlockPos4.MutableBlockPos) This).set(x, y, z, ((Vec4i) this).getW());
		}
	}
}
