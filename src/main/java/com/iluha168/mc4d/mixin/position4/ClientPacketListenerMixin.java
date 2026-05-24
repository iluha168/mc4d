package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Redirect(method = "handleMovePlayer", at = @At(
		value = "NEW",
		target = "(DDDFFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$PosRot;"
	))
	ServerboundMovePlayerPacket.PosRot handleMovePlayer4(
		double x, double y, double z, float yRot, float xRot, boolean onGround, boolean horizontalCollision,
		@Local(name = "player") Player player
	) {
		final double w = ((Entity4) player).getW();
		return new ServerboundMovePlayerPacket.PosRot(new Vec4(x, y, z, w), yRot, xRot, onGround, horizontalCollision);
	}
}
