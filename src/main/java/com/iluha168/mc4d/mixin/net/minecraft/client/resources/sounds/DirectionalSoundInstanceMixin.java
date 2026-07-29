package com.iluha168.mc4d.mixin.net.minecraft.client.resources.sounds;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.resources.sounds.DirectionalSoundInstance;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectionalSoundInstance.class)
abstract class DirectionalSoundInstanceMixin extends AbstractSoundInstanceMixin {
	@Shadow
	@Final
	private Camera camera;

	@Redirect(method = "setPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;directionFromRotation(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 setPosition_direction(float rotX, float rotY) {
		// TODO rotW when 4D renderer
		return Vec4.directionFromRotation(rotX, rotY, 0.0F);
	}
	@Inject(method = "setPosition", at = @At("TAIL"))
	void setPosition_w(CallbackInfo ci, @Local(name = "direction") Vec3 direction) {
		this.setW(((Vec4) this.camera.position()).w + ((Vec4) direction).w);
	}
}
