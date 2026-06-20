package com.iluha168.mc4d.mixin.net.minecraft.server.network;

import com.iluha168.mc4d.network.protocol.game.ServerboundMovePlayerPacket4;
import com.iluha168.mc4d.server.network.ServerGamePacketListenerImpl4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	@Unique
	private double lastGoodW;

	@Overwrite
	public void teleport(double x, double y, double z, float yRot, float xRot) {
		throw Err4.arguments3("ServerGamePacketListenerImpl4#teleport");
	}

	@Override
	public void teleport(Vec4 pos, float yRot, float xRot) {
		this.teleport(new PositionMoveRotation(pos, Vec4.ZERO, yRot, xRot), Collections.emptySet());
	}

	@Inject(method = "resetPosition", at = @At("TAIL"))
	void resetPosition(CallbackInfo ci) {
		this.firstGoodW = this.lastGoodW = ((Entity4) this.player).getW();
	}

	@Redirect(method = "tickPlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V"
	))
	void tickPlayer(ServerPlayer player, double x, double y, double z, float yRot, float xRot) {
		((Entity4) player).absSnapTo(x, y, z, this.firstGoodW, yRot, xRot);
	}

	@Redirect(method = "handleAcceptTeleportPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V"
	))
	void handleAcceptTeleportPacket(ServerPlayer player, double x, double y, double z, float yRot, float xRot) {
		assert this.awaitingPositionFromClient != null;
		double w = ((Vec4) this.awaitingPositionFromClient).w;
		((Entity4) player).absSnapTo(x, y, z, w, yRot, xRot);
		this.lastGoodW = w;
	}

	@ModifyArg(method = "handleMovePlayer", index = 1, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;containsInvalidValues(DDDFF)Z"
	))
	double handleMovePlayer_containsInvalidValues(
		double x,
		@Local(argsOnly = true, name = "packet") ServerboundMovePlayerPacket packet
	) {
		// Not accurate but works if we just want to check if either value is NaN
		return x + ((ServerboundMovePlayerPacket4) packet).getW(0);
	}
	@Definition(id = "clampHorizontal", method = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clampHorizontal(D)D")
	@Definition(id = "packet", local = @Local(type = ServerboundMovePlayerPacket.class, name = "packet", argsOnly = true))
	@Definition(id = "getZ", method = "Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket;getZ(D)D")
	@Expression("clampHorizontal(packet.getZ(?))")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_calculateTargetW(ServerboundMovePlayerPacket packet, CallbackInfo ci, @Share("targetW") LocalDoubleRef targetW) {
		targetW.set(clampHorizontal(((ServerboundMovePlayerPacket4) packet).getW(((Entity4) this.player).getW())));
	}
	@Definition(id = "absSnapTo", method = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V")
	@Definition(id = "getX", method = "Lnet/minecraft/server/level/ServerPlayer;getX()D")
	@Expression("?.absSnapTo(?.getX(), ?, ?, ?, ?)")
	@Redirect(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_absSnapTo(ServerPlayer player, double x, double y, double z, float yRot, float xRot) {
		Entity4 player4 = (Entity4) player;
		player4.absSnapTo(x, y, z, player4.getW(), yRot, xRot);
	}
	@Definition(id = "startZ", local = @Local(type = double.class, name = "startZ"))
	@Definition(id = "getZ", method = "Lnet/minecraft/server/level/ServerPlayer;getZ()D")
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Expression("startZ = this.player.getZ()")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_startW(ServerboundMovePlayerPacket packet, CallbackInfo ci, @Share("startW") LocalDoubleRef startW) {
		startW.set(((Entity4) this.player).getW());
	}
	@Definition(id = "zDist", local = @Local(type = double.class, name = "zDist"))
	@Definition(id = "targetZ", local = @Local(type = double.class, name = "targetZ"))
	@Definition(id = "firstGoodZ", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;firstGoodZ:D")
	@Expression("zDist = targetZ - this.firstGoodZ")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_wDistFirstGood(ServerboundMovePlayerPacket packet, CallbackInfo ci, @Share("wDist") LocalDoubleRef wDist, @Share("targetW") LocalDoubleRef targetW) {
		wDist.set(targetW.get() - this.firstGoodW);
	}
	@Definition(id = "xDist", local = @Local(type = double.class, name = "xDist"))
	@Definition(id = "yDist", local = @Local(type = double.class, name = "yDist"))
	@Definition(id = "zDist", local = @Local(type = double.class, name = "zDist"))
	@Expression("xDist * xDist + yDist * yDist + zDist * zDist")
	@ModifyExpressionValue(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	double handleMovePlayer_movedDist(double original, @Share("wDist") LocalDoubleRef wDist) {
		return original + wDist.get() * wDist.get();
	}
	@Definition(id = "teleport", method = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V")
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "getX", method = "Lnet/minecraft/server/level/ServerPlayer;getX()D")
	@Expression("this.teleport(this.player.getX(), ?, ?, ?, ?)")
	@Redirect(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_teleportWhileSleeping(ServerGamePacketListenerImpl instance, double x, double y, double z, float yRot, float xRot) {
		((ServerGamePacketListenerImpl4) instance).teleport(new Vec4(x, y, z, ((Entity4) this.player).getW()), yRot, xRot);
	}
	@Definition(id = "LOGGER", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;LOGGER:Lorg/slf4j/Logger;")
	@Definition(id = "warn", method = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V")
	@Expression("LOGGER.warn(?, ?)")
	@Redirect(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_logTooQuick(Logger instance, String s, Object[] objects, @Share("wDist") LocalDoubleRef wDist) {
		instance.warn(s + ",{}", ArrayUtils.addAll(objects, wDist.get()));
	}
	@Definition(id = "zDist", local = @Local(type = double.class, name = "zDist"))
	@Definition(id = "targetZ", local = @Local(type = double.class, name = "targetZ"))
	@Definition(id = "lastGoodZ", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;lastGoodZ:D")
	@Expression("zDist = targetZ - this.lastGoodZ")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_wDistLastGood(ServerboundMovePlayerPacket packet, CallbackInfo ci, @Share("wDist") LocalDoubleRef wDist, @Share("targetW") LocalDoubleRef targetW) {
		wDist.set(targetW.get() - this.lastGoodW);
	}
	@Definition(id = "move", method = "Lnet/minecraft/server/level/ServerPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V")
	@Definition(id = "PLAYER", field = "Lnet/minecraft/world/entity/MoverType;PLAYER:Lnet/minecraft/world/entity/MoverType;")
	@Definition(id = "Vec3", type = Vec3.class)
	@Definition(id = "zDist", local = @Local(type = double.class, name = "zDist"))
	@Expression("?.move(PLAYER, new Vec3(?, ?, zDist))")
	@ModifyArg(method = "handleMovePlayer", index = 1, at = @At("MIXINEXTRAS:EXPRESSION"))
	Vec3 handleMovePlayer_move(Vec3 vec3, @Share("wDist") LocalDoubleRef wDist) {
		return Vec4.of(vec3, wDist.get());
	}
	@Definition(id = "zDist", local = @Local(type = double.class, name = "zDist"))
	@Definition(id = "targetZ", local = @Local(type = double.class, name = "targetZ"))
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "getZ", method = "Lnet/minecraft/server/level/ServerPlayer;getZ()D")
	@Expression("zDist = targetZ - this.player.getZ()")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_wDistPlayerW(ServerboundMovePlayerPacket packet, CallbackInfo ci, @Share("wDist") LocalDoubleRef wDist, @Share("targetW") LocalDoubleRef targetW) {
		wDist.set(targetW.get() - ((Entity4) this.player).getW());
	}
	@Redirect(method = "handleMovePlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isEntityCollidingWithAnythingNew(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;DDD)Z",
		ordinal = 0
	))
	boolean handleMovePlayer_isEntityCollidingWithAnythingNew(
		ServerGamePacketListenerImpl instance, LevelReader level, Entity entity, AABB oldAABB,
		double newX, double newY, double newZ, @Share("targetW") LocalDoubleRef targetW
	) {
		return ((ServerGamePacketListenerImpl4) instance).isEntityCollidingWithAnythingNew(level, entity, oldAABB, new Vec4(newX, newY, newZ, targetW.get()));
	}
	@Redirect(method = "handleMovePlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V",
		ordinal = 1
	))
	void handleMovePlayer_snapTo(ServerPlayer player, double x, double y, double z, float yRot, float xRot, @Share("targetW") LocalDoubleRef targetW) {
		((Entity4) player).absSnapTo(x, y, z, targetW.get(), yRot, xRot);
	}
	@Definition(id = "Vec3", type = Vec3.class)
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "getZ", method = "Lnet/minecraft/server/level/ServerPlayer;getZ()D")
	@Definition(id = "startZ", local = @Local(type = double.class, name = "startZ"))
	@Expression("new Vec3(?, ?, this.player.getZ() - startZ)")
	@ModifyExpressionValue(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	Vec3 handleMovePlayer_clientDeltaMovement(Vec3 original, @Share("startW") LocalDoubleRef startW) {
		return Vec4.of(original, ((Entity4) this.player).getW() - startW.get());
	}
	@Definition(id = "doCheckFallDamage", method = "Lnet/minecraft/server/level/ServerPlayer;doCheckFallDamage(DDDZ)V")
	@Definition(id = "clientDeltaMovement", local = @Local(type = Vec3.class, name = "clientDeltaMovement"))
	@Definition(id = "x", field = "Lnet/minecraft/world/phys/Vec3;x:D")
	@Expression("?.doCheckFallDamage(clientDeltaMovement.x, ?, ?, ?)")
	@Redirect(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_doCheckFallDamageClientDeltaMovement(
		ServerPlayer instance, double x, double y, double z, boolean onGround,
		@Local(name = "clientDeltaMovement") Vec3 clientDeltaMovement
	) {
		double w = ((Vec4) clientDeltaMovement).w;
		((Entity4) instance).doCheckFallDamage(x, y, z, w, onGround);
	}
	// TODO                        this.player.checkMovementStatistics(this.player.getX() - startX, this.player.getY() - startY, this.player.getZ() - startZ);
	@Definition(id = "lastGoodZ", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;lastGoodZ:D")
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "getZ", method = "Lnet/minecraft/server/level/ServerPlayer;getZ()D")
	@Expression("this.lastGoodZ = this.player.getZ()")
	@Inject(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_lastGoodW(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
		this.lastGoodW = ((Entity4) this.player).getW();
	}
	@Definition(id = "teleport", method = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V")
	@Definition(id = "startX", local = @Local(type = double.class, name = "startX"))
	@Expression("this.teleport(startX, ?, ?, ?, ?)")
	@Redirect(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_teleportPassThrough(
		ServerGamePacketListenerImpl instance, double x, double y, double z, float yRot, float xRot,
		@Share("startW") LocalDoubleRef startW
	) {
		((ServerGamePacketListenerImpl4) instance).teleport(new Vec4(x, y, z, startW.get()), yRot, xRot);
	}
	@Definition(id = "doCheckFallDamage", method = "Lnet/minecraft/server/level/ServerPlayer;doCheckFallDamage(DDDZ)V")
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "getZ", method = "Lnet/minecraft/server/level/ServerPlayer;getZ()D")
	@Definition(id = "startZ", local = @Local(type = double.class, name = "startZ"))
	@Expression("?.doCheckFallDamage(?, ?, this.player.getZ() - startZ, ?)")
	@Redirect(method = "handleMovePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleMovePlayer_doCheckFallDamagePassThrough(
		ServerPlayer instance, double x, double y, double z, boolean onGround,
		@Share("startW") LocalDoubleRef startW
	) {
		Entity4 player4 = ((Entity4) instance);
		double w = player4.getW() - startW.get();
		player4.doCheckFallDamage(x, y, z, w, onGround);
	}

	@Overwrite
	private boolean isEntityCollidingWithAnythingNew(LevelReader level, Entity entity, AABB oldAABB, double newX, double newY, double newZ) {
		throw Err4.arguments3("ServerGamePacketListenerImpl4#isEntityCollidingWithAnythingNew");
	}
	@Override
	public boolean isEntityCollidingWithAnythingNew(LevelReader level, Entity entity, AABB oldAABB, Vec4 targetPos) {
		AABB4 newAABB = ((AABB4) entity.getBoundingBox()).move(targetPos.subtract(entity.position()));
		Iterable<VoxelShape> newCollisions = level.getPreMoveCollisions(entity, newAABB.deflate(Mth.EPSILON), oldAABB.getBottomCenter());
		VoxelShape oldShape = Shapes.create(oldAABB.deflate(Mth.EPSILON));

		for (VoxelShape shape : newCollisions) {
			if (!Shapes.joinIsNotEmpty(shape, oldShape, BooleanOp.AND)) {
				return true;
			}
		}

		return false;
	}

	@Definition(id = "zo", field = "Lnet/minecraft/server/level/ServerPlayer;zo:D")
	@Definition(id = "getZ", method = "Lnet/minecraft/server/level/ServerPlayer;getZ()D")
	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Expression("this.player.zo = this.player.getZ()")
	@Inject(method = "tickPlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tickPlayer(CallbackInfoReturnable<Boolean> cir) {
		Entity4 player4 = (Entity4) this.player;
		player4.setWO(player4.getW());
	}

	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "zo", field = "Lnet/minecraft/server/level/ServerPlayer;zo:D")
	@Definition(id = "oldPos", local = @Local(type = Vec3.class, name = "oldPos"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Expression("this.player.zo = oldPos.z")
	@Inject(method = "resyncPlayerWithVehicle", at = @At("MIXINEXTRAS:EXPRESSION"))
	void resyncPlayerWithVehicle(Entity vehicle, CallbackInfo ci, @Local(name = "oldPos") Vec3 oldPos) {
		Entity4 player4 = (Entity4) this.player;
		player4.setWO(((Vec4) oldPos).w);
	}

	@ModifyArg(method = "handleClientTickEnd", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;setKnownMovement(Lnet/minecraft/world/phys/Vec3;)V"
	))
	Vec3 handleClientTickEnd(Vec3 lastKnownClientMovement) {
		return Vec4.ZERO;
	}

	@Expression("? < 1.0000001")
	@ModifyExpressionValue(method = "handleUseItemOn", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	boolean handleUseItemOn(boolean original, @Local(name = "distance") Vec3 distance) {
		return original && Math.abs(((Vec4) distance).w) < 1.0000001;
	}
}
