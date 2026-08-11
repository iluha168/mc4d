package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.MC4D;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.mixin.net.minecraft.world.entity.player.PlayerMixin;
import com.iluha168.mc4d.network.protocol.game.ClientboundPlayerLookAtPacket4;
import com.iluha168.mc4d.network.protocol.game.ClientboundPlayerRotationPacket4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.player.Input4;
import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends PlayerMixin {
	@Shadow
	public ServerGamePacketListenerImpl connection;

	@Shadow
	private Input lastClientInput;

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;of(III)Lnet/minecraft/core/SectionPos;"
	))
	SectionPos init_lastSectionPos(int x, int y, int z) {
		return SectionPos4.of(x, y, z, z);
	}

	// TODO readAdditionalSaveData
	// TODO addAdditionalSaveData
	// TODO tick_absSnapTo
	// TODO playShoulderEntityAmbientSound
	// TODO respawnEntityOnShoulder
	// TODO tellNeutralMobsThatIDied
	// TODO findRespawnPositionAndUseSpawnBlock
	// TODO findRespawnAndUseSpawnBlock

	@SuppressWarnings({"deprecation", "RedundantMethodOverride"})
	@Overwrite
	@Deprecated
	public void forceSetRotation(float yRot, boolean relativeY, float xRot, boolean relativeX) {
		throw Err4.rotation("Entity4#forceSetRotation");
	}
	@Override
	public void forceSetRotation(float yRot, boolean relativeY, float xRot, boolean relativeX, float wRot, boolean relativeW, float vRot, boolean relativeV) {
		super.forceSetRotation(yRot, relativeY, xRot, relativeX, wRot, relativeW, vRot, relativeV);
		this.connection.send(ClientboundPlayerRotationPacket4.from(yRot, relativeY, xRot, relativeX, wRot, relativeW, vRot, relativeV));
	}

	@Redirect(method = "lambda$startSleepInBed$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;of(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FF)Lnet/minecraft/world/level/storage/LevelData$RespawnData;"
	))
	LevelData.RespawnData startSleepInBed_respawnData(ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch) {
		return LevelData4.RespawnData.of(dimension, pos, yaw, pitch, this.getWRot(), this.getVRot());
	}
	@Redirect(method = "lambda$startSleepInBed$0", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB startSleepInBed_nearbyMobs(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(name = "bedCenter") Vec3 bedCenter) {
		final double bedCenterW = ((Vec4) bedCenter).w;
		return new AABB4(minX, minY, minZ, bedCenterW - 8.0, maxX, maxY, maxZ, bedCenterW + 8.0);
	}

	// TODO isReachableBedBlock
	// TODO stopSleepInBed
	// TODO checkFallDamage
	// TODO rideTick
	// TODO checkMovementStatistics
	// TODO checkRidingStatistics
	// TODO didNotMove

	@ModifyExpressionValue(method = "lookAt(Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;DDD)Lnet/minecraft/network/protocol/game/ClientboundPlayerLookAtPacket;"
	))
	ClientboundPlayerLookAtPacket lookAt(ClientboundPlayerLookAtPacket packet, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		((ClientboundPlayerLookAtPacket4) packet).setW(((Vec4) pos).w);
		return packet;
	}

	// TODO teleportTo
	// TODO teleportRelative
	// TODO teleportTo
	// TODO snapTo
	// TODO setCamera
	// TODO indicateDamage
	// TODO startRiding

	@Inject(method = "setKnownMovement", at = @At("HEAD"))
	void setKnownMovement(Vec3 lastKnownClientMovement, CallbackInfo ci) {
		if (!(lastKnownClientMovement instanceof Vec4)) {
			throw Err4.container3();
		}
	}

	@Redirect(method = "getLastClientMoveIntent", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getLastClientMoveIntent_anaIntent(double x, double y, double z) {
		final Input4 lastClientInput4 = Input4.as(this.lastClientInput);
		final float anaIntent = lastClientInput4.ana() == lastClientInput4.kata() ? 0.0F : (lastClientInput4.ana() ? 1.0F : -1.0F);
		return new Vec4(x, y, z, anaIntent);
	}
	@Redirect(method = "getLastClientMoveIntent", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerPlayer;getInputVector(Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getLastClientMoveIntent_getInputVector(Vec3 input, float speed, float yRot) {
		// Mirrors LocalPlayerMixin: keybind movement is camera-slice relative.
		return Entity4.getInputVector((Vec4) input, speed, yRot, MC4D.getCameraSliceWRot(), MC4D.getCameraSliceVRot());
	}

	// TODO RespawnPosAngle

	@Mixin(ServerPlayer.SavedPosition.class)
	static class SavedPositionMixin {
		@ModifyExpressionValue(method = "lambda$static$0", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;",
			opcode = Opcodes.GETSTATIC
		))
		private static Codec<Vec4> CODEC_Pos(Codec<Vec3> original) {
			return Vec4.CODEC;
		}
		@ModifyExpressionValue(method = "lambda$static$0", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/phys/Vec2;CODEC:Lcom/mojang/serialization/Codec;",
			opcode = Opcodes.GETSTATIC
		))
		private static Codec<RotationVec> CODEC_Rotation(Codec<Vec2> original) {
			return RotationVec.CODEC;
		}
	}
}
