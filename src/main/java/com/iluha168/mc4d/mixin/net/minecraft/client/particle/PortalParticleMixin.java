package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.AABB4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PortalParticle.class)
abstract class PortalParticleMixin extends SingleQuadParticleMixin {
	@Unique private double wStart;

	@Override
	public void init_finish(double w) {
		throw new IllegalStateException("Programmer error: use Particle4#init_finish.");
	}
	@Override
	public void init_finish(double w, double xd, double yd, double zd, double wd) {
		super.init_finish(w);
		this.wd = wd;
		this.wStart = w;
	}

	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	public void move(double xa, double ya, double za) {
		throw Err4.arguments3("Particle4#move");
	}
	@Override
	public void move(double xa, double ya, double za, double wa) {
		this.setBoundingBox(((AABB4) this.getBoundingBox()).move(xa, ya, za, wa));
		this.setLocationFromBoundingbox();
	}

	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/PortalParticle;zo:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/PortalParticle;setPos(DDD)V"
	))
	void tick_setPos(PortalParticle instance, double x, double y, double z, @Local(name = "var4") float var4) {
		this.w = this.wStart + this.wd * var4;
		((Particle4) instance).setPos(x, y, z, this.w);
	}

	@Mixin(PortalParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
