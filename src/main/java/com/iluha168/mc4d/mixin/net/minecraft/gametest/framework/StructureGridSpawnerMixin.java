package com.iluha168.mc4d.mixin.net.minecraft.gametest.framework;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.StructureGridSpawner;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureGridSpawner.class)
class StructureGridSpawnerMixin {
	@Redirect(method = "<init>", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB init_rowBounds(BlockPos pos) {
		return new AABB4(pos);
	}

	@Redirect(method = "onBatchStart", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB onBatchStart_rowBounds(BlockPos pos) {
		return new AABB4(pos);
	}

	@Redirect(method = "spawnStructure", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;move(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
		ordinal = 0
	))
	BlockPos.MutableBlockPos spawnStructure(BlockPos.MutableBlockPos instance, int x, int y, int z) {
		return ((BlockPos4.MutableBlockPos) instance).move(x, y, z, z);
	}
	@Redirect(method = "spawnStructure", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;move(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
		ordinal = 1
	))
	BlockPos.MutableBlockPos spawnStructure_nextRow(BlockPos.MutableBlockPos instance, int x, int y, int z) {
		return ((BlockPos4.MutableBlockPos) instance).move(x, y, z, x);
	}
}
