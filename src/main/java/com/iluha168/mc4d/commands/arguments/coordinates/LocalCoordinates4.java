package com.iluha168.mc4d.commands.arguments.coordinates;

import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link net.minecraft.commands.arguments.coordinates.LocalCoordinates}.
 */
public interface LocalCoordinates4 {
	static LocalCoordinates4 as(LocalCoordinates coordinates) {
		return (LocalCoordinates4) (Object) coordinates;
	}

	double ana();
	@ApiStatus.Internal
	void setAna(double ana);
}
