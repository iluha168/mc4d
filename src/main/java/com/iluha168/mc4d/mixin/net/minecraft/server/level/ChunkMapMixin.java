package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.server.level.ThreadedLevelLightEngine4;
import com.iluha168.mc4d.util.StaticCache3D;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Mixin(ChunkMap.class)
class ChunkMapMixin {
	// TODO other methods

	@Redirect(method = "getChunkRangeFuture", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;square(I)I"
	))
	int getChunkRangeFuture_chunkCount(int x) {
		return MathHelpers.cube(x);
	}

	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Definition(id = "range", local = @Local(type = int.class, name = "range", argsOnly = true))
	@Expression("x = @(-range)")
	@ModifyExpressionValue(method = "getChunkRangeFuture", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getChunkRangeFuture_setW(int negRange, @Share("w") LocalIntRef w){
		w.set(negRange);
		return negRange;
	}

	// This does apply properly, IDE is lying
	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x = x + @(1)")
	@ModifyExpressionValue(method = "getChunkRangeFuture", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getChunkRangeFuture_incW(int one, @Share("w") LocalIntRef w, @Local(argsOnly = true, name = "range") int range) {
		w.set(w.get() + 1);
		if (w.get() <= range) return 0;
		w.set(-range);
		return 1;
	}

	@Definition(id = "distance", local = @Local(type = int.class, name = "distance"))
	@Definition(id = "max", method = "Ljava/lang/Math;max(II)I")
	@Expression("distance = @(max(?, ?))")
	@ModifyExpressionValue(method = "getChunkRangeFuture", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getChunkRangeFuture_distance(int distXZ, @Share("w") LocalIntRef w) {
		return Math.max(distXZ, Math.abs(w.get()));
	}

	@Redirect(method = "getChunkRangeFuture", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long getChunkRangeFuture_pack(int x, int z, @Share("w") LocalIntRef w, @Local(name = "centerPos") ChunkPos centerPos) {
		return ChunkPos4.pack(x, z, ChunkPos4.as(centerPos).w() + w.get());
	}

	@Redirect(method = "applyStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;"
	))
	<T> T applyStep_holder(StaticCache2D<T> cache, int x, int z, @Local(name = "pos") ChunkPos pos) {
		return ((StaticCache3D<T>) cache).get(x, z, ChunkPos4.as(pos).w());
	}
	@ModifyArgs(method = "applyStep", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
	))
	void applyStep_reportLocation(Args args, @Local(name = "pos") ChunkPos pos) {
		assert args.get(0) == Locale.ROOT;
		args.set(1, args.get(1) + ",%d");
		args.set(2, ArrayUtils.addAll((Object[]) args.get(2), ChunkPos4.as(pos).w()));
	}
	@Redirect(method = "applyStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long applyStep_reportPositionHash(int x, int z, @Local(name = "pos") ChunkPos pos) {
		return ChunkPos4.pack(x, z, ChunkPos4.as(pos).w());
	}

	// TODO other methods

	@Redirect(method = "lambda$waitForLightBeforeSending$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;waitForPendingTasks(II)Ljava/util/concurrent/CompletableFuture;"
	))
	CompletableFuture<?> waitForLightBeforeSending(ThreadedLevelLightEngine lightEngine, int chunkX, int chunkZ, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		return ((ThreadedLevelLightEngine4) lightEngine).waitForPendingTasks(chunkX, chunkZ, ChunkPos4.as(chunkPos).w());
	}

	// TODO other methods
}
