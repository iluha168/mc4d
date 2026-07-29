package com.iluha168.mc4d.mixin.net.minecraft.client.player;

import com.iluha168.mc4d.mixin.net.minecraft.world.entity.LivingEntityMixin;
import com.iluha168.mc4d.world.entity.LivingEntity4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RemotePlayer.class)
abstract class RemotePlayerMixin extends LivingEntityMixin {
	@Shadow
	private Vec3 lerpDeltaMovement;

	@Shadow
	private int lerpDeltaMovementSteps;

	@Redirect(method = "<init>", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	Vec3 init() {
		return Vec4.ZERO;
	}

	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/RemotePlayer;lerpHeadRotationStep(ID)V"
	))
	void aiStep_lerpHeadRotationStep(RemotePlayer instance, int steps, double targetYHeadRot) {
		((LivingEntity4) instance).lerpHeadRotationStep(steps, targetYHeadRot, this.lerpWHeadRot);
	}
	@Redirect(method = "aiStep", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 aiStep_addDeltaMovement(double x, double y, double z) {
		return new Vec4(x, y, z, (((Vec4) this.lerpDeltaMovement).w - ((Vec4) this.getDeltaMovement()).w) / this.lerpDeltaMovementSteps);
	}
}
