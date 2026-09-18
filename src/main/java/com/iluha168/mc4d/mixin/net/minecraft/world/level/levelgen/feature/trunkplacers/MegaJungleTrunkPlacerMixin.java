package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.RotationVec;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MegaJungleTrunkPlacer.class)
class MegaJungleTrunkPlacerMixin {
	@ModifyExpressionValue(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/RandomSource;nextFloat()F"
	))
	private float placeTrunk_wRot_bw(
		float original,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Share("wRotCos") LocalFloatRef wRotCos,
		@Share("wRotSin") LocalFloatRef wRotSin,
		@Share("bw") LocalIntRef bw
	) {
		final double wRot = RotationVec.randomWRotRad(random);
		wRotCos.set(Mth.cos(wRot));
		wRotSin.set(Mth.sin(wRot));
		bw.set(0); // Technically 0 is the default, but just to be clear.
		return original;
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;cos(D)F"
	))
	private float placeTrunk_bx(double i, @Share("wRotCos") LocalFloatRef wRotCos) {
		return Mth.cos(i) * wRotCos.get();
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;sin(D)F"
	))
	private float placeTrunk_bz_bw(
		double i,
		@Local(name = "b") int b,
		@Share("wRotCos") LocalFloatRef wRotCos,
		@Share("wRotSin") LocalFloatRef wRotSin,
		@Share("bw") LocalIntRef bw
	) {
		bw.set((int) (1.5F + wRotSin.get() * b));
		return Mth.sin(i) * wRotCos.get();
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos placeTrunk_offset(BlockPos origin, int x, int y, int z, @Share("bw") LocalIntRef bw) {
		return ((BlockPos4) origin).offset(x, y, z, bw.get());
	}
}
