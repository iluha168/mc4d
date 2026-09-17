package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.api.net.minecraft.world.level.EntityGetter4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerCloudParticle.class)
abstract class PlayerCloudParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/PlayerCloudParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(PlayerCloudParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/PlayerCloudParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(PlayerCloudParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/PlayerCloudParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(PlayerCloudParticle instance, double value) {}

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

	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;getNearestPlayer(DDDDZ)Lnet/minecraft/world/entity/player/Player;"
	))
	@Nullable Player tick_getNearestPlayer(ClientLevel instance, double x, double y, double z, double maxDist, boolean filterOutCreative) {
		return ((EntityGetter4) instance).getNearestPlayer(x, y, z, this.w(), maxDist, filterOutCreative);
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/PlayerCloudParticle;setPos(DDD)V"
	))
	void tick_setPos(PlayerCloudParticle instance, double x, double y, double z) {
		((Particle4) instance).setPos(x, y, z, this.w);
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.PlayerCloudParticle$Provider",
		"net.minecraft.client.particle.PlayerCloudParticle$SneezeProvider",
	})
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
