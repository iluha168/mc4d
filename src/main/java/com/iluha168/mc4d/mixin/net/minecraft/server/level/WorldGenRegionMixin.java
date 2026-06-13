package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.util.StaticCache3D;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Locale;

@Mixin(WorldGenRegion.class)
abstract class WorldGenRegionMixin implements LevelReader4 {
	@Shadow
	@Final
	private ChunkAccess center;

	@Shadow
	@Final
	private ChunkStep generatingStep;

	@Shadow
	@Final
	private StaticCache2D<GenerationChunkHolder> cache;

	@Overwrite
	public @Nullable ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus targetStatus, boolean loadOrGenerate) {
		throw Err4.arguments3("LevelReader4#getChunk");
	}
	@Override
	public @Nullable ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW, ChunkStatus targetStatus, boolean loadOrGenerate) {
		int distance = ChunkPos4.as(this.center.getPos()).getChessboardDistance(chunkX, chunkZ, chunkW);
		ChunkStatus maxAllowedStatus = distance >= this.generatingStep.directDependencies().size()
			? null
			: this.generatingStep.directDependencies().get(distance);
		GenerationChunkHolder chunkHolder;
		if (maxAllowedStatus == null) {
			chunkHolder = null;
		} else {
			chunkHolder = ((StaticCache3D<GenerationChunkHolder>) this.cache).get(chunkX, chunkZ, chunkW);
			if (targetStatus.isOrBefore(maxAllowedStatus)) {
				ChunkAccess chunk = chunkHolder.getChunkIfPresentUnchecked(maxAllowedStatus);
				if (chunk != null) {
					return chunk;
				}
			}
		}

		ChunkStatus status = chunkHolder == null ? null : chunkHolder.getPersistedStatus();
		CrashReport report = CrashReport.forThrowable(
			new IllegalStateException("Requested chunk unavailable during world generation"), "Exception generating new chunk"
		);
		CrashReportCategory category = report.addCategory("Chunk request details");
		category.setDetail("Requested chunk", String.format(Locale.ROOT, "%d, %d, %d", chunkX, chunkZ, chunkW));
		category.setDetail("Generating status", () -> this.generatingStep.targetStatus().getName());
		category.setDetail("Requested status", targetStatus::getName);
		category.setDetail("Actual status", () -> chunkHolder == null || status == null ? "[out of cache bounds]" : status.getName());
		category.setDetail("Maximum allowed status", () -> maxAllowedStatus == null ? "null" : maxAllowedStatus.getName());
		category.setDetail("Dependencies", this.generatingStep.directDependencies()::toString);
		category.setDetail("Requested distance", distance);
		category.setDetail("Generating chunk", this.center.getPos()::toString);
		throw new ReportedException(report);
	}

	@Override
	public ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW) {
		return this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.EMPTY);
	}

	@Overwrite
	@Deprecated
	public boolean hasChunk(int chunkX, int chunkZ) {
		throw Err4.arguments2("LevelReader4#hasChunk");
	}
	@Override
	public boolean hasChunk(int chunkX, int chunkZ, int chunkW) {
		int distance = ChunkPos4.as(this.center.getPos()).getChessboardDistance(chunkX, chunkZ, chunkW);
		return distance < this.generatingStep.directDependencies().size();
	}

	// TODO getBlockState
	// TODO getNearestPlayer
	// TODO getUncachedNoiseBiome
	// TODO ensureCanWrite
	// TODO setBlock
	// TODO addFreshEntity
	// TODO getCurrentDifficultyAt

	@Overwrite
	@Deprecated
	public int getHeight(Heightmap.Types type, int x, int z) {
		throw Err4.arguments2("LevelReader4#getHeight");
	}
	@Override
	public int getHeight(Heightmap.Types type, int x, int z, int w) {
		return ((ChunkAccess4) this.getChunk(
			SectionPos.blockToSectionCoord(x),
			SectionPos.blockToSectionCoord(z),
			SectionPos.blockToSectionCoord(w)
		)).getHeight(
			type,
			SectionPos.sectionRelative(x),
			SectionPos.sectionRelative(z),
			SectionPos.sectionRelative(w)
		) + 1;
	}

	// TODO addParticle
}
