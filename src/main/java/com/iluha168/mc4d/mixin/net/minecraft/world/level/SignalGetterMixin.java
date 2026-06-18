package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignalGetter.class)
interface SignalGetterMixin {
	@Shadow
	int getDirectSignal(BlockPos pos, Direction direction);

	@Shadow
	int getSignal(BlockPos pos, Direction direction);

	@Definition(id = "max", method = "Ljava/lang/Math;max(II)I")
	@Definition(id = "getDirectSignal", method = "Lnet/minecraft/world/level/SignalGetter;getDirectSignal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I")
	@Definition(id = "EAST", field = "Lnet/minecraft/core/Direction;EAST:Lnet/minecraft/core/Direction;")
	@Expression("max(?, this.getDirectSignal(?, EAST))")
	@ModifyExpressionValue(method = "getDirectSignalTo", at = @At("MIXINEXTRAS:EXPRESSION"))
	default int getDirectSignalTo(int result, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		if (result >= 15) {
			return result;
		}
		BlockPos4 pos4 = (BlockPos4) pos;

		result = Math.max(result, this.getDirectSignal(pos4.kata(), Direction4.KATA));
		if (result >= 15) {
			return result;
		}

		result = Math.max(result, this.getDirectSignal(pos4.ana(), Direction4.ANA));
		return result;
	}

	@WrapMethod(method = "hasNeighborSignal")
	default boolean hasNeighborSignal(BlockPos blockPos, Operation<Boolean> original) {
		if (original.call(blockPos)) {
			return true;
		}
		BlockPos4 pos4 = (BlockPos4) blockPos;
		return this.getSignal(pos4.kata(), Direction4.KATA) > 0
			|| this.getSignal(pos4.ana() , Direction4.ANA ) > 0;
	}
}
