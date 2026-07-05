package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FillCommand.class)
class FillCommandMixin {
	@Definition(id = "area", local = @Local(name = "area", type = long.class))
	@Expression("area = @(?)")
	@ModifyExpressionValue(method = "fillBlocks", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static long fillBlocks_area(long original, @Local(argsOnly = true, name = "region") BoundingBox region) {
		return original * ((BoundingBox4) region).getWSpan();
	}
	@Redirect(method = "fillBlocks", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosed(IIIIII)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> fillBlocks_betweenClosed(
		int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
		@Local(argsOnly = true, name = "region") BoundingBox region
	) {
		final BoundingBox4 region4 = (BoundingBox4) region;
		return BlockPos4.betweenClosed(minX, minY, minZ, region4.minW(), maxX, maxY, maxZ, region4.maxW());
	}

	@Mixin(targets = "net.minecraft.server.commands.FillCommand$Mode")
	static class ModeMixin {
		@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
		@Definition(id = "maxZ", method = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;maxZ()I")
		@Expression("?.getZ() != ?.maxZ()")
		@ModifyExpressionValue(method = "lambda$static$0", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static boolean OUTLINE(boolean original, @Local(argsOnly = true, name = "r") BoundingBox r, @Local(argsOnly = true, name = "p") BlockPos p) {
			final BoundingBox4 r4 = (BoundingBox4) r;
			final double pw = Vec4i.getW(p);
			return original && pw != r4.minW() && pw != r4.maxW();
		}
		@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
		@Definition(id = "maxZ", method = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;maxZ()I")
		@Expression("?.getZ() != ?.maxZ()")
		@ModifyExpressionValue(method = "lambda$static$1", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static boolean HOLLOW(boolean original, @Local(argsOnly = true, name = "r") BoundingBox r, @Local(argsOnly = true, name = "p") BlockPos p) {
			final BoundingBox4 r4 = (BoundingBox4) r;
			final double pw = Vec4i.getW(p);
			return original && pw != r4.minW() && pw != r4.maxW();
		}
	}
}
