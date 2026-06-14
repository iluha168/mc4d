package com.iluha168.mc4d.mixin.net.neoforged.neoforge.common.extensions;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.extensions.ILevelReaderExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ILevelReaderExtension.class)
interface ILevelReaderExtensionMixin {
	@Redirect(method = "isAreaLoaded", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	default BlockPos isAreaLoaded(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, z);
	}
}
