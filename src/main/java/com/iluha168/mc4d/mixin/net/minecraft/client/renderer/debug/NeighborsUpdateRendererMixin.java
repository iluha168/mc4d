package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.debug;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NeighborsUpdateRenderer.class)
class NeighborsUpdateRendererMixin {
	// TODO everything

	@Redirect(method = "emitGizmos", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB emitGizmos(BlockPos pos) {
		return new AABB4(pos);
	}
}
