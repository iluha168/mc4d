package com.iluha168.mc4d.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.block.dispatch.WRangeVariant;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;

/**
 * Implemented by {@link CompiledSectionMesh}.
 * <p>
 * Carries the W boundaries of every {@link WRangeVariant} compiled into the mesh, so the
 * renderer knows to re-mesh the section exactly when the camera's block-local W crosses one of
 * them.
 */
public interface CompiledSectionMesh4 {
	float[] NO_W_BOUNDARIES = new float[0];

	/**
	 * Sorted distinct block-local camera W values, scaled to (0;{@value com.iluha168.mc4d.client.renderer.block.dispatch.Variant4#W_SCALE}),
	 * at which some model in this mesh changes.
	 */
	float[] wBoundaries();

	/** Whether any of the sorted {@code wBoundaries} lies within [{@code minLocalW};{@code maxLocalW}]. */
	static boolean anyWBoundaryIn(float[] wBoundaries, float minLocalW, float maxLocalW) {
		for (final float boundary : wBoundaries) {
			if (boundary > maxLocalW) return false;
			if (boundary >= minLocalW) return true;
		}
		return false;
	}
}
