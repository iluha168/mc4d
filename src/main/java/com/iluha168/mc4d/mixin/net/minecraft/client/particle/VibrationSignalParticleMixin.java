package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.VibrationSignalParticle;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(VibrationSignalParticle.class)
abstract class VibrationSignalParticleMixin extends SingleQuadParticleMixin {
	@Shadow @Final private PositionSource target;
	@Shadow private float rot, rotO, pitch, pitchO;

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/gameevent/PositionSource;getPosition(Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
	))
	Optional<Vec3> init(PositionSource instance, Level level) {
		return Optional.empty(); // Skip double computation.
	}
	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		// I hate it, but we simply do not have w in the vanilla constructor, and everything depends on it.
		final Optional<Vec3> position = this.target.getPosition(this.level);
		if (position.isEmpty()) return;
		final Vec4 destination = (Vec4) position.get();
		final double dx = this.x - destination.x;
		final double dy = this.y - destination.y;
		final double dz = this.z - destination.z;
		final double dw = this.w - destination.w;
		this.rotO = this.rot = (float) Mth.atan2(dx, dz);
		this.pitchO = this.pitch = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz + dw * dw));
	}

	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/VibrationSignalParticle;xo:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/VibrationSignalParticle;setPos(DDD)V"
	))
	void tick_setPos(
		VibrationSignalParticle instance, double x, double y, double z,
		@Local(name = "alpha") double alpha,
		@Local(name = "destination") Vec3 destination
	) {
		((Particle4) instance).setPos(x, y, z, Mth.lerp(alpha, this.w, ((Vec4) destination).w));
	}
	@ModifyArg(method = "tick", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sqrt(D)D"
	))
	double tick_pitch(double horizontalSqr, @Local(name = "destination") Vec3 destination) {
		final double dw = this.w - ((Vec4) destination).w;
		return horizontalSqr + dw * dw;
	}

	@Mixin(VibrationSignalParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider4<VibrationParticleOption> {
		@Shadow
		public abstract Particle createParticle(VibrationParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(VibrationParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, 0.0, 0.0, 0.0, 0.0);
			return particle;
		}
	}
}
