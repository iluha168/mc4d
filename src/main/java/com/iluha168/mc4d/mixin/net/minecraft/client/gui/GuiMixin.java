package com.iluha168.mc4d.mixin.net.minecraft.client.gui;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
class GuiMixin {
	// TODO the rest

	@Redirect(method = "updateVignetteBrightness", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos updateVignetteBrightness(double x, double y, double z, @Local(argsOnly = true, name = "camera") Entity camera) {
		return BlockPos4.containing(x, y, z, ((Entity4) camera).getW());
	}

	// TODO the rest
}
