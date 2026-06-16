package com.iluha168.mc4d.mixin.net.minecraft.client.tutorial;

import com.iluha168.mc4d.world.entity.player.Input4;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.tutorial.MovementTutorialStepInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MovementTutorialStepInstance.class)
class MovementTutorialStepInstanceMixin {
	@Expression("? == ?")
	@ModifyExpressionValue(method = "onInput", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	boolean onInput(boolean original, @Local(argsOnly = true, name = "input") ClientInput input) {
		Input4 input4 = Input4.as(input.keyPresses);
		return original || input4.ana() || input4.kata();
	}
}
