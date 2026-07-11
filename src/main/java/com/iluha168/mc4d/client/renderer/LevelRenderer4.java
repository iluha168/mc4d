package com.iluha168.mc4d.client.renderer;

/**
 * Implemented by {@link net.minecraft.client.renderer.LevelRenderer}.
 */
public interface LevelRenderer4 {
	// The minimum time required to pass since the last movement-along-W-triggered section refresh.
	byte REFRESH_CAMERA_SECTION_EVERY_TICKS = 2;
	byte REFRESH_ALL_SECTIONS_EVERY_TICKS = 15;

	void setBlocksDirty(int x0, int y0, int z0, int w0, int x1, int y1, int z1, int w1);

	void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ, int sectionW);
	void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int minSectionW, int maxSectionX, int maxSectionY, int maxSectionZ, int maxSectionW);
	void setSectionDirty(int sectionX, int sectionY, int sectionZ, int sectionW);
}
