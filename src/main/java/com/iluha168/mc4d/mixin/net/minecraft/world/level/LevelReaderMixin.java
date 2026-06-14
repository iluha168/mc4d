package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelReader.class)
interface LevelReaderMixin extends LevelReader4 {
	@Shadow
	int getMinY();

	@Redirect(method = "getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)I", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelReader;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
	))
	private static int getHeight_BlockPos(LevelReader instance, Heightmap.Types types, int x, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((LevelReader4) instance).getHeight(types, x, z, Vec4i.getW(pos));
	}

	@Definition(id = "hasChunksAt", method = "Lnet/minecraft/world/level/LevelReader;hasChunksAt(IIIIII)Z")
	@Expression("this.hasChunksAt(?, ?, ?, ?, ?, ?)")
	@Redirect(method = "getBlockStatesIfLoaded", at = @At("MIXINEXTRAS:EXPRESSION"))
	default boolean getBlockStatesIfLoaded(LevelReader This, int x0, int y0, int z0, int x1, int y1, int z1, @Local(argsOnly = true, name = "box") AABB box) {
		if (!(box instanceof AABB4 box4)) throw Err4.container3();
		final int w0 = Mth.floor(box4.minW);
		final int w1 = Mth.floor(box4.maxW);
		return this.hasChunksAt(x0, y0, z0, w0, x1, y1, z1, w1);
	}

	@Overwrite
	@Deprecated
	default Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ) {
		throw Err4.arguments3("BiomeManager4.NoiseBiomeSource#getNoiseBiome");
	}

	@Redirect(method = "getHeightmapPos", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelReader;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
	))
	default int getHeightmapPos_getHeight(LevelReader This, Heightmap.Types types, int x, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((LevelReader4) This).getHeight(types, x, z, Vec4i.getW(pos));
	}
	@Redirect(method = "getHeightmapPos", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	default BlockPos getHeightmapPos_new(int x, int y, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return BlockPos4.from(x, y, z, Vec4i.getW(pos));
	}

	@WrapOperation(method = "canSeeSkyFromBelowWater", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	default BlockPos canSeeSkyFromBelowWater(int x, int y, int z, Operation<BlockPos> original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		BlockPos res = original.call(x, y, z);
		Vec4i.setW(res, Vec4i.getW(pos));
		return res;
	}

	@Redirect(method = "getChunk(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/ChunkAccess;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelReader;getChunk(II)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	default ChunkAccess getChunk_BlockPos(LevelReader instance, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((LevelReader4) instance).getChunk(chunkX, chunkZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}
	@Overwrite
	@Deprecated
	default ChunkAccess getChunk(int chunkX, int chunkZ) {
		throw Err4.arguments2("LevelReader4#getChunk");
	}
	@Overwrite
	@Deprecated
	default ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus status) {
		throw Err4.arguments2("LevelReader4#getChunk");
	}

	@Overwrite
	@Deprecated
	default @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
		throw Err4.arguments2("LevelReader4#getChunkForCollisions");
	}

	@Definition(id = "z1", local = @Local(type = int.class, name = "z1"))
	@Expression("z1 = @(?)")
	@Inject(method = "containsAnyLiquid", at = @At("MIXINEXTRAS:EXPRESSION"))
	default void containsAnyLiquid_w0_w1(
		AABB box, CallbackInfoReturnable<Boolean> cir,
		@Share("w0") LocalIntRef w0,
		@Share("w1") LocalIntRef w1
	) {
		if (!(box instanceof AABB4 box4)) throw Err4.container3();
		w0.set(Mth.floor(box4.minW));
		w1.set(Mth.ceil (box4.maxW));
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "z0", local = @Local(type = int.class, name = "z0"))
	@Expression("z = z0")
	@Inject(method = "containsAnyLiquid", at = @At("MIXINEXTRAS:EXPRESSION"))
	default void containsAnyLiquid_w(AABB box, CallbackInfoReturnable<Boolean> cir, @Share("w") LocalIntRef w, @Share("w0") LocalIntRef w0) {
		w.set(w0.get());
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "containsAnyLiquid", at = @At("MIXINEXTRAS:EXPRESSION"))
	default int containsAnyLiquid_incrementW(int one, @Share("w") LocalIntRef w, @Share("w0") LocalIntRef w0, @Share("w1") LocalIntRef w1) {
		w.set(w.get() + 1);
		if (w.get() < w1.get()) return 0;
		w.set(w0.get());
		return 1;
	}
	@Redirect(method = "containsAnyLiquid", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	default BlockPos.MutableBlockPos containsAnyLiquid_set(BlockPos.MutableBlockPos instance, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) instance).set(x, y, z, w.get());
	}

	@ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = 30000000))
	private static int getMaxLocalRawBrightness(int constant) {
		return Level4.MAX_LEVEL_SIZE;
	}
	@ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = -30000000))
	private static int getMaxLocalRawBrightness_(int constant) {
		return -Level4.MAX_LEVEL_SIZE;
	}
	@Expression("? < ?")
	@ModifyExpressionValue(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static boolean getMaxLocalRawBrightness(boolean original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = Vec4i.getW(pos);
		return original && w >= -Level4.MAX_LEVEL_SIZE && w < Level4.MAX_LEVEL_SIZE;
	}

	@Overwrite
	@Deprecated
	default boolean hasChunkAt(int blockX, int blockZ) {
		throw Err4.arguments2("LevelReader4#hasChunkAt");
	}

	@Redirect(method = "hasChunkAt(Lnet/minecraft/core/BlockPos;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelReader;hasChunkAt(II)Z"
	))
	default boolean hasChunkAt_BlockPos(LevelReader instance, int blockX, int blockZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((LevelReader4) instance).hasChunkAt(blockX, blockZ, Vec4i.getW(pos));
	}

	@Definition(id = "hasChunksAt", method = "Lnet/minecraft/world/level/LevelReader;hasChunksAt(IIIIII)Z")
	@Expression("this.hasChunksAt(?, ?, ?, ?, ?, ?)")
	@Redirect(method = "hasChunksAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
	default boolean hasChunksAt(
		LevelReader This, int x0, int y0, int z0, int x1, int y1, int z1,
		@Local(argsOnly = true, name = "pos0") BlockPos pos0,
		@Local(argsOnly = true, name = "pos1") BlockPos pos1
	) {
		return this.hasChunksAt(x0, y0, z0, Vec4i.getW(pos0), x1, y1, z1, Vec4i.getW(pos1));
	}

	@Overwrite
	@Deprecated
	default boolean hasChunksAt(int x0, int y0, int z0, int x1, int y1, int z1) {
		throw Err4.arguments3("LevelReader4#hasChunksAt4");
	}

	@Overwrite
	@Deprecated
	default boolean hasChunksAt(int x0, int z0, int x1, int z1) {
		throw Err4.arguments2("LevelReader4#hasChunksAt");
	}
}
