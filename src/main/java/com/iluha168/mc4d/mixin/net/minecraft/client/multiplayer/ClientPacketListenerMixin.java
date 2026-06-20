package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import com.iluha168.mc4d.client.multiplayer.ClientChunkCache4;
import com.iluha168.mc4d.client.multiplayer.ClientLevel4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.network.protocol.game.*;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.border.WorldBorder4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.*;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PositionMoveRotation;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.BitSet;
import java.util.Iterator;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
	// TODO the rest

	@Shadow
	private ClientLevel level;

	@Shadow
	@Final
	private RandomSource random;

	protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
		super(minecraft, connection, cookie);
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Redirect(method = "handleMovePlayer", at = @At(
		value = "NEW",
		target = "(DDDFFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$PosRot;"
	))
	ServerboundMovePlayerPacket.PosRot handleMovePlayer4(
		double x, double y, double z, float yRot, float xRot, boolean onGround, boolean horizontalCollision,
		@Local(name = "player") Player player
	) {
		final double w = ((Entity4) player).getW();
		return new ServerboundMovePlayerPacket.PosRot(new Vec4(x, y, z, w), yRot, xRot, onGround, horizontalCollision);
	}

	@WrapOperation(method = "setValuesFromPositionPacket", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/entity/PositionMoveRotation;"
	))
	private static PositionMoveRotation setValuesFromPositionPacket(Vec3 position, Vec3 deltaMovement, float yRot, float xRot, Operation<PositionMoveRotation> original) {
		return original.call(position, Vec4.ZERO, yRot, xRot);
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

	// TODO the rest

	@Redirect(method = "handleInitializeBorder", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;setCenter(DD)V"
	))
	void handleInitializeBorder(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "packet") ClientboundInitializeBorderPacket packet) {
		((WorldBorder4) instance).setCenter(x, z, ((ClientboundInitializeBorderPacket4) packet).getNewCenterW());
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
