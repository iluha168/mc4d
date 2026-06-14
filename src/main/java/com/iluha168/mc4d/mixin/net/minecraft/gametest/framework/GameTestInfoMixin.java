package com.iluha168.mc4d.mixin.net.minecraft.gametest.framework;

import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameTestInfo.class)
class GameTestInfoMixin {
	@ModifyExpressionValue(method = "createTestInstanceBlock", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i createTestInstanceBlock(Vec3i original) {
		Vec4i.setW(original, original.getZ());
		return original;
	}
}
