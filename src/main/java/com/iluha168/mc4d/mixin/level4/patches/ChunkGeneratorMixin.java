package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
class ChunkGeneratorMixin {
	// TODO the rest

	@Redirect(method = "lambda$applyBiomeDecoration$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/WorldGenLevel;getChunk(II)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	private static ChunkAccess applyBiomeDecoration_fillSections(WorldGenLevel level, int chunkX, int chunkZ, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		return ((LevelReader4) level).getChunk(chunkX, chunkZ, ChunkPos4.as(chunkPos).w());
	}

	// TODO the rest

	@Definition(id = "targetZ", local = @Local(type = int.class, name = "targetZ"))
	@Definition(id = "chunkPos", local = @Local(type = ChunkPos.class, name = "chunkPos"))
	@Definition(id = "z", method = "Lnet/minecraft/world/level/ChunkPos;z()I")
	@Expression("targetZ = chunkPos.z()")
	@Inject(method = "createReferences", at = @At("MIXINEXTRAS:EXPRESSION"))
	void createReferences_targetW(
		WorldGenLevel level, StructureManager structureManager, ChunkAccess centerChunk, CallbackInfo ci,
		@Local(name = "chunkPos") ChunkPos chunkPos,
		@Share("targetW") LocalIntRef targetW
	) {
		targetW.set(ChunkPos4.as(chunkPos).w());
	}
	@Definition(id = "targetBlockZ", local = @Local(type = int.class, name = "targetBlockZ"))
	@Definition(id = "chunkPos", local = @Local(type = ChunkPos.class, name = "chunkPos"))
	@Definition(id = "getMinBlockZ", method = "Lnet/minecraft/world/level/ChunkPos;getMinBlockZ()I")
	@Expression("targetBlockZ = chunkPos.getMinBlockZ()")
	@Inject(method = "createReferences", at = @At("MIXINEXTRAS:EXPRESSION"))
	void createReferences_targetBlockW(
		WorldGenLevel level, StructureManager structureManager, ChunkAccess centerChunk, CallbackInfo ci,
		@Local(name = "chunkPos") ChunkPos chunkPos,
		@Share("targetBlockW") LocalIntRef targetBlockW
	) {
		targetBlockW.set(ChunkPos4.as(chunkPos).getMinBlockW());
	}
	@Definition(id = "sourceZ", local = @Local(type = int.class, name = "sourceZ"))
	@Definition(id = "targetZ", local = @Local(type = int.class, name = "targetZ"))
	@Expression("sourceZ = @(targetZ - ?)")
	@Inject(method = "createReferences", at = @At("MIXINEXTRAS:EXPRESSION"))
	void createReferences_sourceW(
		WorldGenLevel level, StructureManager structureManager, ChunkAccess centerChunk, CallbackInfo ci,
		@Share("targetW") LocalIntRef targetW,
		@Local(name = "range") int range,
		@Share("sourceW") LocalIntRef sourceW
	) {
		sourceW.set(targetW.get() - range);
	}
	// This does apply properly, IDE is lying
	@Definition(id = "sourceZ", local = @Local(type = int.class, name = "sourceZ"))
	@Expression("sourceZ = sourceZ + @(1)")
	@ModifyExpressionValue(method = "createReferences", at = @At("MIXINEXTRAS:EXPRESSION"))
	int createReferences_incrementSourceW(
		int one,
		@Share("targetW") LocalIntRef targetW,
		@Local(name = "range") int range,
		@Share("sourceW") LocalIntRef sourceW
	) {
		sourceW.set(sourceW.get() + 1);
		if (sourceW.get() <= targetW.get() + range) return 0;
		sourceW.set(targetW.get() - range);
		return 1;
	}
	@Redirect(method = "createReferences", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long createReferences_sourceChunkKey(int x, int z, @Share("sourceW") LocalIntRef sourceW) {
		return ChunkPos4.pack(x, z, sourceW.get());
	}
	@Redirect(method = "createReferences", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/WorldGenLevel;getChunk(II)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess createReferences_getChunk(WorldGenLevel level, int sourceX, int sourceZ, @Share("sourceW") LocalIntRef sourceW) {
		return ((LevelReader4) level).getChunk(sourceX, sourceZ, sourceW.get());
	}
	// TODO createReferences_intersects

	// TODO the rest
}
