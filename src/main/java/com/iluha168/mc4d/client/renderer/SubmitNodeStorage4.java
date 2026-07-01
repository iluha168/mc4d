package com.iluha168.mc4d.client.renderer;

import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.util.ARGB;

/**
 * Implemented by {@link SubmitNodeStorage}.
 */
public interface SubmitNodeStorage4 {
	int entityTintColor();
	void setEntityTintColor(int tintColor);

	default int tintByEntity(int color) {
		return ARGB.alphaBlend(color, this.entityTintColor());
	}

	/**
	 * Implemented by {@link SubmitNodeStorage.ItemSubmit}.
	 */
	interface ItemSubmit {
		static SubmitNodeStorage4.ItemSubmit as(SubmitNodeStorage.ItemSubmit submit) {
			return (SubmitNodeStorage4.ItemSubmit) (Object) submit;
		}

		int entityTintColor();
		void setEntityTintColor(int tintColor);

		default int tintByEntity(int color) {
			return ARGB.alphaBlend(color, this.entityTintColor());
		}
	}
}
