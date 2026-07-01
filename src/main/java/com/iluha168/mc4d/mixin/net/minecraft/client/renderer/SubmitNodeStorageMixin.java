package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.client.renderer.SubmitNodeStorage4;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SubmitNodeStorage.class)
class SubmitNodeStorageMixin implements SubmitNodeStorage4 {
	@Unique private int entityTintColor;

	@Override
	public int entityTintColor() {
		return this.entityTintColor;
	}
	@Override
	public void setEntityTintColor(int tintColor) {
		this.entityTintColor = tintColor;
	}

	@Mixin(SubmitNodeStorage.ItemSubmit.class)
	static class ItemSubmitMixin implements SubmitNodeStorage4.ItemSubmit {
		@Unique private int entityTintColor;

		@Override
		public int entityTintColor() {
			return this.entityTintColor;
		}
		@Override
		public void setEntityTintColor(int tintColor) {
			this.entityTintColor = tintColor;
		}
	}
}
