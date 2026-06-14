package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {
	@Redirect(method = "lambda$isBlockInLine$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getApproximateNearest(DDD)Lnet/minecraft/core/Direction;"
	))
	private static Direction isBlockInLine$0(double dx, double dy, double dz, @Local(name = "delta") Vec3 delta) {
		return Direction4.getApproximateNearest(dx, dy, dz, ((Position4) delta).w());
	}
	@Redirect(method = "lambda$isBlockInLine$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getApproximateNearest(DDD)Lnet/minecraft/core/Direction;"
	))
	private static Direction isBlockInLine$1(double dx, double dy, double dz, @Local(name = "delta") Vec3 delta) {
		return Direction4.getApproximateNearest(dx, dy, dz, ((Position4) delta).w());
	}

	@Redirect(method = "lambda$clip$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getApproximateNearest(DDD)Lnet/minecraft/core/Direction;"
	))
	private static Direction clip(double dx, double dy, double dz, @Local(name = "delta") Vec3 delta) {
		return Direction4.getApproximateNearest(dx, dy, dz, ((Position4) delta).w());
	}

	/**
	 * @author iluha168
	 * @reason TODO Way too tightly coupled with 3D. Replace with a thousand mixins.
	 */
	@Overwrite
	static <T, C> T traverseBlocks(Vec3 from, Vec3 to, C context, BiFunction<C, BlockPos, @Nullable T> consumer, Function<C, T> missFactory) {
		if (from.equals(to)) {
			return missFactory.apply(context);
		}
		if (!(from instanceof Vec4 from4 && to instanceof Vec4 to4)) {
			throw Err4.container3();
		}
		double toX = Mth.lerp(-AABB4.EPSILON, to.x, from.x);
		double toY = Mth.lerp(-AABB4.EPSILON, to.y, from.y);
		double toZ = Mth.lerp(-AABB4.EPSILON, to.z, from.z);
		double toW = Mth.lerp(-AABB4.EPSILON, to4.w, from4.w);

		double fromX = Mth.lerp(-AABB4.EPSILON, from.x, to.x);
		double fromY = Mth.lerp(-AABB4.EPSILON, from.y, to.y);
		double fromZ = Mth.lerp(-AABB4.EPSILON, from.z, to.z);
		double fromW = Mth.lerp(-AABB4.EPSILON, from4.w, to4.w);

		int currentBlockX = Mth.floor(fromX);
		int currentBlockY = Mth.floor(fromY);
		int currentBlockZ = Mth.floor(fromZ);
		int currentBlockW = Mth.floor(fromW);

		BlockPos.MutableBlockPos pos = BlockPos4.MutableBlockPos.from(currentBlockX, currentBlockY, currentBlockZ, currentBlockW);
		T first = consumer.apply(context, pos);
		if (first != null) {
			return first;
		}

		double dx = toX - fromX;
		double dy = toY - fromY;
		double dz = toZ - fromZ;
		double dw = toW - fromW;

		int signX = Mth.sign(dx);
		int signY = Mth.sign(dy);
		int signZ = Mth.sign(dz);
		int signW = Mth.sign(dw);

		double tDeltaX = signX == 0 ? Double.MAX_VALUE : signX / dx;
		double tDeltaY = signY == 0 ? Double.MAX_VALUE : signY / dy;
		double tDeltaZ = signZ == 0 ? Double.MAX_VALUE : signZ / dz;
		double tDeltaW = signW == 0 ? Double.MAX_VALUE : signW / dw;

		double tX = tDeltaX * (signX > 0 ? 1.0 - Mth.frac(fromX) : Mth.frac(fromX));
		double tY = tDeltaY * (signY > 0 ? 1.0 - Mth.frac(fromY) : Mth.frac(fromY));
		double tZ = tDeltaZ * (signZ > 0 ? 1.0 - Mth.frac(fromZ) : Mth.frac(fromZ));
		double tW = tDeltaW * (signW > 0 ? 1.0 - Mth.frac(fromW) : Mth.frac(fromW));

		while (tX <= 1.0 || tY <= 1.0 || tZ <= 1.0 || tW <= 1.0) {
			if (tX < tY) {
				// x < y
				if (tX < tZ) {
					// x < y && x < z
					if (tX < tW) {
						// x < y && x < z && x < w
						currentBlockX += signX;
						tX += tDeltaX;
					} else {
						// w <= x < y && w <= x < z
						currentBlockW += signW;
						tW += tDeltaW;
					}
				} else {
					// z <= x < y
					if (tZ < tW) {
						// z <= x < y && z < w
						currentBlockZ += signZ;
						tZ += tDeltaZ;
					} else {
						// w <= z <= x < y
						currentBlockW += signW;
						tW += tDeltaW;
					}
				}
			} else {
				// y <= x
				if (tY < tZ) {
					// y <= x && y < z
					if (tY < tW) {
						// y <= x && y < z && y < w
						currentBlockY += signY;
						tY += tDeltaY;
					} else {
						// w <= y <= x && w <= y < z
						currentBlockW += signW;
						tW += tDeltaW;
					}
				} else {
					// z <= y <= x
					if (tZ < tW) {
						// z <= y <= x && z < w
						currentBlockZ += signZ;
						tZ += tDeltaZ;
					} else {
						// w <= z <= y <= x
						currentBlockW += signW;
						tW += tDeltaW;
					}
				}
			}

			T result = consumer.apply(context, ((BlockPos4.MutableBlockPos) pos).set(currentBlockX, currentBlockY, currentBlockZ, currentBlockW));
			if (result != null) {
				return result;
			}
		}

		return missFactory.apply(context);
	}

	/**
	 * @author iluha168
	 * @reason TODO Way too tightly coupled with 3D. Replace with a thousand mixins.
	 */
	@Overwrite
	private static int addCollisionsAlongTravel(LongSet visitedBlocks, Vec3 deltaMove3, AABB aabbAtTarget3, BlockGetter.BlockStepVisitor visitor) {
		Vec4 deltaMove = (Vec4) deltaMove3;
		AABB4 aabbAtTarget = (AABB4) aabbAtTarget3;

		double boxSizeX = aabbAtTarget.getXsize();
		double boxSizeY = aabbAtTarget.getYsize();
		double boxSizeZ = aabbAtTarget.getZsize();
		double boxSizeW = aabbAtTarget.getWsize();

		Vec3i cornerDir = getFurthestCorner(deltaMove);
		Vec4 toCenter = aabbAtTarget.getCenter();
		Vec4 toCorner = new Vec4(
			toCenter.x() + boxSizeX * 0.5 * cornerDir.getX(),
			toCenter.y() + boxSizeY * 0.5 * cornerDir.getY(),
			toCenter.z() + boxSizeZ * 0.5 * cornerDir.getZ(),
			toCenter.w() + boxSizeW * 0.5 * Vec4i.getW(cornerDir)
		);
		Vec4 fromCorner = toCorner.subtract(deltaMove);

		int cornerVisitedBlockX = Mth.floor(fromCorner.x);
		int cornerVisitedBlockY = Mth.floor(fromCorner.y);
		int cornerVisitedBlockZ = Mth.floor(fromCorner.z);
		int cornerVisitedBlockW = Mth.floor(fromCorner.w);

		int signX = Mth.sign(deltaMove.x);
		int signY = Mth.sign(deltaMove.y);
		int signZ = Mth.sign(deltaMove.z);
		int signW = Mth.sign(deltaMove.w);

		double tDeltaX = signX == 0 ? Double.MAX_VALUE : signX / deltaMove.x;
		double tDeltaY = signY == 0 ? Double.MAX_VALUE : signY / deltaMove.y;
		double tDeltaZ = signZ == 0 ? Double.MAX_VALUE : signZ / deltaMove.z;
		double tDeltaW = signW == 0 ? Double.MAX_VALUE : signW / deltaMove.w;

		double tX = tDeltaX * (signX > 0 ? 1.0 - Mth.frac(fromCorner.x) : Mth.frac(fromCorner.x));
		double tY = tDeltaY * (signY > 0 ? 1.0 - Mth.frac(fromCorner.y) : Mth.frac(fromCorner.y));
		double tZ = tDeltaZ * (signZ > 0 ? 1.0 - Mth.frac(fromCorner.z) : Mth.frac(fromCorner.z));
		double tW = tDeltaW * (signW > 0 ? 1.0 - Mth.frac(fromCorner.w) : Mth.frac(fromCorner.w));

		int iterations = 0;

		while (tX <= 1.0 || tY <= 1.0 || tZ <= 1.0 || tW <= 1.0) {
			if (tX < tY) {
				// x < y
				if (tX < tZ) {
					// x < y && x < z
					if (tX < tW) {
						// x < y && x < z && x < w
						cornerVisitedBlockX += signX;
						tX += tDeltaX;
					} else {
						// w <= x < y && w <= x < z
						cornerVisitedBlockW += signW;
						tW += tDeltaW;
					}
				} else {
					// z <= x < y
					if (tZ < tW) {
						// z <= x < y && z < w
						cornerVisitedBlockZ += signZ;
						tZ += tDeltaZ;
					} else {
						// w <= z <= x < y
						cornerVisitedBlockW += signW;
						tW += tDeltaW;
					}
				}
			} else {
				// y <= x
				if (tY < tZ) {
					// y <= x && y < z
					if (tY < tW) {
						// y <= x && y < z && y < w
						cornerVisitedBlockY += signY;
						tY += tDeltaY;
					} else {
						// w <= y <= x && w <= y < z
						cornerVisitedBlockW += signW;
						tW += tDeltaW;
					}
				} else {
					// z <= y <= x
					if (tZ < tW) {
						// z <= y <= x && z < w
						cornerVisitedBlockZ += signZ;
						tZ += tDeltaZ;
					} else {
						// w <= z <= y <= x
						cornerVisitedBlockW += signW;
						tW += tDeltaW;
					}
				}
			}

			Optional<Vec3> hitPointOpt = AABB4.clip(
				cornerVisitedBlockX,
				cornerVisitedBlockY,
				cornerVisitedBlockZ,
				cornerVisitedBlockW,
				cornerVisitedBlockX + 1,
				cornerVisitedBlockY + 1,
				cornerVisitedBlockZ + 1,
				cornerVisitedBlockW + 1,
				fromCorner,
				toCorner
			);
			if (hitPointOpt.isPresent()) {
				iterations++;
				Vec4 hitPoint = (Vec4) hitPointOpt.get();
				double cornerHitX = Mth.clamp(hitPoint.x, cornerVisitedBlockX + Mth.EPSILON, cornerVisitedBlockX + 1.0 - Mth.EPSILON);
				double cornerHitY = Mth.clamp(hitPoint.y, cornerVisitedBlockY + Mth.EPSILON, cornerVisitedBlockY + 1.0 - Mth.EPSILON);
				double cornerHitZ = Mth.clamp(hitPoint.z, cornerVisitedBlockZ + Mth.EPSILON, cornerVisitedBlockZ + 1.0 - Mth.EPSILON);
				double cornerHitW = Mth.clamp(hitPoint.w, cornerVisitedBlockW + Mth.EPSILON, cornerVisitedBlockW + 1.0 - Mth.EPSILON);

				int oppositeCornerX = Mth.floor(cornerHitX - boxSizeX * cornerDir.getX());
				int oppositeCornerY = Mth.floor(cornerHitY - boxSizeY * cornerDir.getY());
				int oppositeCornerZ = Mth.floor(cornerHitZ - boxSizeZ * cornerDir.getZ());
				int oppositeCornerW = Mth.floor(cornerHitW - boxSizeW * Vec4i.getW(cornerDir));

				int currentIteration = iterations;

				for (BlockPos pos : BlockPos4.betweenCornersInDirection(
					cornerVisitedBlockX, cornerVisitedBlockY, cornerVisitedBlockZ, cornerVisitedBlockW,
					oppositeCornerX, oppositeCornerY, oppositeCornerZ, oppositeCornerW,
					deltaMove
				)) {
					if (visitedBlocks.add(pos.asLong()) && !visitor.visit(pos, currentIteration)) {
						return -1;
					}
				}
			}
		}

		return iterations;
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Unique
	private static Vec3i getFurthestCorner(Vec4 direction) {
		double wDot = Math.abs(direction.w);
		double xDot = Math.abs(direction.x);
		double yDot = Math.abs(direction.y);
		double zDot = Math.abs(direction.z);
		int wSign = direction.w >= 0.0 ? 1 : -1;
		int xSign = direction.x >= 0.0 ? 1 : -1;
		int ySign = direction.y >= 0.0 ? 1 : -1;
		int zSign = direction.z >= 0.0 ? 1 : -1;

		// Negate the min and max aligned axes; keep middle two unchanged.
		if (wDot <= xDot && wDot <= yDot && wDot <= zDot) {
			// w is min
			if (xDot >= yDot && xDot >= zDot) {
				return Vec4i.from(-wSign, -xSign, ySign, zSign);
			} else if (yDot >= zDot) {
				return Vec4i.from(-wSign, xSign, -ySign, zSign);
			} else {
				return Vec4i.from(-wSign, xSign, ySign, -zSign);
			}
		} else if (xDot <= yDot && xDot <= zDot) {
			// x is min
			if (wDot >= yDot && wDot >= zDot) {
				return Vec4i.from(-wSign, -xSign, ySign, zSign);
			} else if (yDot >= zDot) {
				return Vec4i.from(wSign, -xSign, -ySign, zSign);
			} else {
				return Vec4i.from(wSign, -xSign, ySign, -zSign);
			}
		} else if (yDot <= zDot) {
			// y is min
			if (wDot >= xDot && wDot >= zDot) {
				return Vec4i.from(-wSign, xSign, -ySign, zSign);
			} else if (xDot >= zDot) {
				return Vec4i.from(wSign, -xSign, -ySign, zSign);
			} else {
				return Vec4i.from(wSign, xSign, -ySign, -zSign);
			}
		} else {
			// z is min
			if (wDot >= xDot && wDot >= yDot) {
				return Vec4i.from(-wSign, xSign, ySign, -zSign);
			} else if (xDot >= yDot) {
				return Vec4i.from(wSign, -xSign, ySign, -zSign);
			} else {
				return Vec4i.from(wSign, xSign, -ySign, -zSign);
			}
		}
	}
}
