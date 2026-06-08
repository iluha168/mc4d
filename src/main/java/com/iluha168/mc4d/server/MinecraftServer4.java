package com.iluha168.mc4d.server;

import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

public interface MinecraftServer4 {
	interface ChunkLoadStatusView {
		@Nullable ChunkStatus get(int x, int z, int w);
	}
}
