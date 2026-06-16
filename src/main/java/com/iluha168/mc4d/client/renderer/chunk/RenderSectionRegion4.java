package com.iluha168.mc4d.client.renderer.chunk;

import net.minecraft.client.renderer.chunk.RenderSectionRegion;

/**
 * Implemented by {@link RenderSectionRegion}.
 */
public interface RenderSectionRegion4 {
	int minSectionW();
	void setMinSectionW(int w);

	static int index(int minSectionX, int minSectionY, int minSectionZ, int minSectionW, int sectionX, int sectionY, int sectionZ, int sectionW) {
		int sz = RenderSectionRegion.SIZE;
		return (sectionX - minSectionX) + (sectionY - minSectionY) * sz + (sectionZ - minSectionZ) * sz * sz + (sectionW - minSectionW) * sz * sz * sz;
	}
}