package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonHeadRenderer.class)
class PistonHeadRendererMixin {
	// TODO extractRenderState state.wOffset when 4D renderer

	@Redirect(method = "getRenderBoundingBox(Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;)Lnet/minecraft/world/phys/AABB;", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/AABB;INFINITE:Lnet/minecraft/world/phys/AABB;",
		opcode = Opcodes.GETSTATIC
	))
	AABB getRenderBoundingBox() {
		return AABB4.INFINITE;
	}
}
