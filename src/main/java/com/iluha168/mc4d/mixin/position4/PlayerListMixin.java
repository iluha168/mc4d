package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.server.network.ServerGamePacketListenerImpl4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Redirect(method = "placeNewPlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"
	))
	void placeNewPlayerTeleport(
		ServerGamePacketListenerImpl playerConnection,
		double x, double y, double z, float yRot, float xRot,
		@Local(argsOnly = true, name = "player") ServerPlayer player
	) {
		((ServerGamePacketListenerImpl4) playerConnection).teleport(
			new Vec4(x, y, z, ((Entity4) player).getW()),
			yRot, xRot
		);
	}

	@Redirect(method = "respawn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;snapTo(DDDFF)V"
	))
	void respawn(ServerPlayer player, double x, double y, double z, float yRot, float xRot, @Local(name = "pos") Vec3 pos) {
		double w = ((Position4) pos).w();
		player.snapTo(new Vec4(x, y, z, w), yRot, xRot);
	}
}
