package com.iluha168.mc4d.mixin.net.minecraft.world.level.portal;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.portal.TeleportTransition4;
import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(TeleportTransition.class)
public abstract class TeleportTransitionMixin implements TeleportTransition4 {
	@Shadow
	public abstract ServerLevel newLevel();

	@Shadow
	public abstract Vec3 position();

	@Shadow
	public abstract Vec3 deltaMovement();

	@Shadow
	public abstract float yRot();

	@Shadow
	public abstract float xRot();

	@Shadow
	public abstract boolean missingRespawnBlock();

	@Shadow
	public abstract boolean asPassenger();

	@Shadow
	public abstract Set<Relative> relatives();

	@Shadow
	public abstract TeleportTransition.PostTeleportTransition postTeleportTransition();

	@Unique	private float wRot = Float.NaN;
	@Unique private float vRot = Float.NaN;

	@Override
	public float wRot() {
		if (Float.isNaN(this.wRot)) throw Err4.field4missing("wRot");
		return this.wRot;
	}
	@Override
	public void setWRot(float wRot) {
		this.wRot = wRot;
	}
	@Override
	public float vRot() {
		if (Float.isNaN(this.vRot)) throw Err4.field4missing("vRot");
		return this.vRot;
	}
	@Override
	public void setVRot(float vRot) {
		this.vRot = vRot;
	}

	@Redirect(method = {"createDefault", "missingRespawnBlock(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"}, at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec3 createDefault_missingRespawnBlock_ZERO() {
		return Vec4.ZERO;
	}
	@ModifyExpressionValue(method = {"createDefault", "missingRespawnBlock(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"}, at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFZZLjava/util/Set;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"
	))
	private static TeleportTransition createDefault_missingRespawnBlock_new(TeleportTransition transition, @Local(name = "respawnData") LevelData.RespawnData respawnData) {
		final TeleportTransition4 transition4 = TeleportTransition4.as(transition);
		final LevelData4.RespawnData respawnData4 = LevelData4.RespawnData.as(respawnData);
		transition4.setWRot(respawnData4.wRot());
		transition4.setVRot(respawnData4.vRot());
		return transition;
	}

	@Overwrite
	@Deprecated
	public TeleportTransition withRotation(float yRot, float xRot) {
		throw Err4.rotation("TeleportTransition4#withRotation");
	}
	@Override
	public TeleportTransition withRotation(float yRot, float xRot, float wRot, float vRot) {
		return TeleportTransition4.from(
			this.newLevel(), this.position(), this.deltaMovement(), yRot, xRot, wRot, vRot,
			this.missingRespawnBlock(), this.asPassenger(), this.relatives(), this.postTeleportTransition()
		);
	}

	@ModifyReturnValue(method = {"withPosition", "transitionAsPassenger"}, at = @At("RETURN"))
	private TeleportTransition withPosition_transitionAsPassenger(TeleportTransition transition) {
		final TeleportTransition4 transition4 = TeleportTransition4.as(transition);
		transition4.setWRot(this.wRot());
		transition4.setVRot(this.vRot());
		return transition;
	}

	@WrapMethod(method = "equals")
	public boolean equals(Object o, Operation<Boolean> original) {
		if (!original.call(o)) {
			return false;
		}
		final TeleportTransition4 o4 = (TeleportTransition4) o;
		return this.wRot() == o4.wRot() && this.vRot() == o4.vRot();
	}

	@WrapMethod(method = "hashCode")
	public int hashCode(Operation<Integer> original) {
		return (original.call() * 31 + Float.hashCode(this.wRot())) * 31 + Float.hashCode(this.vRot());
	}
}
