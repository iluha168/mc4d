package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.state;

import com.iluha168.mc4d.core.Direction4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
class BlockBehaviourMixin {
	@Definition(id = "UPDATE_SHAPE_ORDER", field = "Lnet/minecraft/world/level/block/state/BlockBehaviour;UPDATE_SHAPE_ORDER:[Lnet/minecraft/core/Direction;")
	@Expression("UPDATE_SHAPE_ORDER = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Direction[] UPDATE_SHAPE_ORDER(Direction[] original) {
		return ArrayUtils.addAll(original, Direction4.KATA, Direction4.ANA);
	}

	// TODO offsetType
}
