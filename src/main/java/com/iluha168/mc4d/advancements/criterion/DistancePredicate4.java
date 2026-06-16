package com.iluha168.mc4d.advancements.criterion;

import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;

/**
 * Implemented by {@link DistancePredicate}.
 */
public interface DistancePredicate4 {
	MinMaxBounds.Doubles w();
	void setW(MinMaxBounds.Doubles w);

	static DistancePredicate4 as(DistancePredicate predicate) {
		return (DistancePredicate4) (Object) predicate;
	}

	static DistancePredicate from(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles w, MinMaxBounds.Doubles horizontal, MinMaxBounds.Doubles absolute) {
		DistancePredicate predicate = new DistancePredicate(x, y, z, horizontal, absolute);
		DistancePredicate4.as(predicate).setW(w);
		return predicate;
	}

	boolean matches(double x0, double y0, double z0, double w0, double x1, double y1, double z1, double w1);
}
