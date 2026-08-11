package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.network.protocol.game.ClientboundMoveEntityPacket4;
import com.iluha168.mc4d.network.protocol.game.ClientboundRotateHeadPacket4;
import com.iluha168.mc4d.network.protocol.game.VecDeltaCodec4;
import com.iluha168.mc4d.server.level.ServerEntity4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.vehicle.minecart.NewMinecartBehavior4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalByteRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerEntity.class)
abstract class ServerEntityMixin implements ServerEntity4 {
	@Shadow
	@Final
	private VecDeltaCodec positionCodec;
	@Shadow
	@Final
	private Entity entity;
	@Shadow
	@Final
	private ServerEntity.Synchronizer synchronizer;

	@Shadow
	protected abstract void sendDirtyEntityData();

	@Shadow
	private Vec3 lastSentMovement;
	@Shadow
	@Final
	private static double TOLERANCE_LEVEL_POSITION;
	@Shadow
	private int tickCount;
	@Shadow
	@Final
	public static int FORCED_POS_UPDATE_PERIOD;
	@Shadow
	private byte lastSentYRot;
	@Shadow
	private byte lastSentXRot;
	@Unique
	private byte lastSentWRot;
	@Unique
	private byte lastSentVRot;
	@Unique
	private byte lastSentWHeadRot;

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(CallbackInfo ci) {
		final Entity4 entity4 = (Entity4) this.entity;
		this.lastSentWRot = Mth.packDegrees(entity4.getWRot());
		this.lastSentVRot = Mth.packDegrees(entity4.getVRot());
		this.lastSentWHeadRot = Mth.packDegrees(entity4.getWHeadRot());
	}

	@Definition(id = "packDegrees", method = "Lnet/minecraft/util/Mth;packDegrees(F)B")
	@Definition(id = "getXRot", method = "Lnet/minecraft/world/entity/Entity;getXRot()F")
	@Expression("packDegrees(?.getXRot())")
	@ModifyExpressionValue(method = "sendChanges", at = @At("MIXINEXTRAS:EXPRESSION"))
	byte sendChanges_wRotn_vRotn(
		byte xRotn,
		@Share("wRotn") LocalByteRef wRotn,
		@Share("vRotn") LocalByteRef vRotn
	) {
		final Entity4 entity4 = (Entity4) this.entity;
		wRotn.set(Mth.packDegrees(entity4.getWRot()));
		vRotn.set(Mth.packDegrees(entity4.getVRot()));
		return xRotn;
	}
	@Definition(id = "abs", method = "Ljava/lang/Math;abs(I)I")
	@Definition(id = "lastSentXRot", field = "Lnet/minecraft/server/level/ServerEntity;lastSentXRot:B")
	@Expression("abs(? - this.lastSentXRot) >= 1")
	@ModifyExpressionValue(method = "sendChanges", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean sendChanges_shouldSendRotation(
		boolean original,
		@Share("wRotn") LocalByteRef wRotn,
		@Share("vRotn") LocalByteRef vRotn
	) {
		return original || Math.abs(wRotn.get() - this.lastSentWRot) >= 1 || Math.abs(vRotn.get() - this.lastSentVRot) >= 1;
	}
	// "new ClientboundMoveEntityPacket.Rot" is covered by sendChanges_rot below
	// "this.lastSentXRot =" is covered by sendChanges_lastSentWRot
	@Definition(id = "handleMinecartPosRot", method = "Lnet/minecraft/server/level/ServerEntity;handleMinecartPosRot(Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior;BBZ)V")
	@Expression("this.handleMinecartPosRot(?, ?, ?, ?)")
	@Redirect(method = "sendChanges", at = @At("MIXINEXTRAS:EXPRESSION"))
	void sendChanges_handleMinecartPosRot(
		ServerEntity instance, NewMinecartBehavior newMinecartBehavior, byte yRotn, byte xRotn, boolean shouldSendRotation,
		@Share("wRotn") LocalByteRef wRotn,
		@Share("vRotn") LocalByteRef vRotn
	) {
		this.handleMinecartPosRot(newMinecartBehavior, yRotn, xRotn, wRotn.get(), vRotn.get(), shouldSendRotation);
	}
	@Inject(method = "sendChanges", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/protocol/game/VecDeltaCodec;encodeZ(Lnet/minecraft/world/phys/Vec3;)J"
	))
	void sendChanges_wa(CallbackInfo ci, @Share("wa") LocalLongRef wa, @Local(name = "currentPosition") Vec3 currentPosition) {
		wa.set(((VecDeltaCodec4) this.positionCodec).encodeW((Vec4) currentPosition));
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;getRequiresPrecisePosition()Z"))
	boolean sendChanges_deltaTooBig(boolean deltaTooBig, @Share("wa") LocalLongRef wa) {
		return deltaTooBig || wa.get() < -32768L || wa.get() > 32767L;
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "NEW",
		target = "(ISSSZ)Lnet/minecraft/network/protocol/game/ClientboundMoveEntityPacket$Pos;"
	))
	ClientboundMoveEntityPacket.Pos sendChanges_pos(ClientboundMoveEntityPacket.Pos packet, @Share("wa") LocalLongRef wa) {
		((ClientboundMoveEntityPacket4) packet).setWa((short) wa.get());
		return packet;
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "NEW",
		target = "(IBBZ)Lnet/minecraft/network/protocol/game/ClientboundMoveEntityPacket$Rot;"
	))
	ClientboundMoveEntityPacket.Rot sendChanges_rot(
		ClientboundMoveEntityPacket.Rot packet,
		@Share("wRotn") LocalByteRef wRotn,
		@Share("vRotn") LocalByteRef vRotn
	) {
		final ClientboundMoveEntityPacket4 packet4 = (ClientboundMoveEntityPacket4) packet;
		packet4.setWRotPacked(wRotn.get());
		packet4.setVRotPacked(vRotn.get());
		return packet;
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "NEW",
		target = "(ISSSBBZ)Lnet/minecraft/network/protocol/game/ClientboundMoveEntityPacket$PosRot;"
	))
	ClientboundMoveEntityPacket.PosRot sendChanges_posRot(
		ClientboundMoveEntityPacket.PosRot packet,
		@Share("wa") LocalLongRef wa,
		@Share("wRotn") LocalByteRef wRotn,
		@Share("vRotn") LocalByteRef vRotn
	) {
		final ClientboundMoveEntityPacket4 packet4 = (ClientboundMoveEntityPacket4) packet;
		packet4.setWa((short) wa.get());
		packet4.setWRotPacked(wRotn.get());
		packet4.setVRotPacked(vRotn.get());
		return packet;
	}
	@Inject(method = "sendChanges", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/server/level/ServerEntity;lastSentXRot:B",
		opcode = Opcodes.PUTFIELD
	))
	void sendChanges_lastSentWRot(CallbackInfo ci,
		@Share("wRotn") LocalByteRef wRotn,
		@Share("vRotn") LocalByteRef vRotn
	) {
		this.lastSentWRot = wRotn.get();
		this.lastSentVRot = vRotn.get();
	}
	@Definition(id = "abs", method = "Ljava/lang/Math;abs(I)I")
	@Definition(id = "lastSentYHeadRot", field = "Lnet/minecraft/server/level/ServerEntity;lastSentYHeadRot:B")
	@Expression("abs(? - this.lastSentYHeadRot) >= 1")
	@ModifyExpressionValue(method = "sendChanges", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean sendChanges_lastSentWHeadRotOnSend(boolean original, @Share("wHeadRot") LocalByteRef wHeadRot) {
		wHeadRot.set(Mth.packDegrees(((Entity4) this.entity).getWHeadRot()));
		return original || Math.abs(wHeadRot.get() - this.lastSentWHeadRot) >= 1;
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/entity/Entity;B)Lnet/minecraft/network/protocol/game/ClientboundRotateHeadPacket;"
	))
	ClientboundRotateHeadPacket sendChanges_ClientboundRotateHeadPacket(ClientboundRotateHeadPacket packet, @Share("wHeadRot") LocalByteRef wHeadRot) {
		((ClientboundRotateHeadPacket4) packet).setWHeadRotPacked(wHeadRot.get());
		this.lastSentWHeadRot = wHeadRot.get();
		return packet;
	}

	@Overwrite
	@Deprecated
	private void handleMinecartPosRot(NewMinecartBehavior newMinecartBehavior, byte yRotn, byte xRotn, boolean shouldSendRotation) {
		throw Err4.rotation(null);
	}
	@Unique
	private void handleMinecartPosRot(NewMinecartBehavior newMinecartBehavior, byte yRotn, byte xRotn, byte wRotn, byte vRotn, boolean shouldSendRotation) {
		this.sendDirtyEntityData();
		if (newMinecartBehavior.lerpSteps.isEmpty()) {
			Vec3 movement = this.entity.getDeltaMovement();
			double diff = movement.distanceToSqr(this.lastSentMovement);
			Vec3 currentPosition = this.entity.trackingPosition();
			boolean positionChanged = this.positionCodec.delta(currentPosition).lengthSqr() >= TOLERANCE_LEVEL_POSITION;
			boolean shouldSendPosition = positionChanged || this.tickCount % FORCED_POS_UPDATE_PERIOD == 0;
			if (shouldSendPosition || shouldSendRotation || diff > AABB4.EPSILON) {
				Entity4 entity4 = (Entity4) this.entity;
				this.synchronizer
					.sendToTrackingPlayers(
						new ClientboundMoveMinecartPacket(
							this.entity.getId(),
							List.of(
								NewMinecartBehavior4.MinecartStep.from(
									this.entity.position(), this.entity.getDeltaMovement(),
									this.entity.getYRot(), this.entity.getXRot(), entity4.getWRot(), entity4.getVRot(),
									1.0F
								)
							)
						)
					);
			}
		} else {
			this.synchronizer.sendToTrackingPlayers(new ClientboundMoveMinecartPacket(this.entity.getId(), List.copyOf(newMinecartBehavior.lerpSteps)));
			newMinecartBehavior.lerpSteps.clear();
		}

		this.lastSentYRot = yRotn;
		this.lastSentXRot = xRotn;
		this.lastSentWRot = wRotn;
		this.lastSentVRot = vRotn;
		this.positionCodec.setBase(this.entity.position());
	}

	@Override
	public float getLastSentWRot() {
		return Mth.unpackDegrees(this.lastSentWRot);
	}
	@Override
	public float getLastSentVRot() {
		return Mth.unpackDegrees(this.lastSentVRot);
	}
	@Override
	public float getLastSentWHeadRot() {
		return Mth.unpackDegrees(this.lastSentWHeadRot);
	}
}
