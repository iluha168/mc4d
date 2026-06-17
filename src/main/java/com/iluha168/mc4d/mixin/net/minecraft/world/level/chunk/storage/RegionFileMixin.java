package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.storage;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RegionFile.class)
class RegionFileMixin {
	@ModifyConstant(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", constant = @Constant(intValue = 8192))
	int init_headerBytes(int headerBytes) {
		return headerBytes * ChunkPos.REGION_SIZE;
	}
	@ModifyConstant(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", constant = @Constant(intValue = 1024))
	int init_chunksPerRegion(int chunksPerRegion) {
		return chunksPerRegion * ChunkPos.REGION_SIZE;
	}
	@ModifyConstant(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", constant = @Constant(intValue = 4096))
	int init_timestampTableOffset(int locationTableBytes) {
		return locationTableBytes * ChunkPos.REGION_SIZE;
	}
	@Definition(id = "usedSectors", field = "Lnet/minecraft/world/level/chunk/storage/RegionFile;usedSectors:Lnet/minecraft/world/level/chunk/storage/RegionBitmap;")
	@Definition(id = "force", method = "Lnet/minecraft/world/level/chunk/storage/RegionBitmap;force(II)V")
	@Expression("this.usedSectors.force(0, @(?))")
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	int init_reservedHeaderSectors(int headerSectors) {
		return headerSectors * ChunkPos.REGION_SIZE;
	}
	@Definition(id = "sectorNumber", local = @Local(type = int.class, name = "sectorNumber"))
	@Expression("sectorNumber < @(?)") // Ide is lying
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	int init_minDataSector(int minSector) {
		return minSector * ChunkPos.REGION_SIZE;
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

	@Overwrite // IDK how to change this properly
	@Deprecated
	private static int getOffsetIndex(ChunkPos pos) {
		return pos.getRegionLocalX() + (pos.getRegionLocalZ() + ChunkPos4.as(pos).getRegionLocalW() * ChunkPos.REGION_SIZE) * ChunkPos.REGION_SIZE;
	}
}
