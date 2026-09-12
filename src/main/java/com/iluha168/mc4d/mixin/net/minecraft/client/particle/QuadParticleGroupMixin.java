package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.renderer.culling.Frustum4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.QuadParticleGroup;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QuadParticleGroup.class)
class QuadParticleGroupMixin {
	@Redirect(method = "extractRenderState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/culling/Frustum;pointInFrustum(DDD)Z"
	))
	boolean extractRenderState(Frustum frustum, double x, double y, double z, @Local(name = "particle") SingleQuadParticle particle, @Local(name = "partialTickTime", argsOnly = true) float partialTickTime) {
		final Particle4 particle4 = (Particle4) particle;
		// Wtf is vanilla doing there?
		// Rendered position (SingleQuadParticle#extractRotatedQuad) does lerp, but the frustum checks the latest position?
		// This actually matters a lot more for movement along the axis perpendicular to the visible world.
		// We are fixing vanilla, I guess.
		final double w = Mth.lerp(partialTickTime, particle4.wo(), particle4.w());
		final float dw = particle.getQuadSize(partialTickTime);
		return ((Frustum4) frustum).pointInFrustum(
			Mth.lerp(partialTickTime, particle.xo, x),
			Mth.lerp(partialTickTime, particle.yo, y),
			Mth.lerp(partialTickTime, particle.zo, z),
			w - dw,
			w + dw
		);
	}
}
