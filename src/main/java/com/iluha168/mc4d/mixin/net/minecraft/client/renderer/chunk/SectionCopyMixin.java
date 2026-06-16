package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.chunk.PalettedContainer4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionCopy.class)
class SectionCopyMixin {
	// TODO DebugLevelSource
	@SuppressWarnings("unchecked")
	@Redirect(method = "getBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/PalettedContainer;get(III)Ljava/lang/Object;"
	))
	private <T> T getBlockState(PalettedContainer<T> section, int x, int y, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((PalettedContainer4<T>) section).get(x, y, z, SectionPos.sectionRelative(Vec4i.getW(pos)));
	}
	// TODO formatLocation
}
