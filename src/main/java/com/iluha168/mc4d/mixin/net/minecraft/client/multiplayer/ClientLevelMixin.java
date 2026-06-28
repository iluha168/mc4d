package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import com.iluha168.mc4d.client.color.block.BlockTintCache4;
import com.iluha168.mc4d.client.multiplayer.ClientLevel4;
import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleEngine4;
import com.iluha168.mc4d.client.particle.TerrainParticle4;
import com.iluha168.mc4d.client.renderer.LevelRenderer4;
import com.iluha168.mc4d.client.resources.sounds.SimpleSoundInstance4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Cursor4D;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.LevelMixin;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.ColorResolver4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
abstract
class ClientLevelMixin extends LevelMixin implements ClientLevel4 {
	@Shadow
	@Final
	private LevelRenderer levelRenderer;

	@Shadow
	protected abstract void trySpawnDripParticles(BlockPos pos, BlockState state, ParticleOptions dripParticle, boolean isTopSolid);

	@Shadow
	@Nullable
	protected abstract Block getMarkerParticleTarget();

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	protected abstract ParticleStatus calculateParticleLevel(boolean alwaysShowParticles);

	@Redirect(method = "syncBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;absSnapTo(DDD)V"
	))
	void syncBlockState(Player player, double x, double y, double z, @Local(argsOnly = true, name = "playerPos") Vec3 playerPos) {
		((Entity4) player).absSnapTo(x, y, z, ((Vec4) playerPos).w);
	}

	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos init(BlockPos respawnPos) {
		Vec4i.setW(respawnPos, respawnPos.getZ());
		return respawnPos;
	}

	@Redirect(method = "lambda$onChunkLoaded$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/color/block/BlockTintCache;invalidateForChunk(II)V"
	))
	private static void onChunkLoaded(BlockTintCache instance, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		((BlockTintCache4) instance).invalidateForChunk(chunkX, chunkZ, ChunkPos4.as(pos).w());
	}

	@Overwrite
	@Deprecated
	public boolean hasChunk(int chunkX, int chunkZ) {
		throw Err4.arguments2("LevelReader4#hasChunk");
	}
	@Override
	public boolean hasChunk(int chunkX, int chunkZ, int chunkW) {
		return true;
	}

	@Overwrite
	@Deprecated
	public void animateTick(int xt, int yt, int zt) {
		throw Err4.arguments3("ClientLevel4#animateTick");
	}
	@Override
	public void animateTick(int xt, int yt, int zt, int wt) {
		RandomSource animateRandom = RandomSource.createThreadLocalInstance();
		Block markerParticleTarget = this.getMarkerParticleTarget();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int i = 0; i < 888; i++) {
			this.doAnimateTick(xt, yt, zt, wt, 16, animateRandom, markerParticleTarget, pos);
			this.doAnimateTick(xt, yt, zt, wt, 32, animateRandom, markerParticleTarget, pos);
		}
	}

	@Overwrite
	@Deprecated
	public void doAnimateTick(int xt, int yt, int zt, int r, RandomSource animateRandom, @Nullable Block markerParticleTarget, BlockPos.MutableBlockPos pos) {
		throw Err4.arguments3("ClientLevel4#doAnimateTick");
	}
	@Override
	public void doAnimateTick(int xt, int yt, int zt, int wt, int r, RandomSource animateRandom, @Nullable Block markerParticleTarget, BlockPos.MutableBlockPos pos) {
		int x = xt + this.random.nextInt(r) - this.random.nextInt(r);
		int y = yt + this.random.nextInt(r) - this.random.nextInt(r);
		int z = zt + this.random.nextInt(r) - this.random.nextInt(r);
		int w = wt + this.random.nextInt(r) - this.random.nextInt(r);
		((BlockPos4.MutableBlockPos) pos).set(x, y, z, w);
		BlockState state = this.getBlockState(pos);
		Level This = (Level) (Object) this;
		state.getBlock().animateTick(state, This, pos, animateRandom);
		FluidState fluidState = this.getFluidState(pos);
		if (!fluidState.isEmpty()) {
			fluidState.animateTick(This, pos, animateRandom);
			ParticleOptions dripParticle = fluidState.getDripParticle();
			if (dripParticle != null && this.random.nextInt(10) == 0) {
				boolean hasWatertightBottom = state.isFaceSturdy(This, pos, Direction.DOWN);
				BlockPos below = pos.below();
				this.trySpawnDripParticles(below, this.getBlockState(below), dripParticle, hasWatertightBottom);
			}
		}

		if (markerParticleTarget == state.getBlock()) {
			this.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, state), x + 0.5, y + 0.5, z + 0.5, w + 0.5, 0.0, 0.0, 0.0, 0.0);
		}

		if (!state.isCollisionShapeFullBlock(This, pos)) {
			for (AmbientParticle particle : This.environmentAttributes().getValue(EnvironmentAttributes.AMBIENT_PARTICLES, pos)) {
				if (particle.canSpawn(this.random)) {
					this.addParticle(
						particle.particle(),
						pos.getX() + this.random.nextDouble(),
						pos.getY() + this.random.nextDouble(),
						pos.getZ() + this.random.nextDouble(),
						Vec4i.getW(pos) + this.random.nextDouble(),
						0.0,
						0.0,
						0.0,
						0.0
					);
				}
			}
		}
	}

	// TODO trySpawnDripParticles
	// TODO spawnParticle
	// TODO spawnFluidParticle

	@Overwrite
	@Deprecated
	public void playSeededSound(@Nullable Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
		throw Err4.arguments3("Level4#playSeededSound");
	}
	@Override
	public void playSeededSound(
		@Nullable Entity except, double x, double y, double z, double w, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed
	) {
		// TODO net.neoforged.neoforge.event.EventHooks.onPlaySoundAtPosition
		if (except == this.minecraft.player) {
			this.playSound(x, y, z, w, sound.value(), source, volume, pitch, false, seed);
		}
	}

	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	public void playLocalSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		throw Err4.arguments3("Level4#playLocalSound");
	}
	@Override
	public void playLocalSound(double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		this.playSound(x, y, z, w, sound, source, volume, pitch, distanceDelay, this.random.nextLong());
	}

	@Overwrite
	@Deprecated
	private void playSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed) {
		throw Err4.arguments3(null);
	}
	@Unique
	private void playSound(double x, double y, double z, double w, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed) {
		double distanceToSqr = ((Vec4) this.minecraft.gameRenderer.getMainCamera().position()).distanceToSqr(x, y, z, w);
		SimpleSoundInstance instance = SimpleSoundInstance4.from(sound, source, volume, pitch, RandomSource.create(seed), x, y, z, w);
		if (distanceDelay && distanceToSqr > 100.0) {
			double delayInSeconds = Math.sqrt(distanceToSqr) / 40.0;
			this.minecraft.getSoundManager().playDelayed(instance, (int)(delayInSeconds * 20.0));
		} else {
			this.minecraft.getSoundManager().play(instance);
		}
	}

	// TODO createFireworks

	@Overwrite
	@Deprecated
	public void setSectionDirtyWithNeighbors(int chunkX, int chunkY, int chunkZ) {
		throw Err4.arguments3("ClientLevel4#setSectionDirtyWithNeighbors");
	}
	@Override
	public void setSectionDirtyWithNeighbors(int chunkX, int chunkY, int chunkZ, int chunkW) {
		((LevelRenderer4) this.levelRenderer).setSectionDirtyWithNeighbors(chunkX, chunkY, chunkZ, chunkW);
	}

	@Overwrite
	@Deprecated
	public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int maxSectionX, int maxSectionY, int maxSectionZ) {
		throw Err4.arguments3("ClientLevel4#setSectionRangeDirty");
	}
	@Override
	public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int minSectionW, int maxSectionX, int maxSectionY, int maxSectionZ, int maxSectionW) {
		((LevelRenderer4) this.levelRenderer).setSectionRangeDirty(minSectionX, minSectionY, minSectionZ, minSectionW, maxSectionX, maxSectionY, maxSectionZ, maxSectionW);
	}

	@SuppressWarnings({"deprecation", "RedundantMethodOverride"})
	@Overwrite
	@Deprecated
	public void addParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3("LevelAccessor4#addParticle");
	}
	@Override
	public void addParticle(ParticleOptions particle, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {
		this.doAddParticle(particle, particle.getType().getOverrideLimiter(), false, x, y, z, w, xd, yd, zd, wd);
	}

	@SuppressWarnings({"deprecation", "RedundantMethodOverride"})
	@Overwrite
	@Deprecated
	public void addParticle(
		ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double xd, double yd, double zd
	) {
		throw Err4.arguments3("Level4#addParticle");
	}
	@Override
	public void addParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {
		this.doAddParticle(particle, particle.getType().getOverrideLimiter() || overrideLimiter, alwaysShow, x, y, z, w, xd, yd, zd, wd);
	}

	@SuppressWarnings({"deprecation", "RedundantMethodOverride"})
	@Overwrite
	@Deprecated
	public void addAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3("Level4#addAlwaysVisibleParticle");
	}
	@Override
	public void addAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {
		this.doAddParticle(particle, false, true, x, y, z, w, xd, yd, zd, wd);
	}

	@SuppressWarnings({"deprecation", "RedundantMethodOverride"})
	@Overwrite
	@Deprecated
	public void addAlwaysVisibleParticle(ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3("Level4#addAlwaysVisibleParticle");
	}
	@Override
	public void addAlwaysVisibleParticle(ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double w, double xd, double yd, double zd, double wd) {
		this.doAddParticle(particle, particle.getType().getOverrideLimiter() || overrideLimiter, true, x, y, z, w, xd, yd, zd, wd);
	}

	@Overwrite
	@Deprecated
	private void doAddParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShowParticles, double x, double y, double z, double xd, double yd, double zd) {
		throw Err4.arguments3(null);
	}
	@Unique
	private void doAddParticle(
		ParticleOptions particle, boolean overrideLimiter, boolean alwaysShowParticles,
		double x, double y, double z, double w,
		double xd, double yd, double zd, double wd
	) {
		try {
			Camera camera = this.minecraft.gameRenderer.getMainCamera();
			ParticleStatus particleLevel = this.calculateParticleLevel(alwaysShowParticles);
			if (overrideLimiter) {
				((ParticleEngine4) this.minecraft.particleEngine).createParticle(particle, x, y, z, w, xd, yd, zd, wd);
			} else if (!(((Vec4) camera.position()).distanceToSqr(x, y, z, w) > 1024.0)) {
				if (particleLevel != ParticleStatus.MINIMAL) {
					((ParticleEngine4) this.minecraft.particleEngine).createParticle(particle, x, y, z, w, xd, yd, zd, wd);
				}
			}
		} catch (Throwable var19) {
			CrashReport report = CrashReport.forThrowable(var19, "Exception while adding particle");
			CrashReportCategory category = report.addCategory("Particle being added");
			//noinspection DataFlowIssue
			category.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(particle.getType()));
			category.setDetail(
				"Parameters", () -> ParticleTypes.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), particle).toString()
			);
			category.setDetail("Position", () -> CrashReportCategory.formatLocation((Level) (Object) this, x, y, z)); // TODO formatLocation 4D
			throw new ReportedException(report);
		}
	}

	@Override
	public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ, int quartW) {
		return this.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
	}

	@Redirect(method = "calculateBlockTint", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/level/biome/Biome;DD)I",
		ordinal = 0
	))
	int calculateBlockTint_dist0(ColorResolver resolver, Biome biome, double x, double z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((ColorResolver4) resolver).getColor(biome, x, z, Vec4i.getW(pos));
	}
	@ModifyVariable(method = "calculateBlockTint", at = @At("STORE"), name = "count")
	int calculateBlockTint_count(int count, @Local(name = "dist") int dist) {
		return count * (dist * 2 + 1);
	}
	@Redirect(method = "calculateBlockTint", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/core/Cursor3D;"
	))
	Cursor3D calculateBlockTint_cursor(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @Local(argsOnly = true, name = "pos") BlockPos pos, @Local(name = "dist") int dist) {
		final int posW = Vec4i.getW(pos);
		return new Cursor4D(minX, minY, minZ, posW - dist, maxX, maxY, maxZ, posW + dist);
	}
	@Redirect(method = "calculateBlockTint", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos calculateBlockTint_set(BlockPos.MutableBlockPos nextPos, int x, int y, int z, @Local(name = "cursor") Cursor3D cursor) {
		return ((BlockPos4.MutableBlockPos) nextPos).set(x, y, z, ((Cursor4D) cursor).nextW());
	}
	@Redirect(method = "calculateBlockTint", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/level/biome/Biome;DD)I",
		ordinal = 1
	))
	int calculateBlockTint_colorBlended(ColorResolver resolver, Biome biome, double x, double z, @Local(name = "nextPos") BlockPos.MutableBlockPos nextPos) {
		return ((ColorResolver4) resolver).getColor(biome, x, z, Vec4i.getW(nextPos));
	}

	@Redirect(method = "addDestroyBlockEffect", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	void addDestroyBlockEffect(
		VoxelShape shape, Shapes.DoubleLineConsumer consumer,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "blockState") BlockState blockState
	) {
		ClientLevel This = (ClientLevel) (Object) this;
		((VoxelShape4) shape).forAllBoxes((x1, y1, z1, w1, x2, y2, z2, w2) -> {
			double widthX = Math.min(1.0, x2 - x1);
			double widthY = Math.min(1.0, y2 - y1);
			double widthZ = Math.min(1.0, z2 - z1);
			double widthW = Math.min(1.0, w2 - w1);
			int countX = Math.max(2, Mth.ceil(widthX / 0.25));
			int countY = Math.max(2, Mth.ceil(widthY / 0.25));
			int countZ = Math.max(2, Mth.ceil(widthZ / 0.25));
			int countW = Math.max(2, Mth.ceil(widthW / 0.25));

			for (int xx = 0; xx < countX; xx++)
				for (int yy = 0; yy < countY; yy++)
					for (int zz = 0; zz < countZ; zz++)
						for (int ww = 0; ww < countW; ww++) {
							double relX = (xx + 0.5) / countX;
							double relY = (yy + 0.5) / countY;
							double relZ = (zz + 0.5) / countZ;
							double relW = (ww + 0.5) / countW;
							double x = relX * widthX + x1;
							double y = relY * widthY + y1;
							double z = relZ * widthZ + z1;
							double w = relW * widthW + w1;
							this.minecraft.particleEngine.add(
								TerrainParticle4.from(
									This,
									pos.getX() + x,
									pos.getY() + y,
									pos.getZ() + z,
									Vec4i.getW(pos) + w,
									relX - 0.5,
									relY - 0.5,
									relZ - 0.5,
									relW - 0.5,
									blockState, pos
								).updateSprite(blockState, pos)
							);
						}
		});
	}

	@ModifyExpressionValue(method = "addBreakingBlockEffect(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/phys/HitResult;)V", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/client/particle/TerrainParticle;"
	))
	TerrainParticle addBreakingBlockEffect(
		TerrainParticle original,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "direction") Direction direction,
		@Local(name = "shape") AABB shape
	) {
		if (!(shape instanceof AABB4 shape4)) throw Err4.container3();
		final int w = Vec4i.getW(pos);
		double wp = w + this.random.nextDouble() * (shape4.maxW - shape4.minW - 0.2F) + 0.1F + shape4.minW;

		if (direction == Direction4.KATA) {
			wp = w + shape4.minW - 0.1F;
		}

		if (direction == Direction4.ANA) {
			wp = w + shape4.maxW + 0.1F;
		}

		((Particle4) original).init_finish(wp, 0.0);
		return original;
	}

	// TODO explode
}
