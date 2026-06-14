package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureManager.class)
class StructureManagerMixin {
	@Redirect(method = "startsForStructure(Lnet/minecraft/world/level/ChunkPos;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess startsForStructure(LevelAccessor level, int x, int z, ChunkStatus chunkStatus, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ((LevelReader4) level).getChunk(x, z, ChunkPos4.as(pos).w(), chunkStatus);
	}

	@Redirect(method = "startsForStructure(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/levelgen/structure/Structure;)Ljava/util/List;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess startsForStructure(LevelAccessor level, int x, int z, ChunkStatus chunkStatus, @Local(argsOnly = true, name = "pos") SectionPos pos) {
		return ((LevelReader4) level).getChunk(x, z, ((SectionPos4) pos).w(), chunkStatus);
	}

	@Redirect(method = "fillStartsForStructure", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess fillStartsForStructure(LevelAccessor level, int x, int z, ChunkStatus chunkStatus, @Local(name = "sectionPos") SectionPos sectionPos) {
		return ((LevelReader4) level).getChunk(x, z, ((SectionPos4) sectionPos).w(), chunkStatus);
	}

	@Redirect(method = "hasAnyStructureAt", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess hasAnyStructureAt(LevelAccessor level, int x, int z, ChunkStatus chunkStatus, @Local(name = "sectionPos") SectionPos sectionPos) {
		return ((LevelReader4) level).getChunk(x, z, ((SectionPos4) sectionPos).w(), chunkStatus);
	}

	@Redirect(method = "getAllStructuresAt", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess getAllStructuresAt(LevelAccessor level, int x, int z, ChunkStatus chunkStatus, @Local(name = "sectionPos") SectionPos sectionPos) {
		return ((LevelReader4) level).getChunk(x, z, ((SectionPos4) sectionPos).w(), chunkStatus);
	}
}
