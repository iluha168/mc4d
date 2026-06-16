package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.entity;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
class EntityRendererMixin {
	// TODO everything else

	@Redirect(method = "extractShadow", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos extractShadow(BlockPos.MutableBlockPos instance, int x, int y, int z) {
		// TODO use actual W value for 4D renderer
		return ((BlockPos4.MutableBlockPos) instance).set(x, y, z, y);
	}

	// TODO everything else
}
