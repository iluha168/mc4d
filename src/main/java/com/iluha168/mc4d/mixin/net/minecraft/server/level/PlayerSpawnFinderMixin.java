package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(PlayerSpawnFinder.class)
abstract
class PlayerSpawnFinderMixin {
	@Shadow
	@Final
	private CompletableFuture<Vec3> finishedFuture;

	@Shadow
	@Final
	private ServerLevel level;

	@Shadow
	protected abstract void scheduleNext();

	@Shadow
	@Final
	private BlockPos spawnSuggestion;

	@Shadow
	@Final
	private int radius;

	@Shadow
	@Final
	private int candidateCount;

	@Shadow
	@Final
	private int coprime;

	@Shadow
	@Final
	private int offset;

	@Shadow
	private static boolean noCollisionNoLiquid(CollisionGetter level, BlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;min(JJ)J"
	))
	long setCandidateCount(long a, long b, @Local(name = "squareSide") long squareSide) {
		return Math.min(32768L, b * squareSide);
	}

	// TODO findSpawn when 4D world border

	@Definition(id = "deltaZ", local = @Local(type = int.class, name = "deltaZ"))
	@Expression("deltaZ = @(?)")
	@ModifyExpressionValue(method = "scheduleNext", at = @At("MIXINEXTRAS:EXPRESSION"))
	int scheduleNext_deltaW(int deltaZW, @Share("deltaW") LocalIntRef deltaW) {
		deltaW.set(deltaZW % (this.radius * 2 + 1));
		return deltaZW / (this.radius * 2 + 1);
	}

	@Definition(id = "scheduleCandidate", method = "Lnet/minecraft/server/level/PlayerSpawnFinder;scheduleCandidate(IIILjava/util/function/Supplier;)V")
	@Definition(id = "targetX", local = @Local(type = int.class, name = "targetX"))
	@Expression("this.scheduleCandidate(targetX, ?, ?, ?)")
	@Redirect(method = "scheduleNext", at = @At("MIXINEXTRAS:EXPRESSION"))
	void scheduleNext_scheduleCandidate(
		PlayerSpawnFinder This, int candidateX, int candidateZ, int candidateIndex, Supplier<Optional<Vec3>> candidateChecker,
		@Share("deltaW") LocalIntRef deltaW
	) {
		final int targetW = Vec4i.getW(this.spawnSuggestion) + deltaW.get() - this.radius;
		this.scheduleCandidate(candidateX, candidateZ, targetW, candidateIndex, () -> {
			BlockPos spawnPos = getOverworldRespawnPos(this.level, candidateX, candidateZ, targetW);
			//noinspection ConstantValue
			return spawnPos != null && noCollisionNoLiquid(this.level, spawnPos)
				? Optional.of(Vec3.atBottomCenterOf(spawnPos))
				: Optional.empty();
		});
	}

	@Definition(id = "scheduleCandidate", method = "Lnet/minecraft/server/level/PlayerSpawnFinder;scheduleCandidate(IIILjava/util/function/Supplier;)V")
	@Definition(id = "spawnSuggestion", field = "Lnet/minecraft/server/level/PlayerSpawnFinder;spawnSuggestion:Lnet/minecraft/core/BlockPos;")
	@Definition(id = "getX", method = "Lnet/minecraft/core/BlockPos;getX()I")
	@Expression("this.scheduleCandidate(this.spawnSuggestion.getX(), ?, ?, ?)")
	@Redirect(method = "scheduleNext", at = @At("MIXINEXTRAS:EXPRESSION"))
	void scheduleNext_scheduleCandidateFallback(PlayerSpawnFinder This, int candidateX, int candidateZ, int candidateIndex, Supplier<Optional<Vec3>> candidateChecker) {
		this.scheduleCandidate(candidateX, candidateZ, Vec4i.getW(this.spawnSuggestion), candidateIndex, candidateChecker);
	}

	@ModifyConstant(method = "getCoprime", constant = @Constant(intValue = 17))
	private static int getCoprime(int constant) {
		return 32771;
	}

	@Overwrite
	private void scheduleCandidate(int candidateX, int candidateZ, int candidateIndex, Supplier<Optional<Vec3>> candidateChecker) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void scheduleCandidate(int candidateX, int candidateZ, int candidateW, int candidateIndex, Supplier<Optional<Vec3>> candidateChecker) {
		if (this.finishedFuture.isDone()) return;
		int chunkX = SectionPos.blockToSectionCoord(candidateX);
		int chunkZ = SectionPos.blockToSectionCoord(candidateZ);
		int chunkW = SectionPos.blockToSectionCoord(candidateW);
		this.level
			.getChunkSource()
			.addTicketAndLoadWithRadius(TicketType.SPAWN_SEARCH, ChunkPos4.from(chunkX, chunkZ, chunkW), 0)
			.whenCompleteAsync((ignored, throwable) -> {
				if (throwable == null) {
					try {
						Optional<Vec3> spawnPos = candidateChecker.get();
						if (spawnPos.isPresent()) {
							this.finishedFuture.complete(spawnPos.get());
						} else {
							this.scheduleNext();
						}
					} catch (Throwable e) {
						throwable = e;
					}
				}

				if (throwable != null) {
					CrashReport report = CrashReport.forThrowable(throwable, "Searching for spawn");
					CrashReportCategory details = report.addCategory("Spawn Lookup");
					details.setDetail("Origin", this.spawnSuggestion::toString);
					details.setDetail("Radius", () -> Integer.toString(this.radius));
					details.setDetail("Candidate", () -> "[" + candidateX + "," + candidateZ + "," + candidateW + "]");
					details.setDetail("Progress", () -> candidateIndex + " out of " + this.candidateCount);
					this.finishedFuture.completeExceptionally(new ReportedException(report));
				}
			}, this.level.getServer());
	}


	@Overwrite
	protected static @Nullable BlockPos getOverworldRespawnPos(ServerLevel level, int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private static @Nullable BlockPos getOverworldRespawnPos(ServerLevel level, int x, int z, int w) {
		boolean caveWorld = level.dimensionType().hasCeiling();
		LevelChunk chunk = ((Level4) level).getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), SectionPos.blockToSectionCoord(w));
		if (!(chunk instanceof ChunkAccess4 chunk4)) {
			throw Err4.container3();
		}
		int topY = caveWorld
			? level.getChunkSource().getGenerator().getSpawnHeight(level)
			: chunk4.getHeight(Heightmap.Types.MOTION_BLOCKING, SectionPos.sectionRelative(x), SectionPos.sectionRelative(z), SectionPos.sectionRelative(w));
		if (topY < level.getMinY()) {
			return null;
		}
		int surface = chunk4.getHeight(Heightmap.Types.WORLD_SURFACE, SectionPos.sectionRelative(x), SectionPos.sectionRelative(z), SectionPos.sectionRelative(w));
		if (surface <= topY && surface > chunk4.getHeight(Heightmap.Types.OCEAN_FLOOR, SectionPos.sectionRelative(x), SectionPos.sectionRelative(z), SectionPos.sectionRelative(w))) {
			return null;
		}

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int y = topY + 1; y >= level.getMinY(); y--) {
			((BlockPos4.MutableBlockPos) pos).set(x, y, z, w);
			BlockState blockState = level.getBlockState(pos);
			if (!blockState.getFluidState().isEmpty()) {
				break;
			}
			if (Block.isFaceFull(blockState.getCollisionShape(level, pos), Direction.UP)) {
				return pos.above().immutable();
			}
		}

		return null;
	}

	@Redirect(method = "getSpawnPosInChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/PlayerSpawnFinder;getOverworldRespawnPos(Lnet/minecraft/server/level/ServerLevel;II)Lnet/minecraft/core/BlockPos;"
	))
	private static @Nullable BlockPos getSpawnPosInChunk(ServerLevel level, int x, int z, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		ChunkPos4 chunkPos4 = ChunkPos4.as(chunkPos);
		for (int w = chunkPos4.getMinBlockW(); w <= chunkPos4.getMaxBlockW(); w++) {
			BlockPos validSpawnPosition = getOverworldRespawnPos(level, x, z, w);
			if (validSpawnPosition != null) {
				return validSpawnPosition;
			}
		}
		return null;
	}
}
