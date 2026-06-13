package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.level.levelgen.Heightmap4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(FlatLevelSource.class)
class FlatLevelSourceMixin {
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(0)")
	@Inject(method = "fillFromNoise", at = @At("MIXINEXTRAS:EXPRESSION"))
	void fillFromNoise_w(
		Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess centerChunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir,
		@Share("w") LocalIntRef w
	) {
		w.set(0); // Do not really have to do that but whatever
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "fillFromNoise", at = @At("MIXINEXTRAS:EXPRESSION"))
	int fillFromNoise_incrementW(int one, @Share("w") LocalIntRef w) {
		w.set(w.get() + 1);
		if (w.get() < LevelChunkSection.SECTION_WIDTH) return 0;
		w.set(0);
		return 1;
	}
	@Redirect(method = "fillFromNoise", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos fillFromNoise_set(BlockPos.MutableBlockPos blockPos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) blockPos).set(x, y, z, w.get());
	}
	@Redirect(method = "fillFromNoise", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z"
	))
	boolean fillFromNoise_heightmaps(Heightmap heightmap, int localX, int localY, int localZ, BlockState state, @Share("w") LocalIntRef w) {
		return ((Heightmap4) heightmap).update(localX, localY, localZ, w.get(), state);
	}

	// TODO the rest
}
