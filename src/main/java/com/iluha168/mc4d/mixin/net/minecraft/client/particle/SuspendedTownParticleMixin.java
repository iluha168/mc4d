package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SuspendedTownParticle.class)
abstract class SuspendedTownParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SuspendedTownParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(SuspendedTownParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SuspendedTownParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(SuspendedTownParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SuspendedTownParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(SuspendedTownParticle instance, double value) {}

	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w, wa);
		this.xd *= 0.02F;
		this.yd *= 0.02F;
		this.zd *= 0.02F;
		this.wd *= 0.02F;
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

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/SuspendedTownParticle;zo:D")
	@Expression("this.zo = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/SuspendedTownParticle;move(DDD)V"
	))
	void tick_move(SuspendedTownParticle instance, double xa, double ya, double za) {
		((Particle4) instance).move(xa, ya, za, this.wd);
		this.wd *= 0.99;
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.SuspendedTownParticle$ComposterFillProvider",
		"net.minecraft.client.particle.SuspendedTownParticle$DolphinSpeedProvider",
		"net.minecraft.client.particle.SuspendedTownParticle$EggCrackProvider",
		"net.minecraft.client.particle.SuspendedTownParticle$HappyVillagerProvider",
		"net.minecraft.client.particle.SuspendedTownParticle$Provider",
	})
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, wAux);
			return particle;
		}
	}
}
