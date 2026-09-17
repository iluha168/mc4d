package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubblePopParticle;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BubblePopParticle.class)
abstract class BubblePopParticleMixin extends SingleQuadParticleMixin {
	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w);
		this.wd = wa;
	}

	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/BubblePopParticle;xo:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/BubblePopParticle;move(DDD)V"
	))
	void tick_move(BubblePopParticle instance, double xa, double ya, double za) {
		((Particle4) instance).move(xa, ya, za, this.wd);
	}

	@Mixin(BubblePopParticle.Provider.class)
	static abstract class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
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
			final Particle particle = new BubblePopParticle(level, x, y, z, xAux, yAux, zAux, this.sprites);
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
