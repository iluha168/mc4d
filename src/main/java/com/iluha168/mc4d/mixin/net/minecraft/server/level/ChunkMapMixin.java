package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.network.protocol.game.ClientboundSetChunkCacheCenterPacket4;
import com.iluha168.mc4d.server.level.ChunkMap4;
import com.iluha168.mc4d.server.level.ChunkTrackingView4;
import com.iluha168.mc4d.server.level.ThreadedLevelLightEngine4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.util.StaticCache3D;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Mixin(ChunkMap.class)
class ChunkMapMixin implements ChunkMap4 {
	// TODO other methods

	@Overwrite
	@Deprecated
	public boolean isChunkTracked(ServerPlayer player, int chunkX, int chunkZ) {
		throw Err4.arguments2("ChunkMap4#isChunkTracked");
	}
	@Override
	public boolean isChunkTracked(ServerPlayer player, int chunkX, int chunkZ, int chunkW) {
		return ChunkTrackingView4.as(player.getChunkTrackingView()).contains(chunkX, chunkZ, chunkW)
			&& !player.connection.chunkSender.isPending(ChunkPos4.pack(chunkX, chunkZ, chunkW));
	}

	@Overwrite
	@Deprecated
	private boolean isChunkOnTrackedBorder(ServerPlayer player, int chunkX, int chunkZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private boolean isChunkOnTrackedBorder(ServerPlayer player, int chunkX, int chunkZ, int chunkW) {
		if (!this.isChunkTracked(player, chunkX, chunkZ, chunkW)) {
			return false;
		}
		for (int dx = -1; dx <= 1; dx++)
			for (int dz = -1; dz <= 1; dz++)
				for (int dw = -1; dw <= 1; dw++)
					if ((dx != 0 || dz != 0 || dw != 0) && !this.isChunkTracked(player, chunkX + dx, chunkZ + dz, chunkW + dw))
						return true;

		return false;
	}

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

	@ModifyExpressionValue(method = "applyChunkTrackingView", at = @At(
		value = "NEW",
		target = "(II)Lnet/minecraft/network/protocol/game/ClientboundSetChunkCacheCenterPacket;"
	))
	ClientboundSetChunkCacheCenterPacket applyChunkTrackingView(ClientboundSetChunkCacheCenterPacket original, @Local(name = "to") ChunkTrackingView.Positioned to) {
		((ClientboundSetChunkCacheCenterPacket4) original).setW(ChunkPos4.as(to.center()).w());
		return original;
	}

	// TODO other methods

	@Redirect(method = "getPlayers", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ChunkMap;isChunkOnTrackedBorder(Lnet/minecraft/server/level/ServerPlayer;II)Z"
	))
	boolean getPlayers_isChunkOnTrackedBorder(ChunkMap self, ServerPlayer player, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return this.isChunkOnTrackedBorder(player, x, z, ChunkPos4.as(pos).w());
	}
	@Redirect(method = "getPlayers", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ChunkMap;isChunkTracked(Lnet/minecraft/server/level/ServerPlayer;II)Z"
	))
	boolean getPlayers_isChunkTracked(ChunkMap self, ServerPlayer player, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return this.isChunkTracked(player, x, z, ChunkPos4.as(pos).w());
	}

	@Redirect(method = "lambda$waitForLightBeforeSending$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ThreadedLevelLightEngine;waitForPendingTasks(II)Ljava/util/concurrent/CompletableFuture;"
	))
	CompletableFuture<?> waitForLightBeforeSending(ThreadedLevelLightEngine lightEngine, int chunkX, int chunkZ, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		return ((ThreadedLevelLightEngine4) lightEngine).waitForPendingTasks(chunkX, chunkZ, ChunkPos4.as(chunkPos).w());
	}

	// TODO other methods

	@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
	static class TrackedEntityMixin {
		@Shadow
		@Final
		private Entity entity;

		@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
		@Expression("?.z * ?.z")
		@ModifyExpressionValue(method = "updatePlayer", at = @At("MIXINEXTRAS:EXPRESSION"))
		double updatePlayer_distanceSquared(double original, @Local(name = "deltaToPlayer") Vec3 deltaToPlayer) {
			final double w = ((Vec4) deltaToPlayer).w;
			return original + w * w;
		}
		@Redirect(method = "updatePlayer", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ChunkMap;isChunkTracked(Lnet/minecraft/server/level/ServerPlayer;II)Z"
		))
		boolean updatePlayer_isChunkTracked(ChunkMap instance, ServerPlayer player, int x, int z) {
			return ((ChunkMap4) instance).isChunkTracked(player, x, z, ChunkPos4.as(this.entity.chunkPosition()).w());
		}
	}
}
