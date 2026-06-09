package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.util.StaticCache3D;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.server.level.GeneratingChunkMap;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerationTask.class)
public class ChunkGenerationTaskMixin {
	@Shadow
	@Final
	private ChunkPos pos;

	@Redirect(method = "create", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;create(IIILnet/minecraft/util/StaticCache2D$Initializer;)Lnet/minecraft/util/StaticCache2D;"
	))
	private static StaticCache2D<GenerationChunkHolder> create(
		int centerX, int centerZ, int range, StaticCache2D.Initializer<GenerationChunkHolder> initializer,
		@Local(argsOnly = true, name = "chunkMap") GeneratingChunkMap chunkMap,
		@Local(argsOnly = true, name = "pos") ChunkPos pos
	) {
		return StaticCache3D.create(
			centerX, centerZ, ChunkPos4.as(pos).w(), range,
			(x, z, w) -> chunkMap.acquireGeneration(ChunkPos4.pack(x, z, w))
		);
	}

	@Redirect(method = "releaseClaim", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;"
	))
	<T> T releaseClaim(StaticCache2D<T> cache, int x, int z) {
		return ((StaticCache3D<T>) cache).get(x, z, ChunkPos4.as(this.pos).w());
	}

	@Redirect(method = "canLoadWithoutGeneration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;",
		ordinal = 0
	))
	<T> T canLoadWithoutGeneration_highestGeneratedStatus(StaticCache2D<T> cache, int x, int z) {
		return ((StaticCache3D<T>) cache).get(x, z, ChunkPos4.as(this.pos).w());
	}

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "range", local = @Local(type = int.class, name = "range"))
	@Expression("z = @(? - range)")
	@Inject(method = "canLoadWithoutGeneration", at = @At("MIXINEXTRAS:EXPRESSION"))
	void canLoadWithoutGeneration_initW(
		CallbackInfoReturnable<Boolean> cir,
		@Share("w") LocalIntRef w,
		@Local(name = "range") int range
	) {
		w.set(ChunkPos4.as(this.pos).w() - range);
	}

	// This does apply properly, IDE is lying
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "canLoadWithoutGeneration", at = @At("MIXINEXTRAS:EXPRESSION"))
	int canLoadWithoutGeneration_incW(
		int one,
		@Share("w") LocalIntRef w,
		@Local(name = "range") int range
	) {
		w.set(w.get() + 1);
		final int posW = ChunkPos4.as(this.pos).w();
		if (w.get() <= posW + range) return 0;
		w.set(posW - range);
		return 1;
	}

	@Redirect(method = "canLoadWithoutGeneration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;getChessboardDistance(II)I"
	))
	int canLoadWithoutGeneration_getChessboardDistance(ChunkPos instance, int x, int z, @Share("w") LocalIntRef w) {
		return ChunkPos4.as(instance).getChessboardDistance(x, z, w.get());
	}

	@Redirect(method = "canLoadWithoutGeneration", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;",
		ordinal = 1
	))
	<T> T canLoadWithoutGeneration_persistedStatus(StaticCache2D<T> cache, int x, int z, @Share("w") LocalIntRef w) {
		return ((StaticCache3D<T>) cache).get(x, z, w.get());
	}

	@Redirect(method = "getCenter", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;"
	))
	<T> T getCenter(StaticCache2D<T> cache, int x, int z) {
		return ((StaticCache3D<T>) cache).get(x, z, ChunkPos4.as(this.pos).w());
	}

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "radius", local = @Local(type = int.class, name = "radius"))
	@Expression("z = @(? - radius)")
	@Inject(method = "scheduleLayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	void scheduleLayer_initW(
		ChunkStatus status, boolean needsGeneration, CallbackInfo ci,
		@Share("w") LocalIntRef w,
		@Local(name = "radius") int radius
	) {
		w.set(ChunkPos4.as(this.pos).w() - radius);
	}

	// This does apply properly, IDE is lying
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "scheduleLayer", at = @At("MIXINEXTRAS:EXPRESSION"))
	int scheduleLayer_incW(
		int one,
		@Share("w") LocalIntRef w,
		@Local(name = "radius") int radius
	) {
		w.set(w.get() + 1);
		final int posW = ChunkPos4.as(this.pos).w();
		if (w.get() <= posW + radius) return 0;
		w.set(posW - radius);
		return 1;
	}

	@Redirect(method = "scheduleLayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;"
	))
	<T> T scheduleLayer_get(StaticCache2D<T> cache, int x, int z, @Share("w") LocalIntRef w) {
		return ((StaticCache3D<T>) cache).get(x, z, w.get());
	}
}
