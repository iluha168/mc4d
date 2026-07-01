package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.ItemPickupParticle4;
import com.iluha168.mc4d.client.renderer.entity.state.EntityRenderState4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemPickupParticle.class)
abstract class ItemPickupParticleMixin extends ParticleMixin implements ItemPickupParticle4 {
	@Shadow
	@Final
	private Entity target;

	@Unique protected double targetW;
	@Unique protected double targetWOld;

	@Override
	public double targetW() {
		return this.targetW;
	}
	@Override
	public double targetWOld() {
		return this.targetWOld;
	}

	@Inject(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/Particle;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V",
		shift = At.Shift.AFTER
	))
	private void init(ClientLevel level, EntityRenderState itemEntity, Entity target, Vec3 movement, CallbackInfo ci) {
		init_finish(((EntityRenderState4) itemEntity).w(), ((Vec4) movement).w);
	}

	@Inject(method = "updatePosition", at = @At("TAIL"))
	void updatePosition(CallbackInfo ci) {
		this.targetW = ((Entity4) this.target).getW();
	}

	@Inject(method = "saveOldPosition", at = @At("TAIL"))
	void saveOldPosition(CallbackInfo ci) {
		this.targetWOld = this.targetW;
	}
}
