package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
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

	@Redirect(method = "isValidBookShelf", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos isValidBookShelf(BlockPos pos, int x, int y, int z, @Local(argsOnly = true, name = "offset") BlockPos offset) {
		return ((BlockPos4) pos).offset(x, y, z, Vec4i.getW(offset) / 2);
	}

	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Local(name = "offset") BlockPos offset
	) {
		final double w = Vec4i.getW(pos) + 0.5;
		final double wd = Vec4i.getW(offset) + random.nextFloat() - 0.5;
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wd);
	}
}
