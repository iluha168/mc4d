package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.world.entity.Entity4;
import com.iluha168.mc4d.api.net.minecraft.world.level.LevelAccessor4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrackingEmitter.class)
abstract class TrackingEmitterMixin extends ParticleMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;ILnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/NoRenderParticle;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V",
		shift = At.Shift.AFTER
	))
	void init(ClientLevel level, Entity entity, ParticleOptions particleType, int lifeTime, Vec3 movement, CallbackInfo ci) {
		this.init_finish(((Entity4) entity).getW(), movement.x, movement.y, movement.z, ((Vec4) movement).w);
	}

	@Definition(id = "xa", local = @Local(name = "xa", type = double.class))
	@Definition(id = "ya", local = @Local(name = "ya", type = double.class))
	@Definition(id = "za", local = @Local(name = "za", type = double.class))
	@Expression("xa * xa + ya * ya + za * za")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	double tick_distSqr(double distSqr3D, @Share("wa") LocalDoubleRef waRef) {
		final double wa = this.random.nextFloat() * 2.0F - 1.0F;
		waRef.set(wa);
		return distSqr3D + wa * wa;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void tick_addParticle(
		ClientLevel instance, ParticleOptions particle, double x, double y, double z,
		double xd, double yd, double zd, @Share("wa") LocalDoubleRef wd
	) {
		final double w = ((Entity4) this.entity).getW(wd.get() / 4.0);
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w, xd, yd, zd, wd.get());
	}
}
