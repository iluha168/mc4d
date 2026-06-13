package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block.dispatch;

import com.iluha168.mc4d.core.Direction4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.OctahedralGroup;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO remove and make 4D rendering
@Mixin(BlockModelRotation.class)
public class BlockModelRotationMixin {
	@Definition(id = "orientation", local = @Local(type = OctahedralGroup.class, name = "orientation", argsOnly = true))
	@Definition(id = "IDENTITY", field = "Lcom/mojang/math/OctahedralGroup;IDENTITY:Lcom/mojang/math/OctahedralGroup;")
	@Expression("orientation != IDENTITY")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean strip4D(boolean original, @Local(argsOnly = true, name = "orientation") OctahedralGroup orientation) {
		return original && orientation.permutation().permute(3) == 3 && !orientation.inverts(Direction4.Axis.W);
	}

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	private static Direction[] direction3() {
		return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	}
}
