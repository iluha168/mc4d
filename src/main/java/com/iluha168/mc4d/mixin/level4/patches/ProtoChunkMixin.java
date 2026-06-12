package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.level.levelgen.Heightmap4;
import com.iluha168.mc4d.world.level.lighting.ChunkSkyLightSources4;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProtoChunk.class)
abstract class ProtoChunkMixin implements ChunkAccess4 {
	@Redirect(method = "getBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState getBlockState(LevelChunkSection section, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int sectionW = SectionPos.sectionRelative(Vec4i.getW(pos));
		return ((LevelChunkSection4) section).getBlockState(sectionX, sectionY, sectionZ, sectionW);
	}

	@Redirect(method = "getFluidState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getFluidState(III)Lnet/minecraft/world/level/material/FluidState;"
	))
	FluidState getFluidState(LevelChunkSection section, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int sectionW = SectionPos.sectionRelative(Vec4i.getW(pos));
		return ((LevelChunkSection4) section).getFluidState(sectionX, sectionY, sectionZ, sectionW);
	}

	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState setBlockState_setBlockState(
		LevelChunkSection section, int sectionX, int sectionY, int sectionZ, BlockState state,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Share("localW") LocalIntRef localW
	) {
		localW.set(SectionPos.sectionRelative(Vec4i.getW(pos)));
		return ((LevelChunkSection4) section).setBlockState(sectionX, sectionY, sectionZ, localW.get(), state);
	}
	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;update(Lnet/minecraft/world/level/BlockGetter;III)Z"
	))
	boolean setBlockState_skyLightSources(ChunkSkyLightSources skyLightSources, BlockGetter level, int x, int y, int z, @Share("localW") LocalIntRef localW) {
		return ((ChunkSkyLightSources4) skyLightSources).update(level, x, y, z, localW.get());
	}
	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z"
	))
	boolean setBlockState_heightmaps(Heightmap heightmap, int localX, int localY, int localZ, BlockState state, @Share("localW") LocalIntRef localW) {
		return ((Heightmap4) heightmap).update(localX, localY, localZ, localW.get(), state);
	}

	// TODO getNoiseBiome

	@Expression("return (short) @(?)")
	@ModifyExpressionValue(method = "packOffsetCoordinates", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int packOffsetCoordinates(int packedXYZ, @Local(argsOnly = true, name = "blockPos") BlockPos blockPos) {
		final int dw = SectionPos.sectionRelative(Vec4i.getW(blockPos));
		return packedXYZ | dw << 12;
	}

	@WrapMethod(method = "unpackOffsetCoordinates")
	private static BlockPos unpackOffsetCoordinates(short packedCoord, int sectionY, ChunkPos chunkPos, Operation<BlockPos> original) {
		BlockPos pos = original.call(packedCoord, sectionY, chunkPos);
		Vec4i.setW(pos, SectionPos.sectionToBlockCoord(
			ChunkPos4.as(chunkPos).w(),
			SectionPos.sectionRelative(packedCoord >>> 12)
		));
		return pos;
	}
}
