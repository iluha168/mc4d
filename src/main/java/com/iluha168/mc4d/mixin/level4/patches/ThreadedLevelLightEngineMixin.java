package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.server.level.ThreadedLevelLightEngine4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskDispatcher;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;

@Mixin(ThreadedLevelLightEngine.class)
abstract
class ThreadedLevelLightEngineMixin implements ThreadedLevelLightEngine4 {
	@Shadow
	@Final
	private ChunkTaskDispatcher taskDispatcher;

	@Shadow
	@Final
	private ObjectList<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> lightTasks;

	@Shadow
	protected abstract void runUpdate();

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "checkBlock", at = @At("MIXINEXTRAS:EXPRESSION"))
	void checkBlock(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) BlockPos pos
	) {
		this.addTask(chunkX, chunkZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)), type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILjava/util/function/IntSupplier;Lnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?, ?)")
	@Redirect(method = "updateChunkStatus", at = @At("MIXINEXTRAS:EXPRESSION"))
	void updateChunkStatus(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), level, type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILjava/util/function/IntSupplier;Lnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?, ?)")
	@Redirect(method = "updateSectionStatus", at = @At("MIXINEXTRAS:EXPRESSION"))
	void updateSectionStatus(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) SectionPos pos
	) {
		this.addTask(chunkX, chunkZ, ((SectionPos4) pos).w(), level, type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	void propagateLightSources(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "setLightEnabled", at = @At("MIXINEXTRAS:EXPRESSION"))
	void setLightEnabled(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILjava/util/function/IntSupplier;Lnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?, ?)")
	@Redirect(method = "queueSectionData", at = @At("MIXINEXTRAS:EXPRESSION"))
	void queueSectionData(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) SectionPos pos
	) {
		this.addTask(chunkX, chunkZ, ((SectionPos4) pos).w(), level, type, runnable);
	}

	@Shadow
	@Final
	private ChunkMap chunkMap;

	@Overwrite
	@Deprecated
	private void addTask(int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void addTask(int chunkX, int chunkZ, int chunkW, ThreadedLevelLightEngine.TaskType type, Runnable runnable) {
		this.addTask(chunkX, chunkZ, chunkW, this.chunkMap.getChunkQueueLevel(ChunkPos4.pack(chunkX, chunkZ, chunkW)), type, runnable);
	}

	@Overwrite
	@Deprecated
	private void addTask(int chunkX, int chunkZ, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void addTask(int chunkX, int chunkZ, int chunkW, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable) {
		this.taskDispatcher.submit(() -> {
			this.lightTasks.add(Pair.of(type, runnable));
			if (this.lightTasks.size() >= ThreadedLevelLightEngine.DEFAULT_BATCH_SIZE) {
				this.runUpdate();
			}
		}, ChunkPos4.pack(chunkX, chunkZ, chunkW), level);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILjava/util/function/IntSupplier;Lnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?, ?)")
	@Redirect(method = "retainData", at = @At("MIXINEXTRAS:EXPRESSION"))
	void retainData(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), level, type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "initializeLight", at = @At("MIXINEXTRAS:EXPRESSION"))
	void initializeLight(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos") ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "lambda$initializeLight$3", at = @At("MIXINEXTRAS:EXPRESSION"))
	void initializeLight$3(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "lightChunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	void lightChunk(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos") ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), type, runnable);
	}

	@Definition(id = "addTask", method = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V")
	@Expression("this.addTask(?, ?, ?, ?)")
	@Redirect(method = "lambda$lightChunk$3", at = @At("MIXINEXTRAS:EXPRESSION"))
	void lightChunk$3(
		ThreadedLevelLightEngine This, int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable,
		@Local(name = "pos", argsOnly = true) ChunkPos pos
	) {
		this.addTask(chunkX, chunkZ, ChunkPos4.as(pos).w(), type, runnable);
	}

	@Overwrite
	@Deprecated
	public CompletableFuture<?> waitForPendingTasks(int chunkX, int chunkZ) {
		throw Err4.arguments2(null);
	}
	@Override
	public CompletableFuture<?> waitForPendingTasks(int chunkX, int chunkZ, int chunkW) {
		return CompletableFuture.runAsync(() -> {}, r -> this.addTask(chunkX, chunkZ, chunkW, ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
	}
}