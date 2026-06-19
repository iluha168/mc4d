package com.iluha168.mc4d.mixin.net.neoforged.neoforge.client.extensions;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.IBlockEntityRendererExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IBlockEntityRendererExtension.class)
interface IBlockEntityRendererExtensionMixin {
	@Redirect(method = "getRenderBoundingBox", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	default AABB getRenderBoundingBox(BlockPos pos) {
		return new AABB4(pos);
	}
}
