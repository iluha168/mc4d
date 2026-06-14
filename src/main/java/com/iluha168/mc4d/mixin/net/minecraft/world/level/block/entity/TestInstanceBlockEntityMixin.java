package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TestInstanceBlockEntity.class)
class TestInstanceBlockEntityMixin {
	@Definition(id = "STRUCTURE_OFFSET", field = "Lnet/minecraft/world/level/block/entity/TestInstanceBlockEntity;STRUCTURE_OFFSET:Lnet/minecraft/core/Vec3i;")
	@Expression("STRUCTURE_OFFSET = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Vec3i STRUCTURE_OFFSET(Vec3i original) {
		Vec4i.setW(original, 0);
		return original;
	}

	@Redirect(method = "getStructureBoundingBox", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getStructureBoundingBox(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, z);
	}

	@Redirect(method = "getStructurePos()Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getStructurePos(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, z);
	}

	@Redirect(method = "getRenderableBox", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getRenderableBox(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, z);
	}

	@ModifyExpressionValue(method = "getTransformedSize", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i getTransformedSize(Vec3i original, @Local(name = "size") Vec3i size) {
		Vec4i.setW(original, Vec4i.getW(size));
		return original;
	}

	// TODO saveTest l10n
	// TODO export l10n
	// TODO runTest l10n

	@Redirect(method = "lambda$forceLoadChunks$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;setChunkForced(IIZ)Z"
	))
	private static boolean forceLoadChunks(ServerLevel instance, int chunkX, int chunkZ, boolean forced, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ((ServerLevel4) instance).setChunkForced(chunkX, chunkZ, ChunkPos4.as(pos).w(), forced);
	}

	@Redirect(method = "getStartCorner", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getStartCorner(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, 0);
	}

	@Redirect(method = "processStructureBoundary", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;",
		ordinal = 0
	))
	BlockPos processStructureBoundary_low_containing(double x, double y, double z, @Local(name = "bounds") AABB bounds) {
		if (!(bounds instanceof AABB4 bounds4)) throw Err4.container3();
		return BlockPos4.containing(x, y, z, bounds4.minW);
	}
	@Redirect(method = "processStructureBoundary", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;",
		ordinal = 1
	))
	BlockPos processStructureBoundary_high_containing(double x, double y, double z, @Local(name = "bounds") AABB bounds) {
		return BlockPos4.containing(x, y, z, ((AABB4) bounds).maxW);
	}
	@Redirect(method = "processStructureBoundary", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos processStructureBoundary_low_offset(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, z);
	}
	@Expression("? == ?")
	@ModifyExpressionValue(method = "lambda$processStructureBoundary$1", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static boolean processStructureBoundary_isNonCeilingEdge(
		boolean original,
		@Local(name = "blockPos", argsOnly = true) BlockPos blockPos,
		@Local(name = "low", argsOnly = true) BlockPos low,
		@Local(name = "high", argsOnly = true) BlockPos high
	) {
		return original
			|| Vec4i.getW(blockPos) == Vec4i.getW(low)
			|| Vec4i.getW(blockPos) == Vec4i.getW(high);
	}
}
