package com.iluha168.mc4d.world.level;

import net.minecraft.world.level.BlockGetter;
import org.jspecify.annotations.Nullable;

// TODO: implement by everything that implements CollisionGetter
public interface CollisionGetter4 {
	@Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ, int chunkW);
}
