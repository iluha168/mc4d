package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.PowerParticleOption;
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

@Mixin(DragonBreathParticle.class)
abstract class DragonBreathParticleMixin extends SingleQuadParticleMixin {
	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w);
		this.wd = wa;
	}

	@Definition(id = "xo", field = "Lnet/minecraft/client/particle/DragonBreathParticle;xo:D")
	@Definition(id = "x", field = "Lnet/minecraft/client/particle/DragonBreathParticle;x:D")
	@Expression("this.xo = this.x")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/DragonBreathParticle;move(DDD)V"
	))
	void tick_move(DragonBreathParticle instance, double xa, double ya, double za) {
		((Particle4) instance).move(xa, ya, za, this.wd);
	}
	@Inject(method = "tick", at = @At(value = "CONSTANT", ordinal = 0, args = "doubleValue=1.1"))
	void tick_wd(CallbackInfo ci) {
		this.wd *= 1.1;
	}
	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/DragonBreathParticle;friction:F",
		ordinal = 0,
		opcode = Opcodes.GETFIELD
	))
	void tick_friction(CallbackInfo ci) {
		this.wd *= this.friction;
	}

	@Mixin(DragonBreathParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<PowerParticleOption> {
		@Shadow
		@Final
		private SpriteSet sprites;

		@Overwrite
		@Deprecated
		public Particle createParticle(
			PowerParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(PowerParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final DragonBreathParticle particle = new DragonBreathParticle(level, x, y, z, xAux, yAux, zAux, this.sprites);
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			particle.setPower(options.getPower());
			return particle;
		}
	}
}
