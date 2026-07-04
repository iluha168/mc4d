package com.iluha168.mc4d.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.block.dispatch.WRangeVariant;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;

/**
 * Implemented by {@link RenderSectionRegion}.
 */
public interface RenderSectionRegion4 {
	int minSectionW();
	void setMinSectionW(int w);

	/** The camera W the section is being compiled for, copied from {@link SectionCompiler4}. */
	double cameraW();
	void setCameraW(double cameraW);

	/** Records W values at which a {@link WRangeVariant} compiled from this region changes, for {@link CompiledSectionMesh4}. */
	void addWBoundaries(float[] wBoundaries);
	float[] collectedWBoundaries();

	static int index(int minSectionX, int minSectionY, int minSectionZ, int minSectionW, int sectionX, int sectionY, int sectionZ, int sectionW) {
		int sz = RenderSectionRegion.SIZE;
		return (sectionX - minSectionX) + (sectionY - minSectionY) * sz + (sectionZ - minSectionZ) * sz * sz + (sectionW - minSectionW) * sz * sz * sz;
	}
}
