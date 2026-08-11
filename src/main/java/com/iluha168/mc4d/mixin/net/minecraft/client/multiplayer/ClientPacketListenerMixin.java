package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import com.iluha168.mc4d.client.multiplayer.ClientChunkCache4;
import com.iluha168.mc4d.client.multiplayer.ClientLevel4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.network.protocol.game.*;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.PositionMoveRotation4;
import com.iluha168.mc4d.world.entity.Relative4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.border.WorldBorder4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
	@Shadow
	private ClientLevel level;

	@Shadow
	@Final
	private RandomSource random;

	protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
		super(minecraft, connection, cookie);
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	// TODO the rest

	@Redirect(method = "handleEntityPositionSync", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;moveOrInterpolateTo(Lnet/minecraft/world/phys/Vec3;FF)V"
	))
	void handleEntityPositionSync_moveOrInterpolate(Entity entity, Vec3 position, float yRot, float xRot, @Local(argsOnly = true, name = "packet") ClientboundEntityPositionSyncPacket packet) {
		final PositionMoveRotation4 values = PositionMoveRotation4.as(packet.values());
		((Entity4) entity).moveOrInterpolateTo(position, yRot, xRot, values.wRot(), values.vRot());
	}
	@Redirect(method = "handleEntityPositionSync", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(Lnet/minecraft/world/phys/Vec3;FF)V"
	))
	void handleEntityPositionSync_snapTo(Entity entity, Vec3 spawnPos, float yRot, float xRot, @Local(argsOnly = true, name = "packet") ClientboundEntityPositionSyncPacket packet) {
		final PositionMoveRotation4 values = PositionMoveRotation4.as(packet.values());
		((Entity4) entity).snapTo(spawnPos, yRot, xRot, values.wRot(), values.vRot());
	}

	@ModifyExpressionValue(method = "handleTeleportEntity", at = @At(
		value = "NEW",
		target = "(DDDFFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$PosRot;"
	))
	ServerboundMovePlayerPacket.PosRot handleTeleportEntity_posRot(ServerboundMovePlayerPacket.PosRot packet) {
		final Entity4 player4 = (Entity4) this.minecraft.player;
		final ServerboundMovePlayerPacket4 packet4 = (ServerboundMovePlayerPacket4) packet;
		//noinspection DataFlowIssue
		packet4.setW(player4.getW());
		packet4.setWRot(player4.getWRot());
		packet4.setVRot(player4.getVRot());
		return packet;
	}
	@ModifyVariable(method = "handleTeleportEntity", at = @At("STORE"), name = "hasRelative")
	boolean handleTeleportEntity_hasRelative(boolean hasRelative, @Local(argsOnly = true, name = "packet") ClientboundTeleportEntityPacket packet) {
		return hasRelative || packet.relatives().contains(Relative4.W);
	}

	@Redirect(method = "handleMoveEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/protocol/game/VecDeltaCodec;decode(JJJ)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 handleMoveEntity_decode(VecDeltaCodec positionCodec, long xa, long ya, long za, @Local(argsOnly = true, name = "packet") ClientboundMoveEntityPacket packet) {
		return ((VecDeltaCodec4) positionCodec).decode(xa, ya, za, ((ClientboundMoveEntityPacket4) packet).getWa());
	}
	@Redirect(method = "handleMoveEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;moveOrInterpolateTo(Lnet/minecraft/world/phys/Vec3;FF)V"
	))
	void handleMoveEntity_moveOrInterpolatePosRot(Entity entity, Vec3 position, float yRot, float xRot, @Local(argsOnly = true, name = "packet") ClientboundMoveEntityPacket packet) {
		final ClientboundMoveEntityPacket4 packet4 = (ClientboundMoveEntityPacket4) packet;
		((Entity4) entity).moveOrInterpolateTo(position, yRot, xRot, packet4.getWRot(), packet4.getVRot());
	}
	@Redirect(method = "handleMoveEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;moveOrInterpolateTo(FF)V"
	))
	void handleMoveEntity_moveOrInterpolateRot(Entity entity, float yRot, float xRot, @Local(argsOnly = true, name = "packet") ClientboundMoveEntityPacket packet) {
		final ClientboundMoveEntityPacket4 packet4 = (ClientboundMoveEntityPacket4) packet;
		((Entity4) entity).moveOrInterpolateTo(yRot, xRot, packet4.getWRot(), packet4.getVRot());
	}

	@Redirect(method = "handleRotateMob", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;lerpHeadTo(FI)V"
	))
	void handleRotateMob(Entity instance, float yRot, int steps, @Local(argsOnly = true, name = "packet") ClientboundRotateHeadPacket packet) {
		((Entity4) instance).lerpHeadTo(yRot, ((ClientboundRotateHeadPacket4) packet).getWHeadRot(), steps);
	}

	@ModifyExpressionValue(method = "handleMovePlayer", at = @At(
		value = "NEW",
		target = "(DDDFFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$PosRot;"
	))
	ServerboundMovePlayerPacket.PosRot handleMovePlayer(
		ServerboundMovePlayerPacket.PosRot packet,
		@Local(name = "player") Player player
	) {
		final Entity4 player4 = (Entity4) player;
		final ServerboundMovePlayerPacket4 packet4 = (ServerboundMovePlayerPacket4) packet;
		packet4.setW(player4.getW());
		packet4.setWRot(player4.getWRot());
		packet4.setVRot(player4.getVRot());
		return packet;
	}

	@Redirect(method = "setValuesFromPositionPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;moveOrInterpolateTo(Lnet/minecraft/world/phys/Vec3;FF)V"
	))
	private static void setValuesFromPositionPacket_moveOrInterpolate(Entity entity, Vec3 position, float yRot, float xRot, @Local(name = "newValues") PositionMoveRotation newValues) {
		final PositionMoveRotation4 newValues4 = PositionMoveRotation4.as(newValues);
		((Entity4) entity).moveOrInterpolateTo(position, yRot, xRot, newValues4.wRot(), newValues4.vRot());
	}
	@Inject(method = "setValuesFromPositionPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setXRot(F)V"
	))
	private static void setValuesFromPositionPacket_setXRot(
		PositionMoveRotation change, Set<Relative> relatives, Entity entity, boolean interpolate, CallbackInfoReturnable<Boolean> cir,
		@Local(name = "newValues") PositionMoveRotation newValues
	) {
		final Entity4 entity4 = (Entity4) entity;
		final PositionMoveRotation4 newValues4 = PositionMoveRotation4.as(newValues);
		entity4.setWRot(newValues4.wRot());
		entity4.setVRot(newValues4.vRot());
	}
	@ModifyExpressionValue(method = "setValuesFromPositionPacket", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/entity/PositionMoveRotation;"
	))
	private static PositionMoveRotation setValuesFromPositionPacket_init(
		PositionMoveRotation currentInterpolationValues, @Local(argsOnly = true, name = "entity") Entity entity
	) {
		final Entity4 entity4 = (Entity4) entity;
		final PositionMoveRotation4 currentInterpolationValues4 = PositionMoveRotation4.as(currentInterpolationValues);
		currentInterpolationValues4.setWRot(entity4.getWRotO());
		currentInterpolationValues4.setVRot(entity4.getVRotO());
		return currentInterpolationValues;
	}
	@Redirect(method = "setValuesFromPositionPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setOldPosAndRot(Lnet/minecraft/world/phys/Vec3;FF)V"
	))
	private static void setValuesFromPositionPacket_setOldPosAndRot(Entity entity, Vec3 position, float yRot, float xRot, @Local(name = "interpolationValues") PositionMoveRotation interpolationValues) {
		final PositionMoveRotation4 interpolationValues4 = PositionMoveRotation4.as(interpolationValues);
		((Entity4) entity).setOldPosAndRot(position, yRot, xRot, interpolationValues4.wRot(), interpolationValues4.vRot());
	}

	@Redirect(method = "handleRotatePlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Relative;rotation(ZZ)Ljava/util/Set;"
	))
	Set<Relative> handleRotatePlayer_rotation(
		boolean relativeYRot, boolean relativeXRot,
		@Local(argsOnly = true, name = "packet") ClientboundPlayerRotationPacket packet
	) {
		final ClientboundPlayerRotationPacket4 packet4 = ClientboundPlayerRotationPacket4.as(packet);
		return Relative4.rotation(relativeYRot, relativeXRot, packet4.relativeW(), packet4.relativeV());
	}
	@Redirect(method = "handleRotatePlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/PositionMoveRotation;withRotation(FF)Lnet/minecraft/world/entity/PositionMoveRotation;"
	))
	PositionMoveRotation handleRotatePlayer_withRotation(
		PositionMoveRotation currentValues, float yRot, float xRot,
		@Local(argsOnly = true, name = "packet") ClientboundPlayerRotationPacket packet
	) {
		final ClientboundPlayerRotationPacket4 packet4 = ClientboundPlayerRotationPacket4.as(packet);
		return PositionMoveRotation4.as(currentValues).withRotation(yRot, xRot, packet4.wRot(), packet4.vRot());
	}
	@Inject(method = "handleRotatePlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;setXRot(F)V"
	))
	void handleRotatePlayer_setXRot(
		ClientboundPlayerRotationPacket packet, CallbackInfo ci,
		@Local(name = "newValues") PositionMoveRotation newValues,
		@Local(name = "player") Player player
	) {
		final Entity4 player4 = (Entity4) player;
		final PositionMoveRotation4 newValues4 = PositionMoveRotation4.as(newValues);
		player4.setWRot(newValues4.wRot());
		player4.setVRot(newValues4.vRot());
	}
	@ModifyExpressionValue(method = "handleRotatePlayer", at = @At(
		value = "NEW",
		target = "(FFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$Rot;"
	))
	ServerboundMovePlayerPacket.Rot handleRotatePlayer_rot(ServerboundMovePlayerPacket.Rot packet, @Local(name = "player") Player player) {
		final Entity4 player4 = (Entity4) player;
		final ServerboundMovePlayerPacket4 packet4 = (ServerboundMovePlayerPacket4) packet;
		packet4.setWRot(player4.getWRot());
		packet4.setVRot(player4.getVRot());
		return packet;
	}

	// TODO the rest

	@Definition(id = "updateLevelChunk", method = "Lnet/minecraft/client/multiplayer/ClientPacketListener;updateLevelChunk(IILnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData;)V")
	@Expression("this.updateLevelChunk(?, ?, ?)")
	@Redirect(method = "handleLevelChunkWithLight", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleLevelChunkWithLight(
		ClientPacketListener This, int x, int z, ClientboundLevelChunkPacketData chunkData,
		@Share("w") LocalIntRef w,
		@Local(argsOnly = true, name = "packet") ClientboundLevelChunkWithLightPacket packet
	) {
		w.set(((ClientboundLevelChunkWithLightPacket4) packet).getW());
		this.updateLevelChunk(x, z, w.get(), chunkData);
	}
	@ModifyArg(method = "handleLevelChunkWithLight", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;queueLightUpdate(Ljava/lang/Runnable;)V"
	))
	Runnable handleLevelChunkWithLight(
		Runnable update,
		@Local(name = "x") int x,
		@Local(name = "z") int z,
		@Share("w") LocalIntRef wRef,
		@Local(name = "lightData") ClientboundLightUpdatePacketData lightData
	) {
		final int w = wRef.get();
		return () -> {
			this.applyLightData(x, z, w, lightData, false);
			LevelChunk chunk = ((ChunkSource4) this.level.getChunkSource()).getChunk(x, z, w, false);
			if (chunk != null) {
				this.enableChunkLight(chunk, x, z, w);
				this.minecraft.levelRenderer.onChunkReadyToRender(chunk.getPos());
			}
		};
	}

	// TODO handleChunksBiomes

	@Overwrite
	@Deprecated
	private void updateLevelChunk(int x, int z, ClientboundLevelChunkPacketData chunkData) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void updateLevelChunk(int x, int z, int w, ClientboundLevelChunkPacketData chunkData) {
		((ClientChunkCache4) this.level.getChunkSource()).replaceWithPacketData(
			x, z, w, chunkData.getReadBuffer(), chunkData.getHeightmaps(),
			((ClientboundLevelChunkPacketData4) chunkData).getBlockEntitiesTagsConsumer(x, z, w)
		);
	}

	@Overwrite
	@Deprecated
	private void enableChunkLight(LevelChunk chunk, int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void enableChunkLight(LevelChunk chunk, int x, int z, int w) {
		LevelLightEngine lightEngine = this.level.getChunkSource().getLightEngine();
		LevelChunkSection[] sections = chunk.getSections();
		ChunkPos chunkPos = chunk.getPos();

		for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
			LevelChunkSection section = sections[sectionIndex];
			int sectionY = this.level.getSectionYFromSectionIndex(sectionIndex);
			lightEngine.updateSectionStatus(SectionPos.of(chunkPos, sectionY), section.hasOnlyAir());
		}

		((ClientLevel4) this.level).setSectionRangeDirty(
			x - 1, this.level.getMinSectionY(), z - 1, w - 1,
			x + 1, this.level.getMaxSectionY(), z + 1, w + 1
		);
	}

	@Redirect(method = "handleTakeItemEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void handleTakeItemEntity(
		ClientLevel instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(name = "from") Entity from
	) {
		((Level4) instance).playLocalSound(x, y, z, ((Entity4) from).getW(), sound, source, volume, pitch, distanceDelay);
	}

	// TODO handleEntityEvent
	// TODO handleExplosion
	// TODO handleGameEvent

	@Redirect(method = "handleSetEntityPassengersPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;setYHeadRot(F)V"
	))
	void handleSetEntityPassengersPacket(LocalPlayer player, float yHeadRot, @Local(name = "vehicle") Entity vehicle) {
		final Entity4 player4 = ((Entity4) player);
		final Entity4 vehicle4 = ((Entity4) vehicle);
		player4.setWRotO(vehicle4.getWRot());
		player4.setWRot(vehicle4.getWRot());
		player4.setYHeadRot(yHeadRot, vehicle4.getWRot());
	}

	// TODO the rest

	@Redirect(method = "handleInitializeBorder", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;setCenter(DD)V"
	))
	void handleInitializeBorder(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "packet") ClientboundInitializeBorderPacket packet) {
		((WorldBorder4) instance).setCenter(x, z, ((ClientboundInitializeBorderPacket4) packet).getNewCenterW());
	}

	// TODO the rest

	@Redirect(method = "handleSoundEvent", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
	))
	void handleSoundEvent(
		ClientLevel level, Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed,
		@Local(argsOnly = true, name = "packet") ClientboundSoundPacket packet
	) {
		((Level4) level).playSeededSound(except, x, y, z, ((ClientboundSoundPacket4) packet).getW(), sound, source, volume, pitch, seed);
	}

	// TODO the rest

	@Definition(id = "addParticle", method = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V")
	@Definition(id = "getZ", method = "Lnet/minecraft/network/protocol/game/ClientboundLevelParticlesPacket;getZ()D")
	@Expression("?.addParticle(?, ?, ?, ?, ?, ?.getZ(), ?, ?, ?)")
	@Redirect(method = "handleParticleEvent", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleParticleEvent_count0(
		ClientLevel level, ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "packet") ClientboundLevelParticlesPacket packet
	) {
		ClientboundLevelParticlesPacket4 packet4 = (ClientboundLevelParticlesPacket4) packet;
		((Level4) level).addParticle(
			particle, overrideLimiter, alwaysShow,
			x, y, z, packet4.getW(),
			xd, yd, zd, packet.getMaxSpeed() * packet4.getWDist()
		);
	}

	@Definition(id = "addParticle", method = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V")
	@Definition(id = "getZ", method = "Lnet/minecraft/network/protocol/game/ClientboundLevelParticlesPacket;getZ()D")
	@Definition(id = "zVarience", local = @Local(type = double.class, name = "zVarience"))
	@Expression("?.addParticle(?, ?, ?, ?, ?, ?.getZ() + zVarience, ?, ?, ?)")
	@Redirect(method = "handleParticleEvent", at = @At("MIXINEXTRAS:EXPRESSION"))
	void handleParticleEvent_multiple(
		ClientLevel level, ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "packet") ClientboundLevelParticlesPacket packet
	) {
		ClientboundLevelParticlesPacket4 packet4 = (ClientboundLevelParticlesPacket4) packet;
		((Level4) level).addParticle(
			particle, overrideLimiter, alwaysShow,
			x, y, z, packet4.getW() + this.random.nextGaussian() * packet4.getWDist(),
			xd, yd, zd, this.random.nextGaussian() * packet.getMaxSpeed()
		);
	}

	@ModifyArg(method = "handleLightUpdatePacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;queueLightUpdate(Ljava/lang/Runnable;)V"
	))
	Runnable handleLightUpdatePacket(
		Runnable update,
		@Local(name = "x") int x,
		@Local(name = "z") int z,
		@Local(name = "lightData") ClientboundLightUpdatePacketData lightData,
		@Local(argsOnly = true, name = "packet") ClientboundLightUpdatePacket packet
	) {
		final int w = ((ClientboundLightUpdatePacket4) packet).getW();
		return () -> this.applyLightData(x, z, w, lightData, true);
	}

	@Overwrite
	@Deprecated
	private void applyLightData(int x, int z, ClientboundLightUpdatePacketData lightData, boolean scheduleRebuild) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void applyLightData(int x, int z, int w, ClientboundLightUpdatePacketData lightData, boolean scheduleRebuild) {
		LevelLightEngine lightEngine = this.level.getChunkSource().getLightEngine();
		BitSet skyYMask = lightData.getSkyYMask();
		BitSet emptySkyYMask = lightData.getEmptySkyYMask();
		Iterator<byte[]> skyUpdates = lightData.getSkyUpdates().iterator();
		this.readSectionList(x, z, w, lightEngine, LightLayer.SKY, skyYMask, emptySkyYMask, skyUpdates, scheduleRebuild);
		BitSet blockYMask = lightData.getBlockYMask();
		BitSet emptyBlockYMask = lightData.getEmptyBlockYMask();
		Iterator<byte[]> blockUpdates = lightData.getBlockUpdates().iterator();
		this.readSectionList(x, z, w, lightEngine, LightLayer.BLOCK, blockYMask, emptyBlockYMask, blockUpdates, scheduleRebuild);
		lightEngine.setLightEnabled(ChunkPos4.from(x, z, w), true);
	}

	// TODO the rest

	@Redirect(method = "handleSetChunkCacheCenter", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientChunkCache;updateViewCenter(II)V"
	))
	private void handleSetChunkCacheCenter(ClientChunkCache chunkSource, int x, int z, @Local(argsOnly = true, name = "packet") ClientboundSetChunkCacheCenterPacket packet) {
		((ClientChunkCache4) chunkSource).updateViewCenter(x, z, ((ClientboundSetChunkCacheCenterPacket4) packet).getW());
	}

	// TODO the rest

	@Overwrite
	@Deprecated
	private void readSectionList(
		int chunkX, int chunkZ,
		LevelLightEngine lightEngine,
		LightLayer layer,
		BitSet yMask,
		BitSet emptyYMask,
		Iterator<byte[]> updates,
		boolean scheduleRebuild
	) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void readSectionList(
		int chunkX, int chunkZ, int chunkW,
		LevelLightEngine lightEngine,
		LightLayer layer,
		BitSet yMask,
		BitSet emptyYMask,
		Iterator<byte[]> updates,
		boolean scheduleRebuild
	) {
		for (int sectionIndex = 0; sectionIndex < lightEngine.getLightSectionCount(); sectionIndex++) {
			int sectionY = lightEngine.getMinLightSection() + sectionIndex;
			boolean haveData = yMask.get(sectionIndex);
			boolean haveEmpty = emptyYMask.get(sectionIndex);
			if (haveData || haveEmpty) {
				lightEngine.queueSectionData(
					layer, SectionPos4.of(chunkX, sectionY, chunkZ, chunkW), haveData ? new DataLayer(updates.next().clone()) : new DataLayer()
				);
				if (scheduleRebuild) {
					((ClientLevel4) this.level).setSectionDirtyWithNeighbors(chunkX, sectionY, chunkZ, chunkW);
				}
			}
		}
	}

	// TODO the rest
}
