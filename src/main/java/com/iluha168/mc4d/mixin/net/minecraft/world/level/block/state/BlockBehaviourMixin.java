package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.state;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Mth4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockBehaviour.class)
class BlockBehaviourMixin {
	@Definition(id = "UPDATE_SHAPE_ORDER", field = "Lnet/minecraft/world/level/block/state/BlockBehaviour;UPDATE_SHAPE_ORDER:[Lnet/minecraft/core/Direction;")
	@Expression("UPDATE_SHAPE_ORDER = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Direction[] UPDATE_SHAPE_ORDER(Direction[] original) {
		return ArrayUtils.addAll(original, Direction4.KATA, Direction4.ANA);
	}

	@Mixin(BlockBehaviour.Properties.class)
	static class PropertiesMixin {
		@Redirect(method = {"lambda$offsetType$0", "lambda$offsetType$1"}, at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/Mth;getSeed(III)J"
		))
		private static long offsetType_seed(int x, int y, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
			return Mth4.getSeed(x, y, z, Vec4i.getW(pos));
		}
		@Redirect(method = {"lambda$offsetType$0", "lambda$offsetType$1"}, at = @At(
			value = "NEW",
			target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
		))
		private static Vec3 offsetType_vec(
			double x, double y, double z,
			@Local(name = "seed") long seed,
			@Local(name = "maxHorizontalOffset") float maxHorizontalOffset
		) {
			final double w = Mth.clamp(((float) (seed >> 12 & 15L) / 15.0F - 0.5) * 0.5, -maxHorizontalOffset, maxHorizontalOffset);
			return new Vec4(x, y, z, w);
		}
	}
}
