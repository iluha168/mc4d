package com.iluha168.mc4d.advancements.criterion;

import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/**
 * Implemented by {@link LocationPredicate}.
 */
public interface LocationPredicate4 {
	static LocationPredicate4 as(LocationPredicate predicate) {
		return (LocationPredicate4) (Object) predicate;
	}

	boolean matches(ServerLevel level, double x, double y, double z, double w);

	/**
	 * Implemented by {@link LocationPredicate.Builder}.
	 */
	interface Builder {
		// TODO setX and setZ have no references, presumably these predicates are deserialized through LocationPredicate.CODEC. Find the assets and add W.
		LocationPredicate.Builder setW(MinMaxBounds.Doubles w);
	}

	/**
	 * Implemented by {@link LocationPredicate.PositionPredicate}.
	 */
	interface PositionPredicate {
		MinMaxBounds.Doubles w();
		void setW(MinMaxBounds.Doubles w);

		static LocationPredicate4.PositionPredicate as(LocationPredicate.PositionPredicate predicate) {
			return (LocationPredicate4.PositionPredicate) (Object) predicate;
		}

		static LocationPredicate.PositionPredicate from(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles w) {
			LocationPredicate.PositionPredicate predicate = new LocationPredicate.PositionPredicate(x, y, z);
			PositionPredicate.as(predicate).setW(w);
			return predicate;
		}

		static Optional<LocationPredicate.PositionPredicate> of(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles w) {
			return x.isAny() && y.isAny() && z.isAny() && w.isAny() ? Optional.empty() : Optional.of(PositionPredicate.from(x, y, z, w));
		}

		boolean matches(double x, double y, double z, double w);
	}
}

