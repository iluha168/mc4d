package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.PalettedContainer4;
import com.iluha168.mc4d.world.level.chunk.Strategy4;
import net.minecraft.world.level.chunk.Strategy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.world.level.chunk.PalettedContainer.class)
abstract
class PalettedContainerMixin<T> implements PalettedContainer4<T> {
	@Shadow
	public abstract void acquire();

	@Shadow
	protected abstract T getAndSet(int index, T value);

	@Shadow
	@Final
	private Strategy<T> strategy;

	@Shadow
	public abstract void release();

	@Shadow
	protected abstract void set(int index, T value);

	@Shadow
	protected abstract T get(int index);

	@Overwrite
	public T getAndSet(int x, int y, int z, T value) {
		throw Err4.arguments3("PalettedContainer4#getAndSet");
	}
	@Override
	public T getAndSet(int x, int y, int z, int w, T value) {
		this.acquire();
		try {
			return this.getAndSet(((Strategy4<?>) this.strategy).getIndex(x, y, z, w), value);
		} finally {
			this.release();
		}
	}

	@Overwrite
	public T getAndSetUnchecked(int x, int y, int z, T value) {
		throw Err4.arguments3("PalettedContainer4#getAndSetUnchecked");
	}
	@Override
	public T getAndSetUnchecked(int x, int y, int z, int w, T value) {
		return this.getAndSet(((Strategy4<?>) this.strategy).getIndex(x, y, z, w), value);
	}

	@Overwrite
	public void set(int x, int y, int z, T value) {
		throw Err4.arguments3("PalettedContainer4#set");
	}
	@Override
	public void set(int x, int y, int z, int w, T value) {
		this.acquire();
		try {
			this.set(((Strategy4<?>) this.strategy).getIndex(x, y, z, w), value);
		} finally {
			this.release();
		}
	}

	@Overwrite
	public T get(int x, int y, int z) {
		throw Err4.arguments3("PalettedContainer4#get");
	}
	@Override
	public T get(int x, int y, int z, int w) {
		return this.get(((Strategy4<?>) this.strategy).getIndex(x, y, z, w));
	}
}
