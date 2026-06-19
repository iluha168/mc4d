package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelEventHandler.class)
class LevelEventHandlerMixin {
	// TODO everything

	@Redirect(method = "notifyNearbyEntities", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB notifyNearbyEntities(BlockPos pos) {
		return new AABB4(pos);
	}
}
