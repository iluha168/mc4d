package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.iluha168.mc4d.world.level.lighting.LightEngine4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightEngine.class)
class LightEngineMixin implements LightEngine4 {
	@Shadow
	@Final
	private static int CACHE_SIZE;

	@Shadow
	@Final
	private long[] lastChunkPos;

	@Shadow
	@Final
	private LightChunk[] lastChunk;

	@Shadow
	@Final
	public LightChunkGetter chunkSource;

	@Redirect(method = "getState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/LightEngine;getChunk(II)Lnet/minecraft/world/level/chunk/LightChunk;"
	))
	LightChunk getState(LightEngine<?, ?> instance, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int chunkW = SectionPos.blockToSectionCoord(Vec4i.getW(pos));
		return ((LightEngine4) instance).getChunk(chunkX, chunkZ, chunkW);
	}

	@Overwrite
	@Deprecated
	protected @Nullable LightChunk getChunk(int chunkX, int chunkZ) {
		throw Err4.arguments2("LightEngine4#getChunk");
	}
	@Override
	public @Nullable LightChunk getChunk(int chunkX, int chunkZ, int chunkW) {
		long pos = ChunkPos4.pack(chunkX, chunkZ, chunkW);

		for (int i = 0; i < CACHE_SIZE; i++) {
			if (pos == this.lastChunkPos[i]) {
				return this.lastChunk[i];
			}
		}

		LightChunk chunk = ((ChunkSource4) this.chunkSource).getChunkForLighting(chunkX, chunkZ, chunkW);

		for (int ix = 1; ix > 0; ix--) {
			this.lastChunkPos[ix] = this.lastChunkPos[ix - 1];
			this.lastChunk[ix] = this.lastChunk[ix - 1];
		}

		this.lastChunkPos[0] = pos;
		this.lastChunk[0] = chunk;
		return chunk;
	}

	@Redirect(method = "retainData", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;getZeroNode(II)J"
	))
	long retainData(int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return SectionPos4.getZeroNode(x, z, ChunkPos4.as(pos).w());
	}

	@Redirect(method = "setLightEnabled", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;getZeroNode(II)J"
	))
	long setLightEnabled(int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return SectionPos4.getZeroNode(x, z, ChunkPos4.as(pos).w());
	}

	@Mixin(LightEngine.QueueEntry.class)
	static class QueueEntryMixin implements LightEngine4.QueueEntry {
		// Vanilla: 4
		@Unique private static final int FROM_LEVEL_BITS = 4;
		// Vanilla: 6
		@Unique private static final int DIRECTION_BITS = 4 * 2;
		// Vanilla: 1008L
		@Unique private static final long DIRECTIONS_MASK = ((1L << DIRECTION_BITS) - 1) << FROM_LEVEL_BITS;
		// Vanilla: 1024L
		@Unique private static final long FLAG_FROM_EMPTY_SHAPE = 1L << (FROM_LEVEL_BITS + DIRECTION_BITS);
		// Vanilla: 2048L
		@Unique private static final long FLAG_INCREASE_FROM_EMISSION = 1L << (FROM_LEVEL_BITS + DIRECTION_BITS + 1);

		@ModifyConstant(method = "decreaseSkipOneDirection", constant = @Constant(longValue = 1008L))
		private static long decreaseSkipOneDirection(long constant) {
			return DIRECTIONS_MASK;
		}

		@ModifyConstant(method = "decreaseAllDirections", constant = @Constant(longValue = 1008L))
		private static long decreaseAllDirections(long constant) {
			return DIRECTIONS_MASK;
		}

		@ModifyConstant(method = "increaseLightFromEmission", constant = @Constant(longValue = 1008L))
		private static long increaseLightFromEmission_mask(long constant) {
			return DIRECTIONS_MASK;
		}
		@ModifyConstant(method = "increaseLightFromEmission", constant = @Constant(longValue = 2048L))
		private static long increaseLightFromEmission_emission(long constant) {
			return FLAG_INCREASE_FROM_EMISSION;
		}
		@ModifyConstant(method = "increaseLightFromEmission", constant = @Constant(longValue = 1024L))
		private static long increaseLightFromEmission_emptyShape(long constant) {
			return FLAG_FROM_EMPTY_SHAPE;
		}

		@ModifyConstant(method = "increaseSkipOneDirection", constant = @Constant(longValue = 1008L))
		private static long increaseSkipOneDirection_mask(long constant) {
			return DIRECTIONS_MASK;
		}
		@ModifyConstant(method = "increaseSkipOneDirection", constant = @Constant(longValue = 1024L))
		private static long increaseSkipOneDirection_emptyShape(long constant) {
			return FLAG_FROM_EMPTY_SHAPE;
		}

		@ModifyConstant(method = "increaseOnlyOneDirection", constant = @Constant(longValue = 1024L))
		private static long increaseOnlyOneDirection(long constant) {
			return FLAG_FROM_EMPTY_SHAPE;
		}

		@Overwrite
		@Deprecated
		public static long increaseSkySourceInDirections(boolean down, boolean north, boolean south, boolean west, boolean east) {
			throw Err4.arguments3("LightEngine4.QueueEntry#increaseSkySourceInDirections");
		}

		@ModifyConstant(method = "isFromEmptyShape", constant = @Constant(longValue = 1024L))
		private static long isFromEmptyShape(long constant) {
			return FLAG_FROM_EMPTY_SHAPE;
		}

		@ModifyConstant(method = "isIncreaseFromEmission", constant = @Constant(longValue = 2048L))
		private static long isIncreaseFromEmission(long constant) {
			return FLAG_INCREASE_FROM_EMISSION;
		}
	}
}
