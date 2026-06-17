package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.storage;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RegionFileStorage.class)
public class RegionFileStorageMixin {
	@Redirect(method = "getRegionFile", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long getRegionFile_key(int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ChunkPos4.pack(x, z, ChunkPos4.as(pos).getRegionW());
	}
	@ModifyConstant(method = "getRegionFile", constant = @Constant(intValue = 16*16))
	int getRegionFile_MAX_CACHE_SIZE(int constant) {
		return 16*16*16;
	}
	@Definition(id = "resolve", method = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;")
	@Definition(id = "pos", local = @Local(type = ChunkPos.class, name = "pos", argsOnly = true))
	@Definition(id = "getRegionZ", method = "Lnet/minecraft/world/level/ChunkPos;getRegionZ()I")
	@Definition(id = "getRegionX", method = "Lnet/minecraft/world/level/ChunkPos;getRegionX()I")
	@Expression("?.resolve(? + pos.getRegionX() + ? + pos.getRegionZ() + @(?))")
	@ModifyExpressionValue(method = "getRegionFile", at = @At("MIXINEXTRAS:EXPRESSION"))
	String getRegionFile_fileName(String suffix, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return "." + ChunkPos4.as(pos).getRegionW() + suffix;
	}
}
