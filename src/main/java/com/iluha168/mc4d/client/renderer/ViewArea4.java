package com.iluha168.mc4d.client.renderer;

/**
 * Implemented by {@link net.minecraft.client.renderer.ViewArea}.
 */
public interface ViewArea4 {
	void setDirty(int sectionX, int sectionY, int sectionZ, int sectionW, boolean playerChanged);

	/**
	 * This is not actually a vanilla method - we use it to refresh every single mesh when switching slices.
	 */
	void setAllDirty(boolean playerChanged);
}
