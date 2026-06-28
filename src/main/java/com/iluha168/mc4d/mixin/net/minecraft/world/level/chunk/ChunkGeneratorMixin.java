package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.level.chunk.ChunkGenerator4;
import com.iluha168.mc4d.world.level.levelgen.WorldgenRandom4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
abstract class ChunkGeneratorMixin implements ChunkGenerator4 {
	// TODO findNearestMapStructure
	// TODO getNearestGeneratedStructure
	// TODO getNearestGeneratedStructure
	// TODO getStructureGeneratingAt

	@Redirect(method = "applyBiomeDecoration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/WorldgenRandom;setDecorationSeed(JII)J"
	))
	long applyBiomeDecoration_setDecorationSeed(WorldgenRandom instance, long seed, int chunkX, int chunkZ, @Local(name = "origin") BlockPos origin) {
		return ((WorldgenRandom4) instance).setDecorationSeed(seed, chunkX, chunkZ, Vec4i.getW(origin));
	}
	@Redirect(method = "lambda$applyBiomeDecoration$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/WorldGenLevel;getChunk(II)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	private static ChunkAccess applyBiomeDecoration_fillSections(WorldGenLevel level, int chunkX, int chunkZ, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		return ((LevelReader4) level).getChunk(chunkX, chunkZ, ChunkPos4.as(chunkPos).w());
	}
	@Definition(id = "setDetail", method = "Lnet/minecraft/CrashReportCategory;setDetail(Ljava/lang/String;Ljava/lang/Object;)Lnet/minecraft/CrashReportCategory;")
	@Definition(id = "z", method = "Lnet/minecraft/world/level/ChunkPos;z()I")
	@Expression("?.setDetail(?, ?.z())")
	@ModifyExpressionValue(method = "applyBiomeDecoration", at = @At("MIXINEXTRAS:EXPRESSION"))
	CrashReportCategory applyBiomeDecoration_CenterW(CrashReportCategory original, @Local(name = "centerPos") ChunkPos centerPos) {
		return original.setDetail("CenterW", ChunkPos4.as(centerPos).w());
	}

	// TODO getWritableArea
	// TODO createStructures

	@ModifyConstant(method = "createReferences", constant = @Constant(intValue = 8))
	int createReferences_range(int constant) {
		// Here we make chunk generation radius never bigger than 1, because 4D chunks are huge
		// See ChunkStepMixin
		return 1;
	}
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
	@Redirect(method = "createReferences", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;intersects(IIII)Z"
	))
	boolean createReferences_intersects(BoundingBox instance, int minX, int minZ, int maxX, int maxZ, @Share("targetBlockW") LocalIntRef targetBlockW) {
		return ((BoundingBox4) instance).intersects(minX, minZ, targetBlockW.get(), maxX, maxZ, targetBlockW.get() + 15);
	}

	@Override
	public abstract int getBaseHeight(int x, int z, int w, final Heightmap.Types type, final LevelHeightAccessor heightAccessor, final RandomState randomState);

	@Overwrite
	@Deprecated
	public int getFirstFreeHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
		throw Err4.arguments2("ChunkGenerator4#getFirstFreeHeight");
	}
	@Override
	public int getFirstFreeHeight(int x, int z, int w, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
		return this.getBaseHeight(x, z, w, type, heightAccessor, randomState);
	}

	@Overwrite
	@Deprecated
	public int getFirstOccupiedHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
		throw Err4.arguments2("ChunkGenerator4#getFirstOccupiedHeight");
	}
	@Override
	public int getFirstOccupiedHeight(int x, int z, int w, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
		return this.getBaseHeight(x, z, w, type, heightAccessor, randomState) - 1;
	}
}
