package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.storage;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RegionFile.class)
class RegionFileMixin {
	@Definition(id = "header", field = "Lnet/minecraft/world/level/chunk/storage/RegionFile;header:Ljava/nio/ByteBuffer;")
	@Definition(id = "allocateDirect", method = "Ljava/nio/ByteBuffer;allocateDirect(I)Ljava/nio/ByteBuffer;")
	@Expression("this.header = @(allocateDirect(?))")
	@ModifyArg(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int header_capacity(int capacity) {
		return capacity * ChunkPos.REGION_SIZE; // Original is Long.SIZE / 8 * ChunkPos.REGION_SIZE * ChunkPos.REGION_SIZE, I guess?
	}

	@Definition(id = "offsets", field = "Lnet/minecraft/world/level/chunk/storage/RegionFile;offsets:Ljava/nio/IntBuffer;")
	@Definition(id = "limit", method = "Ljava/nio/IntBuffer;limit(I)Ljava/nio/IntBuffer;")
	@Expression("this.offsets.limit(?)")
	@ModifyArg(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	int offsets_limit(int limit2D) {
		return limit2D * ChunkPos.REGION_SIZE; // Original is REGION_SIZE squared
	}

	@Definition(id = "pos", local = @Local(type = ChunkPos.class, name = "pos", argsOnly = true))
	@Definition(id = "z", method = "Lnet/minecraft/world/level/ChunkPos;z()I")
	@Definition(id = "x", method = "Lnet/minecraft/world/level/ChunkPos;x()I")
	@Definition(id = "externalFileName", local = @Local(type = String.class, name = "externalFileName"))
	@Expression("externalFileName = ? + pos.x() + ? + pos.z() + @(?)")
	@ModifyExpressionValue(method = "getExternalChunkPath", at = @At("MIXINEXTRAS:EXPRESSION"))
	String getExternalChunkPath(String suffix, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return "." + ChunkPos4.as(pos).w() + suffix;
	}

	@WrapMethod(method = "getOffsetIndex")
	private static int getOffsetIndex(ChunkPos pos, Operation<Integer> original) {
		return original.call(pos) * ChunkPos.REGION_SIZE + ChunkPos4.as(pos).getRegionLocalW();
	}
}
