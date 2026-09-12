package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
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

@Mixin(CritParticle.class)
abstract class CritParticleMixin extends SingleQuadParticleMixin {
	@Shadow
	public abstract void tick();

	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/CritParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(CritParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/CritParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(CritParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/CritParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(CritParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/CritParticle;tick()V"))
	void init_postpone_tick(CritParticle instance) {}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		this.xd *= 0.1F;
		this.yd *= 0.1F;
		this.zd *= 0.1F;
		this.wd *= 0.1F;
		this.xd += xa * 0.4;
		this.yd += ya * 0.4;
		this.zd += za * 0.4;
		this.wd += wa * 0.4;
		this.tick();
	}

	@Mixin(CritParticle.DamageIndicatorProvider.class)
	static abstract class DamageIndicatorProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, xAux, yAux + 1.0, zAux, wAux);
			return particle;
		}
	}

	@Mixin(CritParticle.MagicProvider.class)
	static abstract class MagicProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}

	@Mixin(CritParticle.Provider.class)
	static abstract class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprite;

		@Overwrite
		@Deprecated
		public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = new CritParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
