package com.iluha168.mc4d.mixin.level4;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// TODO: Remove each flag override!
@Mixin(SharedConstants.class)
class SharedConstantsMixin {
	@Definition(id = "DEBUG_DISABLE_BLENDING", field = "Lnet/minecraft/SharedConstants;DEBUG_DISABLE_BLENDING:Z")
	@Expression("DEBUG_DISABLE_BLENDING = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean DEBUG_DISABLE_BLENDING(boolean original) {
		return true;
	}
}
