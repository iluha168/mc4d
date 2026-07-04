package com.iluha168.mc4d.client.renderer.chunk;

import net.minecraft.client.renderer.chunk.SectionCompiler;

/**
 * Implemented by {@link SectionCompiler}.
 */
public interface SectionCompiler4 {
	void setCameraW(double cameraW);

	/**
	 * Implemented by {@link SectionCompiler.Results}.
	 */
	interface Results {
		float[] wBoundaries();
		void setWBoundaries(float[] wBoundaries);

		static Results as(SectionCompiler.Results results) {
			return (Results) (Object) results;
		}
	}
}
