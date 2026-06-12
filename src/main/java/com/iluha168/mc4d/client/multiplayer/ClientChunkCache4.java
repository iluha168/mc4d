package com.iluha168.mc4d.client.multiplayer;

import net.minecraft.client.multiplayer.ClientChunkCache;

public interface ClientChunkCache4 {
	interface Storage {
		static ClientChunkCache4.Storage as(ClientChunkCache.Storage storage) {
			return (ClientChunkCache4.Storage) (Object) storage;
		}

		int viewCenterW();
		void setViewCenterW(int viewCenterW);

		int getIndex(int chunkX, int chunkZ, int chunkW);
		boolean inRange(int chunkX, int chunkZ, int chunkW);
	}
}
