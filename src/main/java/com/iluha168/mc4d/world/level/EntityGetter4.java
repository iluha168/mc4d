package com.iluha168.mc4d.world.level;

import com.iluha168.mc4d.world.entity.Entity4;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

/**
 * All implementations of {@link net.minecraft.world.level.EntityGetter} also implement {@link EntityGetter4}.
 */
public interface EntityGetter4 {
	default @Nullable Player getNearestPlayer(double x, double y, double z, double w, double range, @Nullable Predicate<Entity> predicate) {
		double best = -1.0;
		Player result = null;

		for (Player player : ((EntityGetter) this).players()) {
			if (predicate == null || predicate.test(player)) {
				double dist = ((Entity4) player).distanceToSqr(x, y, z, w);
				if ((range < 0.0 || dist < range * range) && (best == -1.0 || dist < best)) {
					best = dist;
					result = player;
				}
			}
		}

		return result;
	}
	default @Nullable Player getNearestPlayer(double x, double y, double z, double w, double maxDist, boolean filterOutCreative) {
		Predicate<Entity> predicate = filterOutCreative ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
		return this.getNearestPlayer(x, y, z, w, maxDist, predicate);
	}

	default boolean hasNearbyAlivePlayer(double x, double y, double z, double w, double range) {
		for (Player player : ((EntityGetter) this).players()) {
			if (EntitySelector.NO_SPECTATORS.test(player) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player)) {
				double playerDist = ((Entity4) player).distanceToSqr(x, y, z, w);
				if (range < 0.0 || playerDist < range * range) {
					return true;
				}
			}
		}

		return false;
	}
}
