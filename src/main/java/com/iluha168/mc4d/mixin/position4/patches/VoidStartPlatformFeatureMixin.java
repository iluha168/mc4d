package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.expression.Definition;import com.llamalad7.mixinextras.expression.Expression;import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.VoidStartPlatformFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VoidStartPlatformFeature.class)
class VoidStartPlatformFeatureMixin {
	@Definition(id = "PLATFORM_OFFSET", field = "Lnet/minecraft/world/level/levelgen/feature/VoidStartPlatformFeature;PLATFORM_OFFSET:Lnet/minecraft/core/BlockPos;")
	@Expression("PLATFORM_OFFSET = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos PLATFORM_OFFSET(BlockPos original) {
		Vec4i.setW(original, original.getZ());
		return original;
	}
}
