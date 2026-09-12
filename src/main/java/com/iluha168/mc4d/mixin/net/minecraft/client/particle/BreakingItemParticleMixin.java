package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BreakingItemParticle.class)
abstract class BreakingItemParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BreakingItemParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(BreakingItemParticle instance, double value) {}
	@Redirect(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BreakingItemParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(BreakingItemParticle instance, double value) {}
	@Redirect(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BreakingItemParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(BreakingItemParticle instance, double value) {}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		this.xd *= 0.1F;
		this.yd *= 0.1F;
		this.zd *= 0.1F;
		this.wd *= 0.1F;
		this.xd += xa;
		this.yd += ya;
		this.zd += za;
		this.wd += wa;
	}

	@Mixin({
		BreakingItemParticle.CobwebProvider.class,
		BreakingItemParticle.SlimeProvider.class,
		BreakingItemParticle.SnowballProvider.class,
	})
	static abstract class ProviderZeroInitialVelocityMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, 0.0, 0.0, 0.0, 0.0);
			return particle;
		}
	}

	@Mixin(BreakingItemParticle.Provider.class)
	static abstract class ProviderMixin implements ParticleProvider<ItemParticleOption>, ParticleProvider4<ItemParticleOption> {
		@Override
		public @Nullable Particle createParticle(ItemParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
