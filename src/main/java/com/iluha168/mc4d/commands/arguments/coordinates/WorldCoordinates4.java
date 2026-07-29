package com.iluha168.mc4d.commands.arguments.coordinates;

import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * Implemented by {@link net.minecraft.commands.arguments.coordinates.WorldCoordinates}.
 * <p>
 * For world coordinates, the layout is x:x, y:y, z:z, w:w.
 * For rotation coordinates, the layout is x:xRot, y:yRot, z:wRot, w:vRot.
 */
public interface WorldCoordinates4 {
	static WorldCoordinates4 as(WorldCoordinates coordinates) {
		return (WorldCoordinates4) (Object) coordinates;
	}

	@NonNull WorldCoordinate w();
	@ApiStatus.Internal
	void setW(WorldCoordinate w);
}
