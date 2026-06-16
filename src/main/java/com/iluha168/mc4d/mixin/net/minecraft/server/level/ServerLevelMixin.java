package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.LevelMixin;
import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
class ServerLevelMixin extends LevelMixin implements ServerLevel4 {
	@Shadow
	public ServerChunkCache getChunkSource() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	public static BlockPos END_SPAWN_POINT;

	@Shadow
	@Final
	private ServerChunkCache chunkSource;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void staticFixes(CallbackInfo ci) {
		Vec4i.setW(END_SPAWN_POINT, END_SPAWN_POINT.getZ());
	}

	@Overwrite
	@Deprecated
	public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ) {
		throw Err4.arguments3("LevelReader4#getUncachedNoiseBiome");
	}
	@Override
	public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ, int quartW) {
		return ((BiomeResolver4) this.getChunkSource().getGenerator().getBiomeSource()).getNoiseBiome(quartX, quartY, quartZ, quartW, this.getChunkSource().randomState().sampler());
	}

	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Definition(id = "chunkPos", local = @Local(type = ChunkPos.class, name = "chunkPos"))
	@Definition(id = "getMinBlockZ", method = "Lnet/minecraft/world/level/ChunkPos;getMinBlockZ()I")
	@Expression("minZ = @(chunkPos.getMinBlockZ())")
	@Inject(method = "tickChunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tickChunk_minW(LevelChunk chunk, int tickSpeed, CallbackInfo ci, @Share("minW") LocalIntRef minW, @Local(name = "chunkPos") ChunkPos chunkPos) {
		minW.set(ChunkPos4.as(chunkPos).getMinBlockW());
	}
	@Definition(id = "getBlockRandomPos", method = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "minX", local = @Local(type = int.class, name = "minX"))
	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Expression("?.getBlockRandomPos(minX, ?, minZ, ?)")
	@Redirect(method = "tickChunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockPos tickChunk_getBlockRandomPos(ServerLevel instance, int x, int y, int z, int yMask, @Share("minW") LocalIntRef minW) {
		return ((Level4) instance).getBlockRandomPos(x, y, z, minW.get(), yMask);
	}
	@Redirect(method = "tickChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState tickChunk_getBlockState(
		LevelChunkSection instance, int sectionX, int sectionY, int sectionZ,
		@Local(name = "pos") BlockPos pos, @Share("minW") LocalIntRef minW
	) {
		return ((LevelChunkSection4) instance).getBlockState(sectionX, sectionY, sectionZ, Vec4i.getW(pos) - minW.get());
	}

	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Definition(id = "chunkPos", local = @Local(type = ChunkPos.class, name = "chunkPos"))
	@Definition(id = "getMinBlockZ", method = "Lnet/minecraft/world/level/ChunkPos;getMinBlockZ()I")
	@Expression("minZ = @(chunkPos.getMinBlockZ())")
	@Inject(method = "tickThunder", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tickThunder_minW(LevelChunk chunk, CallbackInfo ci, @Share("minW") LocalIntRef minW, @Local(name = "chunkPos") ChunkPos chunkPos) {
		minW.set(ChunkPos4.as(chunkPos).getMinBlockW());
	}
	@Definition(id = "getBlockRandomPos", method = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "minX", local = @Local(type = int.class, name = "minX"))
	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Expression("?.getBlockRandomPos(minX, ?, minZ, ?)")
	@Redirect(method = "tickThunder", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockPos tickThunder_getBlockRandomPos(ServerLevel instance, int x, int y, int z, int yMask, @Share("minW") LocalIntRef minW) {
		return ((Level4) instance).getBlockRandomPos(x, y, z, minW.get(), yMask);
	}
	@Definition(id = "horse", local = @Local(type = SkeletonHorse.class, name = "horse"))
	@Definition(id = "setPos", method = "Lnet/minecraft/world/entity/animal/equine/SkeletonHorse;setPos(DDD)V")
	@Expression("horse.setPos(?, ?, ?)")
	@Redirect(method = "tickThunder", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tickThunder_moveHorse(SkeletonHorse instance, double x, double y, double z, @Local(name = "pos") BlockPos pos) {
		instance.setPos(new Vec4(x, y, z, Vec4i.getW(pos)));
	}

	@Redirect(method = "lambda$findLightningRod$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
	))
	int findLightningRod(ServerLevel instance, Heightmap.Types types, int x, int z, @Local(argsOnly = true, name = "lightningRodPos") BlockPos lightningRodPos) {
		return ((LevelReader4) instance).getHeight(types, x, z, Vec4i.getW(lightningRodPos));
	}

	@Redirect(method = "getCurrentDifficultyAt", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	ChunkAccess getCurrentDifficultyAt(ServerLevel instance, int x, int z, ChunkStatus chunkStatus, boolean loadOrGenerate, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((LevelReader4) instance).getChunk(x, z, SectionPos.blockToSectionCoord(Vec4i.getW(pos)), chunkStatus, loadOrGenerate);
	}

	@Definition(id = "zd", local = @Local(type = double.class, name = "zd"))
	@Expression("zd * zd")
	@ModifyExpressionValue(method = "destroyBlockProgress", at = @At("MIXINEXTRAS:EXPRESSION"))
	double destroyBlockProgress(double original, @Local(argsOnly = true, name = "blockPos") BlockPos blockPos, @Local(name = "player") ServerPlayer player) {
		final double wd = Vec4i.getW(blockPos) - ((Entity4) player).getW();
		return original + wd * wd;
	}

	// TODO playSeededSound
	// TODO playSeededSound
	// TODO levelEvent
	// TODO explode
	// TODO runBlockEvents
	// TODO sendParticles
	// TODO sendParticles
	// TODO sendParticles

	@Overwrite
	@Deprecated
	private boolean sendParticles(ServerPlayer player, boolean overrideLimiter, double x, double y, double z, Packet<?> packet) {
		// TODO 4D particle engine
		return true;
	}

	@Overwrite
	@Deprecated
	public boolean setChunkForced(int chunkX, int chunkZ, boolean forced) {
		throw Err4.arguments2("ServerLevel4#setChunkForced");
	}
	@Override
	public boolean setChunkForced(int chunkX, int chunkZ, int chunkW, boolean forced) {
		boolean updated = this.chunkSource.updateChunkForced(ChunkPos4.from(chunkX, chunkZ, chunkW), forced);
		if (forced && updated) {
			this.getChunk(chunkX, chunkZ, chunkW);
		}
		return updated;
	}

	// TODO dumpEntities
	// TODO dumpBlockEntityTickers
}
