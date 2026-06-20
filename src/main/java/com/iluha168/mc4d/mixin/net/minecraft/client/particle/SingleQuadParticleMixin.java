package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.renderer.state.level.QuadParticleRenderState4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SingleQuadParticle.class)
abstract class SingleQuadParticleMixin extends ParticleMixin {
	@Shadow
	protected float rCol;

	@Shadow
	protected float gCol;

	@Shadow
	protected float bCol;

	@Shadow
	protected float alpha;

	@Shadow
	public abstract float getQuadSize(float a);

	@Shadow
	protected abstract float getU0();
	@Shadow
	protected abstract float getU1();
	@Shadow
	protected abstract float getV0();
	@Shadow
	protected abstract float getV1();

	@Shadow
	protected abstract SingleQuadParticle.Layer getLayer();

	@Redirect(method = "extractRotatedQuad(Lnet/minecraft/client/renderer/state/level/QuadParticleRenderState;Lnet/minecraft/client/Camera;Lorg/joml/Quaternionf;F)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/SingleQuadParticle;extractRotatedQuad(Lnet/minecraft/client/renderer/state/level/QuadParticleRenderState;Lorg/joml/Quaternionf;FFFF)V"
	))
	void extractRotatedQuad(SingleQuadParticle instance, QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float partialTickTime, @Local(name = "pos") Vec3 pos) {
		Particle4 particle4 = (Particle4) instance;
		float w = (float) (Mth.lerp(partialTickTime, particle4.wo(), particle4.w()) - ((Vec4) pos).w);
		this.extractRotatedQuad(particleTypeRenderState, rotation, x, y, z, w, partialTickTime);
	}

	@Overwrite
	@Deprecated
	protected void extractRotatedQuad(QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float partialTickTime) {
		throw Err4.arguments3(null);
	}
	@Unique
	protected void extractRotatedQuad(QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float w, float partialTickTime) {
		((QuadParticleRenderState4) particleTypeRenderState).add(
			this.getLayer(),
			x,
			y,
			z,
			w,
			rotation.x,
			rotation.y,
			rotation.z,
			rotation.w,
			this.getQuadSize(partialTickTime),
			this.getU0(),
			this.getU1(),
			this.getV0(),
			this.getV1(),
			ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol),
			this.getLightCoords(partialTickTime)
		);
	}

	@Override
	@Definition(id = "y", field = "Lnet/minecraft/client/particle/SingleQuadParticle;y:D")
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/SingleQuadParticle;z:D")
	@Expression("@(? + this.y + ? + this.z) + ?")
	@ModifyExpressionValue(method = "toString", at = @At("MIXINEXTRAS:EXPRESSION"))
	String toString(String original) {
		return original + "," + (this.initIncomplete != 0 ? "missingno" : this.w);
	}
}
