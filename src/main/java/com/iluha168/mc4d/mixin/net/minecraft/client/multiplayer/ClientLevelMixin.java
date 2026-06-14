package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import com.iluha168.mc4d.client.color.block.BlockTintCache4;
import com.iluha168.mc4d.client.multiplayer.ClientLevel4;
import com.iluha168.mc4d.client.renderer.LevelRenderer4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.LevelMixin;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
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

		for (int i = 0; i < 667; i++) {
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

		// TODO 4D particle engine
//		if (markerParticleTarget == state.getBlock()) {
//			this.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, state), x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
//		}
//
//		if (!state.isCollisionShapeFullBlock(this, pos)) {
//			for (AmbientParticle particle : this.environmentAttributes().getValue(EnvironmentAttributes.AMBIENT_PARTICLES, pos)) {
//				if (particle.canSpawn(this.random)) {
//					this.addParticle(
//						particle.particle(),
//						pos.getX() + this.random.nextDouble(),
//						pos.getY() + this.random.nextDouble(),
//						pos.getZ() + this.random.nextDouble(),
//						0.0,
//						0.0,
//						0.0
//					);
//				}
//			}
//		}
	}

	// TODO trySpawnDripParticles
	// TODO spawnParticle
	// TODO spawnFluidParticle
	// TODO playSeededSound
	// TODO playLocalSound

	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of 4D sound engine
	 */
	@Overwrite
	private void playSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed) {

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

	// TODO addParticle
	// TODO addParticle
	// TODO addAlwaysVisibleParticle
	// TODO addAlwaysVisibleParticle

	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of a 4D particle engine
	 */
	@Overwrite
	private void doAddParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShowParticles, double x, double y, double z, double xd, double yd, double zd) {

	}

	@Override
	public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ, int quartW) {
		return this.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
	}

	// TODO calculateBlockTint

	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of 4D particle engine
	 */
	@Redirect(method = "addDestroyBlockEffect", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	void addDestroyBlockEffect(VoxelShape shape, Shapes.DoubleLineConsumer consumer) {
	}

	// TODO addBreakingBlockEffect
	// TODO explode
}
