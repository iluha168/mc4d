package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.level.border.WorldBorder4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public abstract class LevelMixin implements Level4, LevelAccessor4 {
	@Shadow
	protected int randValue;

	@Shadow
	public RegistryAccess registryAccess() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow @Final protected RandomSource random;

	@Shadow
	public BlockState getBlockState(BlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public FluidState getFluidState(BlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	private RandomSource soundSeedGenerator;

	@Shadow
	public ResourceKey<Level> dimension()  {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = 30000000))
	private static int isInWorldBoundsHorizontal(int value) {
		return Level4.MAX_LEVEL_SIZE;
	}
	@ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = -30000000))
	private static int isInWorldBoundsHorizontal_(int value) {
		return -Level4.MAX_LEVEL_SIZE;
	}
	@WrapMethod(method = "isInWorldBoundsHorizontal")
	private static boolean isInWorldBoundsHorizontal4(BlockPos pos, Operation<Boolean> original) {
		final int w = Vec4i.getW(pos);
		return original.call(pos) && w >= -Level4.MAX_LEVEL_SIZE && w < Level4.MAX_LEVEL_SIZE;
	}

	@Redirect(method = "isInValidBoundsHorizontal", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;isValid(II)Z"
	))
	private static boolean isInValidBoundsHorizontal(int x, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int chunkW = SectionPos.blockToSectionCoord(Vec4i.getW(pos));
		return ChunkPos4.isValid(x, z, chunkW);
	}

	@Definition(id = "getChunk", method = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;")
	@Expression("this.getChunk(?, ?)")
	@Redirect(method = "getChunkAt", at = @At("MIXINEXTRAS:EXPRESSION"))
	LevelChunk getChunkAt(Level This, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getChunk(chunkX, chunkZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Overwrite
	@Deprecated
	public LevelChunk getChunk(int chunkX, int chunkZ) {
		throw Err4.arguments2("Level4#getChunk");
	}
	@Override
	public LevelChunk getChunk(int chunkX, int chunkZ, int chunkW) {
		return (LevelChunk) this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.FULL);
	}

	@Overwrite
	@Deprecated
	public @Nullable ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus status, boolean loadOrGenerate) {
		throw Err4.arguments2("LevelReader4#getChunk");
	}
	@Override
	public @Nullable ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW, ChunkStatus status, boolean loadOrGenerate) {
		ChunkAccess chunk = ((ChunkSource4) ((LevelAccessor) this).getChunkSource()).getChunk(chunkX, chunkZ, chunkW, status, loadOrGenerate);
		if (chunk == null && loadOrGenerate) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Overwrite
	@Deprecated
	public int getHeight(Heightmap.Types type, int x, int z) {
		throw Err4.arguments2("Level4#getHeight");
	}
	@Override
	public int getHeight(Heightmap.Types type, int x, int z, int w) {
		if (x < -Level4.MAX_LEVEL_SIZE || z < -Level4.MAX_LEVEL_SIZE || w < -Level4.MAX_LEVEL_SIZE ||
			x >= Level4.MAX_LEVEL_SIZE || z >= Level4.MAX_LEVEL_SIZE || w >= Level4.MAX_LEVEL_SIZE
		) {
			return ((LevelReader) this).getSeaLevel() + 1;
		}
		if (this.hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), SectionPos.blockToSectionCoord(w))) {
			return ((ChunkAccess4) this
				.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), SectionPos.blockToSectionCoord(w)))
				.getHeight(type, SectionPos.sectionRelative(x), SectionPos.sectionRelative(z), SectionPos.sectionRelative(w)) + 1;
		}
		return ((LevelReader) this).getMinY();
	}

	@Definition(id = "getChunk", method = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;")
	@Expression("this.getChunk(?, ?)")
	@Redirect(method = "getBlockState", at = @At("MIXINEXTRAS:EXPRESSION"))
	LevelChunk getBlockState(Level This, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getChunk(chunkX, chunkZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Redirect(method = "playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void playSound_blockPos(Level instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		this.playSound(except, x, y, z, Vec4i.getW(pos) + 0.5, sound, source, volume, pitch);
	}

	@Overwrite
	@Deprecated
	public void playSeededSound(@Nullable Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, long seed) {
		throw Err4.arguments3("Level4#playSeededSound");
	}
	@Override
	public void playSeededSound(@Nullable Entity except, double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch, long seed) {
		this.playSeededSound(except, x, y, z, w, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, volume, pitch, seed);
	}

	@Overwrite
	@Deprecated
	public void playSound(@Nullable Entity except, double x, double y, double z, SoundEvent sound, SoundSource source) {
		throw Err4.arguments3("Level4#playSound");
	}
	@Override
	public void playSound(@Nullable Entity except, double x, double y, double z, double w, SoundEvent sound, SoundSource source) {
		this.playSound(except, x, y, z, w, sound, source, 1.0F, 1.0F);
	}

	@Overwrite
	@Deprecated
	public void playSound(@Nullable Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		throw Err4.arguments3("Level4#playSound");
	}
	@Override
	public void playSound(@Nullable Entity except, double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch) {
		this.playSeededSound(except, x, y, z, w, sound, source, volume, pitch, this.soundSeedGenerator.nextLong());
	}

	@Overwrite
	@Deprecated
	public void playSound(@Nullable Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
		throw Err4.arguments3("Level4#playSound");
	}
	@Override
	public void playSound(@Nullable Entity except, double x, double y, double z, double w, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
		this.playSeededSound(except, x, y, z, w, sound, source, volume, pitch, this.soundSeedGenerator.nextLong());
	}

	@Redirect(method = "playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void playLocalSound_blockPos(Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		this.playLocalSound(x, y, z, Vec4i.getW(pos) + 0.5, sound, source, volume, pitch, distanceDelay);
	}

	@Overwrite
	@Deprecated
	public void playLocalSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		throw Err4.arguments3("Level4#playLocalSound");
	}

	@Overwrite
	@Deprecated
	public void addParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3("LevelAccessor4#addParticle");
	}
	@Override
	public void addParticle(ParticleOptions particle, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {}

	@Overwrite
	@Deprecated
	public void addParticle(
		ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double xd, double yd, double zd
	) {
		throw Err4.arguments3("Level4#addParticle");
	}
	@Override
	public void addParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {}


	@Overwrite
	@Deprecated
	public void addAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3("Level4#addAlwaysVisibleParticle");
	}
	@Override
	public void addAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {}

	@Overwrite
	@Deprecated
	public void addAlwaysVisibleParticle(ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3("Level4#addAlwaysVisibleParticle");
	}
	@Override
	public void addAlwaysVisibleParticle(ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {}

	// TODO explode
	// TODO explode
	// TODO explode
	// TODO explode

	@Redirect(method = "isLoaded", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/ChunkSource;hasChunk(II)Z"
	))
	boolean isLoaded(ChunkSource instance, int x, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((ChunkSource4) instance).hasChunk(x, z, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Definition(id = "getChunk", method = "Lnet/minecraft/world/level/Level;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;")
	@Expression("this.getChunk(?, ?, ?, ?)")
	@Redirect(method = "loadedAndEntityCanStandOnFace", at = @At("MIXINEXTRAS:EXPRESSION"))
	ChunkAccess loadedAndEntityCanStandOnFace(Level This, int chunkX, int chunkZ, ChunkStatus status, boolean loadOrGenerate, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getChunk(chunkX, chunkZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)), status, loadOrGenerate);
	}

	@Redirect(method = "getWorldBorderAdjustedRespawnData", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getWorldBorderAdjustedRespawnData(double x, double y, double z, @Local(name = "worldBorder") WorldBorder worldBorder) {
		return BlockPos4.containing(x, y, z, ((WorldBorder4) worldBorder).getCenterW());
	}

	@Overwrite
	@Deprecated
	public @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
		throw Err4.arguments2("LevelReader4#getChunkForCollisions");
	}
	@Override
	public @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ, int chunkW) {
		return this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.FULL, false);
	}

	// TODO createFireworks

	@Overwrite
	public BlockPos getBlockRandomPos(int xo, int yo, int zo, int yMask) {
		throw Err4.arguments3("Level4#getBlockRandomPos");
	}
	@Override
	public BlockPos getBlockRandomPos(int xo, int yo, int zo, int wo, int yMask) {
		this.randValue = this.randValue * 3 + 1013904223;
		int val = this.randValue >> 2;
		return BlockPos4.from(
			xo + SectionPos.sectionRelative(val),
			yo + (val >> 8*2 & yMask),
			zo + SectionPos.sectionRelative(val >> 8),
			wo + SectionPos.sectionRelative(val >> 8*3)
		);
	}
}
