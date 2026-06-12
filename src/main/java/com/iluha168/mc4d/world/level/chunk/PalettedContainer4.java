package com.iluha168.mc4d.world.level.chunk;

public interface PalettedContainer4<T> {
	T getAndSet(int x, int y, int z, int w, T value);
	T getAndSetUnchecked(int x, int y, int z, int w, T value);
	void set(int x, int y, int z, int w, T value);
	T get(int x, int y, int z, int w);
}
