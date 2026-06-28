package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.state.level;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.renderer.state.level.QuadParticleRenderState4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.ARGB;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(QuadParticleRenderState.class)
abstract class QuadParticleRenderStateMixin implements QuadParticleRenderState4 {
	@Shadow
	@Final
	private Map<SingleQuadParticle.Layer, QuadParticleRenderState.Storage> particles;

	@Shadow
	private int particleCount;

	@Shadow
	protected abstract void renderVertex(VertexConsumer builder, Quaternionf rotation, float x, float y, float z, float nx, float ny, float scale, float u, float v, int color, int lightCoords);

	@Unique private boolean enableNeighbouringSliceRenderer;

	@Overwrite
	@Deprecated
	public void add(SingleQuadParticle.Layer layer, float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
		throw Err4.arguments3("QuadParticleRenderState4#add");
	}
	@Override
	public void add(SingleQuadParticle.Layer layer, float x, float y, float z, float w, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
		((QuadParticleRenderState4.Storage) this.particles.computeIfAbsent(layer, ignored -> new QuadParticleRenderState.Storage()))
			.add(x, y, z, w, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords);
		this.particleCount++;
	}

	@Redirect(method = "prepare", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/state/level/QuadParticleRenderState$Storage;forEachParticle(Lnet/minecraft/client/renderer/state/level/QuadParticleRenderState$ParticleConsumer;)V"
	))
	void prepare(QuadParticleRenderState.Storage storage, QuadParticleRenderState.ParticleConsumer consumer, @Local(name = "bufferBuilder") BufferBuilder bufferBuilder) {
		this.enableNeighbouringSliceRenderer = Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NEIGHBOURING_SLICE_PARTICLE_RENDERER);
		((QuadParticleRenderState4.Storage) storage).forEachParticle(
			(x, y, z, w, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords) -> this.renderRotatedQuad(
				bufferBuilder, x, y, z, w, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords
			)
		);
	}

	@Overwrite
	@Deprecated
	protected void renderRotatedQuad(VertexConsumer builder, float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
		throw Err4.arguments3(null);
	}
	@Unique
	protected void renderRotatedQuad(VertexConsumer builder, float x, float y, float z, float w, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
		// In this method x, y, z, w are relative to the camera (it is at w=0).
		if (this.enableNeighbouringSliceRenderer) {
			if (Math.abs(w) > scale + 1) {
				// Do not render the particle when it is outside camera's 3D slice.
				scale = 0.0F;
			} else {
				color = ARGB.alphaBlend(color, ARGB.color(
					2 / (1 + (float) Math.exp(-Math.abs(0.2 * w / scale))) - 1, // Makes particles that are closer less tinted using a sigmoid
					ARGB.transparent(w > 0 ? MC4DClient.COLOR_ANA : MC4DClient.COLOR_KATA)
				));
			}
		} else if (Math.abs(w) > scale) {
			// Do not render the particle when it is outside camera's 3D slice.
			scale = 0.0F;
		}
		Quaternionf rotation = new Quaternionf(xRot, yRot, zRot, wRot);
		this.renderVertex(builder, rotation, x, y, z, 1.0F, -1.0F, scale, u1, v1, color, lightCoords);
		this.renderVertex(builder, rotation, x, y, z, 1.0F, 1.0F, scale, u1, v0, color, lightCoords);
		this.renderVertex(builder, rotation, x, y, z, -1.0F, 1.0F, scale, u0, v0, color, lightCoords);
		this.renderVertex(builder, rotation, x, y, z, -1.0F, -1.0F, scale, u0, v1, color, lightCoords);
	}

	@Mixin(QuadParticleRenderState.Storage.class)
	private static class StorageMixin implements QuadParticleRenderState4.Storage {
		@Shadow
		private int capacity;

		@Shadow
		private float[] floatValues;

		@Shadow
		private int[] intValues;

		@Shadow
		private int currentParticleIndex;

		@Shadow
		private void grow() {
			throw new UnsupportedOperationException("Implemented via mixin");
		}

		@ModifyConstant(method = "<init>", constant = @Constant(intValue = 1024*12))
		private int init(int original) {
			return 1024*13; // One more float for W
		}

		@Overwrite
		@Deprecated
		public void add(float x, float y, float z, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
			throw Err4.arguments3("QuadParticleRenderState4.Storage#add");
		}
		@Override
		public void add(float x, float y, float z, float w, float xRot, float yRot, float zRot, float wRot, float scale, float u0, float u1, float v0, float v1, int color, int lightCoords) {
			if (this.currentParticleIndex >= this.capacity) {
				this.grow();
			}

			int index = this.currentParticleIndex * 13;
			this.floatValues[index++] = x;
			this.floatValues[index++] = y;
			this.floatValues[index++] = z;
			this.floatValues[index++] = w;
			this.floatValues[index++] = xRot;
			this.floatValues[index++] = yRot;
			this.floatValues[index++] = zRot;
			this.floatValues[index++] = wRot;
			this.floatValues[index++] = scale;
			this.floatValues[index++] = u0;
			this.floatValues[index++] = u1;
			this.floatValues[index++] = v0;
			this.floatValues[index] = v1;
			index = this.currentParticleIndex * 2;
			this.intValues[index++] = color;
			this.intValues[index] = lightCoords;
			this.currentParticleIndex++;
		}

		@Overwrite
		@Deprecated
		public void forEachParticle(QuadParticleRenderState.ParticleConsumer consumer) {
			throw Err4.arguments3("QuadParticleRenderState4.Storage#forEachParticle");
		}
		@Override
		public void forEachParticle(QuadParticleRenderState4.ParticleConsumer consumer) {
			for (int particleIndex = 0; particleIndex < this.currentParticleIndex; particleIndex++) {
				int floatIndex = particleIndex * 13;
				int intIndex = particleIndex * 2;
				consumer.consume(
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex++],
					this.floatValues[floatIndex],
					this.intValues[intIndex++],
					this.intValues[intIndex]
				);
			}
		}

		@ModifyConstant(method = "grow", constant = @Constant(intValue = 12))
		private int grow(int original) {
			return 13;
		}
	}
}
