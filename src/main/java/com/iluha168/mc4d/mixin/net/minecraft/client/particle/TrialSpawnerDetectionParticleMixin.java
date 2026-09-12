package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TrialSpawnerDetectionParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TrialSpawnerDetectionParticle.class)
abstract class TrialSpawnerDetectionParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/TrialSpawnerDetectionParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(TrialSpawnerDetectionParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/TrialSpawnerDetectionParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(TrialSpawnerDetectionParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/TrialSpawnerDetectionParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(TrialSpawnerDetectionParticle instance, double value) {}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		this.xd *= 0.0;
		this.yd *= 0.9;
		this.zd *= 0.0;
		this.wd *= 0.0;
		this.xd += xa;
		this.yd += ya;
		this.zd += za;
		this.wd += wa;
	}

	@Mixin(TrialSpawnerDetectionParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprites;

		@Overwrite
		@Deprecated
		public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = new TrialSpawnerDetectionParticle(level, x, y, z, xAux, yAux, zAux, 1.5F, this.sprites);
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
