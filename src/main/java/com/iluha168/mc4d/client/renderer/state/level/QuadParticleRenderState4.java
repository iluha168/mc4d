package com.iluha168.mc4d.client.renderer.state.level;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;

/**
 * Implemented by {@link QuadParticleRenderState}.
 */
public interface QuadParticleRenderState4 {
	void add(
		SingleQuadParticle.Layer layer,
		float x, float y, float z, float w,
		float xRot, float yRot, float zRot, float wRot,
		float scale,
		float u0, float u1, float v0, float v1,
		int color, int lightCoords
	);

	/**
	 * Mirrors {@link QuadParticleRenderState.ParticleConsumer}.
	 */
	@FunctionalInterface
	interface ParticleConsumer {
		void consume(
			float x, float y, float z, float w,
			float xRot, float yRot, float zRot, float wRot,
			float scale,
			float u0, float u1, float v0, float v1,
			int color, int lightCoords
		);
	}

	/**
	 * Implemented by {@link QuadParticleRenderState.Storage}.
	 */
	interface Storage {
		void add(
			float x, float y, float z, float w,
			float xRot, float yRot, float zRot, float wRot,
			float scale,
			float u0, float u1, float v0, float v1,
			int color, int lightCoords
		);

		void forEachParticle(QuadParticleRenderState4.ParticleConsumer consumer);
	}
}
