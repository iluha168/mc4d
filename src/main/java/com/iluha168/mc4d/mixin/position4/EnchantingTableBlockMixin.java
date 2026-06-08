package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(EnchantingTableBlock.class)
class EnchantingTableBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosedStream(IIIIII)Ljava/util/stream/Stream;"
	))
	private static Stream<BlockPos> BOOKSHELF_OFFSETS(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return BlockPos4.betweenClosedStream(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	@Expression("? == 2")
	@ModifyExpressionValue(method = "lambda$static$0", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static boolean BOOKSHELF_OFFSETS_filter(boolean original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return original || Math.abs(Vec4i.getW(pos)) == 2;
	}
}
