package com.iluha168.mc4d.world.level.lighting;

import com.iluha168.mc4d.core.Direction4;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.LightEngine;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link net.minecraft.world.level.lighting.LightEngine}.
 */
public interface LightEngine4 {
	@Nullable LightChunk getChunk(int chunkX, int chunkZ, int chunkW);

	/**
	 * Implemented by {@link net.minecraft.world.level.lighting.LightEngine.QueueEntry}.
	 */
	interface QueueEntry {
		static long increaseSkySourceInDirections(boolean down, boolean north, boolean south, boolean west, boolean east, boolean kata, boolean ana) {
			long increaseData = LightEngine.QueueEntry.withLevel(0L, 15);
			if (down) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction.DOWN);
			if (north) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction.NORTH);
			if (south) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction.SOUTH);
			if (west) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction.WEST);
			if (east) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction.EAST);
			if (kata) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction4.KATA);
			if (ana) increaseData = LightEngine.QueueEntry.withDirection(increaseData, Direction4.ANA);
			return increaseData;
		}
	}
}
