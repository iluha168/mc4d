package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TheEndGatewayRenderer.class)
class TheEndGatewayRendererMixin {
	@Redirect(method = "getRenderBoundingBox(Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;)Lnet/minecraft/world/phys/AABB;", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/AABB;INFINITE:Lnet/minecraft/world/phys/AABB;",
		opcode = Opcodes.GETSTATIC
	))
	AABB getRenderBoundingBox() {
		return AABB4.INFINITE;
	}
}
