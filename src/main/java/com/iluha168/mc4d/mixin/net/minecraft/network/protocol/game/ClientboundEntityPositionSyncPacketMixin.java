package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.PositionMoveRotation4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientboundEntityPositionSyncPacket.class)
class ClientboundEntityPositionSyncPacketMixin {
	@ModifyExpressionValue(method = "of", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/entity/PositionMoveRotation;"
	))
	private static PositionMoveRotation of(PositionMoveRotation original, @Local(argsOnly = true, name = "entity") Entity entity) {
		final PositionMoveRotation4 original4 = PositionMoveRotation4.as(original);
		final Entity4 entity4 = (Entity4) entity;
		original4.setWRot(entity4.getWRot());
		original4.setVRot(entity4.getVRot());
		return original;
	}
}
