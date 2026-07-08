package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.ChunkGeneratorMixin;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.levelgen.DebugLevelSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugLevelSource.class)
class DebugLevelSourceMixin extends ChunkGeneratorMixin implements DebugLevelSource4 {
	@ModifyExpressionValue(method = "applyBiomeDecoration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getPos()Lnet/minecraft/world/level/ChunkPos;"
	))
	ChunkPos applyBiomeDecoration_chunkW(ChunkPos centerPos, @Share("chunkW") LocalIntRef chunkW) {
		chunkW.set(ChunkPos4.as(centerPos).w());
		return centerPos;
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(0)")
	@Inject(method = "applyBiomeDecoration", at = @At("MIXINEXTRAS:EXPRESSION"))
	void applyBiomeDecoration_w(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(0);
	}
	// This does apply properly, IDE is lying. Hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "applyBiomeDecoration", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int applyBiomeDecoration_incrementW(
		int one,
		@Share("w") LocalIntRef w
	) {
		w.set(w.get() + 1);
		if (w.get() < LevelChunkSection.SECTION_WIDTH) return 0;
		w.set(0);
		return 1;
	}
	@Definition(id = "worldZ", local = @Local(type = int.class, name = "worldZ"))
	@Expression("worldZ = @(?)")
	@Inject(method = "applyBiomeDecoration", at = @At("MIXINEXTRAS:EXPRESSION"))
	void applyBiomeDecoration_worldW(
		WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci,
		@Share("chunkW") LocalIntRef chunkW,
		@Share("w") LocalIntRef w,
		@Share("worldW") LocalIntRef worldW
	) {
		worldW.set(SectionPos.sectionToBlockCoord(chunkW.get(), w.get()));
	}
	@Redirect(method = "applyBiomeDecoration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos applyBiomeDecoration_set(BlockPos.MutableBlockPos instance, int x, int y, int z, @Share("worldW") LocalIntRef worldW) {
		return ((BlockPos4.MutableBlockPos) instance).set(x, y, z, worldW.get());
	}
	@Redirect(method = "applyBiomeDecoration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/DebugLevelSource;getBlockStateFor(II)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState applyBiomeDecoration(int worldX, int worldZ, @Share("worldW") LocalIntRef worldW) {
		return DebugLevelSource4.getBlockStateFor(worldX, worldZ, worldW.get());
	}

	@Overwrite
	@Deprecated
	public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
		throw Err4.arguments2("ChunkGenerator4#getBaseHeight");
	}
	@Override
	public int getBaseHeight(int x, int z, int w, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
		return 0;
	}

	@Overwrite
	@Deprecated
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
		throw Err4.arguments2("ChunkGenerator4#getBaseColumn");
	}
	@Override
	public NoiseColumn getBaseColumn(int x, int z, int w, LevelHeightAccessor heightAccessor, RandomState randomState) {
		return new NoiseColumn(0, new BlockState[0]);
	}

	@Overwrite
	@Deprecated
	public static BlockState getBlockStateFor(int worldX, int worldZ) {
		throw Err4.arguments2("DebugLevelSource4#getBlockStateFor");
	}
}
