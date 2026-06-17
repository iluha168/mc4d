package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components.debug;

import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DebugEntryLookingAt.DebugEntryLookingAtState.class)
class DebugEntryLookingAtMixin {
	@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
	@Expression("? + ?.getZ()")
	@ModifyExpressionValue(method = "extractInfo", at = @At("MIXINEXTRAS:EXPRESSION"))
	String extractInfo(String original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return original + ", " + Vec4i.getW(pos);
	}
}
