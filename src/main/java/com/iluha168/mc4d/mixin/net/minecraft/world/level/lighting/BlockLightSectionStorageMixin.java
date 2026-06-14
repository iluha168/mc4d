package com.iluha168.mc4d.mixin.net.minecraft.world.level.lighting;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.level.chunk.DataLayer4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockLightSectionStorage.class)
class BlockLightSectionStorageMixin {
	@Redirect(method = "getLightValue", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/DataLayer;get(III)I"
	))
	int getLightValue(DataLayer instance, int x, int y, int z, @Local(argsOnly = true, name = "blockNode") long blockNode) {
		return ((DataLayer4) instance).get(x, y, z, SectionPos.sectionRelative(BlockPos4.getW(blockNode)));
	}
}
