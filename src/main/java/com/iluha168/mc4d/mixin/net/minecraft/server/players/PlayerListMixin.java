package com.iluha168.mc4d.mixin.net.minecraft.server.players;

import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.server.network.ServerGamePacketListenerImpl4;
import com.iluha168.mc4d.server.players.PlayerList4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.border.BorderChangeListener4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin implements PlayerList4 {
	// TODO the rest

	@Shadow
	@Final
	private List<ServerPlayer> players;

	// TODO placeNewPlayer
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

	// TODO the rest

	@Mixin(targets = "net/minecraft/server/players/PlayerList$1")
	static class addWorldborderListenerMixin implements BorderChangeListener4 {
		@Shadow
		@Final
		PlayerList this$0;

		@Shadow
		@Final
		ServerLevel val$level;

		@Overwrite
		@Deprecated
		public void onSetCenter(WorldBorder border, double x, double z) {
			throw Err4.arguments2("BorderChangeListener4#onSetCenter");
		}
		@Override
		public void onSetCenter(WorldBorder border, double x, double z, double w) {
			this$0.broadcastAll(new ClientboundSetBorderCenterPacket(border), val$level.dimension());
		}
	}

	// TODO the rest

	@Redirect(method = "respawn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;snapTo(DDDFF)V"
	))
	void respawn(ServerPlayer player, double x, double y, double z, float yRot, float xRot, @Local(name = "pos") Vec3 pos) {
		double w = ((Position4) pos).w();
		player.snapTo(new Vec4(x, y, z, w), yRot, xRot);
	}
	// TODO respawn
	// TODO respawn

	// TODO the rest

	@Overwrite
	@Deprecated
	public void broadcast(@Nullable Player except, double x, double y, double z, double range, ResourceKey<Level> dimension, Packet<?> packet) {
		throw Err4.arguments3("PlayerList4#broadcast");
	}
	@Override
	public void broadcast(@Nullable Player except, double x, double y, double z, double w, double range, ResourceKey<Level> dimension, Packet<?> packet) {
		for (ServerPlayer player : this.players) {
			if (player != except && player.level().dimension() == dimension) {
				final double xd = x - player.getX();
				final double yd = y - player.getY();
				final double zd = z - player.getZ();
				final double wd = w - ((Entity4) player).getW();
				if (xd * xd + yd * yd + zd * zd + wd * wd < range * range) {
					player.connection.send(packet);
				}
			}
		}
	}

	// TODO the rest
}
