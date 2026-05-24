package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.network.protocol.game.ServerboundMovePlayerPacket4;
import com.iluha168.mc4d.server.network.ServerGamePacketListenerImpl4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Util;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Set;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerGamePacketListenerImpl4 {
	@Shadow
	public abstract void teleport(PositionMoveRotation destination, Set<Relative> relatives);

	@Shadow
	public ServerPlayer player;
	@Shadow
	private @Nullable Vec3 awaitingPositionFromClient;

	@Shadow
	private static double clampHorizontal(double value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique
	private double firstGoodW;

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, making call sites use teleport(Vec4, yRot, xRot).
	 */
	@Overwrite
	public void teleport(double x, double y, double z, float yRot, float xRot) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use ServerGamePacketListenerImpl4.teleport."));
	}

	@Override
	public void teleport(Vec4 pos, float yRot, float xRot) {
		this.teleport(new PositionMoveRotation(pos, Vec3.ZERO, yRot, xRot), Collections.emptySet());
	}

	@Inject(method = "resetPosition", at = @At("TAIL"))
	void resetPosition(CallbackInfo ci) {
		this.firstGoodW = ((Entity4) this.player).getW();
	}

	@Redirect(method = "tickPlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V"
	))
	void tickPlayer(ServerPlayer player, double x, double y, double z, float yRot, float xRot) {
		((Entity4) player).absSnapTo(
			new Vec4(x, y, z, this.firstGoodW),
			yRot, xRot
		);
	}

	@Redirect(method = "handleAcceptTeleportPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V"
	))
	void handleAcceptTeleportPacket(ServerPlayer player, double x, double y, double z, float yRot, float xRot) {
		assert this.awaitingPositionFromClient != null;
		((Entity4) player).absSnapTo(
			new Vec4(x, y, z, ((Vec4) this.awaitingPositionFromClient).w),
			yRot, xRot
		);
	}

	@Definition(id = "clampHorizontal", method = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clampHorizontal(D)D")
	@Definition(id = "packet", local = @Local(type = ServerboundMovePlayerPacket.class, name = "packet", argsOnly = true))
	@Definition(id = "getZ", method = "Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket;getZ(D)D")
	@Expression("clampHorizontal(packet.getZ(?))")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayerCalculateTargetW(ServerboundMovePlayerPacket packet, CallbackInfo ci, @Share("targetW") LocalDoubleRef targetW) {
		// double targetW = clampHorizontal(packet.getW(this.player.getW()));
		targetW.set(clampHorizontal(((ServerboundMovePlayerPacket4) packet).getW(((Entity4) this.player).getW())));
	}

	@Redirect(method = "handleMovePlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V",
		ordinal = 1
	))
	void handleMovePlayerSnapTo(ServerPlayer player, double x, double y, double z, float yRot, float xRot, @Share("targetW") LocalDoubleRef targetW) {
		((Entity4) player).absSnapTo(
			new Vec4(x, y, z, targetW.get()),
			yRot, xRot
		);
	}
}
