package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
class LevelMixin implements Level4, LevelReader4 {
	@Shadow
	protected int randValue;

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

	@Override
	public boolean hasChunk(int chunkX, int chunkZ, int chunkW) {
		return ((LevelAccessor) this).getChunkSource().hasChunk(chunkX, chunkZ); // TODO chunkW
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
	// TODO playSeededSound
	// TODO playSeededSound
	// TODO playSound
	// TODO playSound
	// TODO playSound
	// TODO playLocalSound
	// TODO playLocalSound
	// TODO addParticle
	// TODO addParticle
	// TODO addAlwaysVisibleParticle
	// TODO addAlwaysVisibleParticle
	// TODO explode
	// TODO explode
	// TODO explode
	// TODO explode
	// TODO isLoaded

	@Definition(id = "getChunk", method = "Lnet/minecraft/world/level/Level;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;")
	@Expression("this.getChunk(?, ?, ?, ?)")
	@Redirect(method = "loadedAndEntityCanStandOnFace", at = @At("MIXINEXTRAS:EXPRESSION"))
	ChunkAccess loadedAndEntityCanStandOnFace(Level This, int chunkX, int chunkZ, ChunkStatus status, boolean loadOrGenerate, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getChunk(chunkX, chunkZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)), status, loadOrGenerate);
	}

	// TODO getWorldBorderAdjustedRespawnData

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
