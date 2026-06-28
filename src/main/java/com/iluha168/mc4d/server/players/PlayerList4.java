package com.iluha168.mc4d.server.players;

import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link net.minecraft.server.players.PlayerList}.
 */
public interface PlayerList4 {
	void broadcast(@Nullable Player except, double x, double y, double z, double w, double range, ResourceKey<Level> dimension, Packet<?> packet);
}
