package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Direction4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.phys.RotationVec;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FancyTrunkPlacer.class)
class FancyTrunkPlacerMixin {
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sin(D)D"
	))
	private double placeTrunk_x(
		double a,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Local(name = "radius") double radius,
		@Share("wRotCos") LocalDoubleRef wRotCos,
		@Share("w") LocalDoubleRef w
	) {
		final double wRot = RotationVec.randomWRotRad(random);
		wRotCos.set(Math.cos(wRot));
		w.set(radius * Math.sin(wRot) + 0.5);
		return Math.sin(a) * wRotCos.get();
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;cos(D)D"
	))
	private double placeTrunk_z(double a, @Share("wRotCos") LocalDoubleRef wRotCos) {
		return Math.cos(a) * wRotCos.get();
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos placeTrunk_checkStart(BlockPos origin, int x, int y, int z, @Share("w") LocalDoubleRef w) {
		return ((BlockPos4) origin).offset(x, y, z, Mth.floor(w.get()));
	}
	@ModifyArg(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sqrt(D)D"
	))
	private double placeTrunk_branchLength(
		double dist3d,
		@Local(argsOnly = true, name = "origin") BlockPos origin,
		@Local(name = "checkStart") BlockPos checkStart
	) {
		final int dw = Vec4i.getW(origin) - Vec4i.getW(checkStart);
		return dist3d + dw * dw;
	}
	@ModifyExpressionValue(method = "placeTrunk", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos placeTrunk_checkBranchBase(BlockPos original, @Local(argsOnly = true, name = "origin") BlockPos origin) {
		Vec4i.setW(original, Vec4i.getW(origin));
		return original;
	}

	@Definition(id = "offset", method = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "endPos", local = @Local(type = BlockPos.class, name = "endPos", argsOnly = true))
	@Expression("endPos.offset(?, ?, ?)")
	@Redirect(method = "makeLimb", at = @At("MIXINEXTRAS:EXPRESSION"))
	private BlockPos makeLimb_offset(
		BlockPos endPos, int x, int y, int z,
		@Local(argsOnly = true, name = "startPos") BlockPos startPos
	) {
		return ((BlockPos4) endPos).offset(x, y, z, -Vec4i.getW(startPos));
	}
	@Definition(id = "offset", method = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "startPos", local = @Local(type = BlockPos.class, name = "startPos", argsOnly = true))
	@Expression("startPos.offset(?, ?, ?)")
	@Redirect(method = "makeLimb", at = @At("MIXINEXTRAS:EXPRESSION"))
	private BlockPos makeLimb_step(
		BlockPos startPos, int x, int y, int z,
		@Local(name = "delta") BlockPos delta,
		@Local(name = "steps") int steps,
		@Local(name = "i") int i
	) {
		final float dw = (float) Vec4i.getW(delta) / steps;
		return ((BlockPos4) startPos).offset(x, y, z, Mth.floor(0.5F + i * dw));
	}

	@ModifyReturnValue(method = "getSteps", at = @At("RETURN"))
	private int getSteps(int absXYZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int absW = Mth.abs(Vec4i.getW(pos));
		return Math.max(absXYZ, absW);
	}

	@ModifyExpressionValue(method = "getLogAxis", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;max(II)I"
	))
	private int getLogAxis_max(int xzDiff, @Local(name = "startPos", argsOnly = true) BlockPos startPos, @Local(name = "blockPos", argsOnly = true) BlockPos blockPos) {
		final int wdiff = Math.abs(Vec4i.getW(blockPos) - Vec4i.getW(startPos));
		return Math.max(xzDiff, wdiff);
	}
	@ModifyExpressionValue(method = "getLogAxis", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/core/Direction$Axis;Z:Lnet/minecraft/core/Direction$Axis;",
		opcode = Opcodes.GETSTATIC
	))
	Direction.Axis getLogAxis_else(Direction.Axis Z, @Local(name = "zdiff") int zdiff, @Local(name = "maxdiff") int maxdiff) {
		return zdiff == maxdiff ? Z : Direction4.Axis.W;
	}

	@ModifyExpressionValue(method = "makeBranches", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos makeBranches(BlockPos original, @Local(argsOnly = true, name = "origin") BlockPos origin) {
		Vec4i.setW(original, Vec4i.getW(origin));
		return original;
	}
}
