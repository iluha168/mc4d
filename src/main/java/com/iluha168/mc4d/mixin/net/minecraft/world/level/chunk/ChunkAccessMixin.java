package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.level.levelgen.Heightmap4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements ChunkAccess4 {
	@Shadow
	@Final
	protected Map<Heightmap.Types, Heightmap> heightmaps;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	public int getMinY() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public int getHeight() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	public LevelChunkSection[] sections;

	@Shadow
	@Final
	protected ChunkPos chunkPos;

	@Overwrite
	@Deprecated
	public int getHeight(Heightmap.Types type, int x, int z) {
		throw Err4.arguments2("ChunkAccess4#getHeight");
	}
	@Override
	public int getHeight(Heightmap.Types type, int x, int z, int w) {
		Heightmap heightmap = this.heightmaps.get(type);
		if (heightmap == null) {
			//noinspection ConstantValue
			if (SharedConstants.IS_RUNNING_IN_IDE && ((ChunkAccess) (Object) this) instanceof LevelChunk) {
				LOGGER.error("Unprimed heightmap: {} {} {} {}", type, x, z, w);
			}

			Heightmap.primeHeightmaps((ChunkAccess) (Object) this, EnumSet.of(type));
			heightmap = this.heightmaps.get(type);
		}

		return ((Heightmap4) heightmap).getFirstAvailable(
			SectionPos.sectionRelative(x),
			SectionPos.sectionRelative(z),
			SectionPos.sectionRelative(w)
		) - 1;
	}

	@Definition(id = "y", local = @Local(type = int.class, name = "y"))
	@Expression("y = @(0)")
	@Inject(method = "findBlocks(Ljava/util/function/Predicate;Ljava/util/function/BiPredicate;Ljava/util/function/BiConsumer;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	void findBlocks_w(Predicate<BlockState> predicate, BiPredicate<BlockState, BlockPos> fineFilter, BiConsumer<BlockPos, BlockState> consumer, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(0);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "y", local = @Local(type = int.class, name = "y"))
	@Expression("y = y + @(1)")
	@ModifyExpressionValue(method = "findBlocks(Ljava/util/function/Predicate;Ljava/util/function/BiPredicate;Ljava/util/function/BiConsumer;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	int findBlocks_incrementW(int one, @Share("w") LocalIntRef w) {
		w.set(w.get() + 1);
		if (w.get() < LevelChunkSection.SECTION_WIDTH) return 0;
		w.set(0);
		return 1;
	}
	@Redirect(method = "findBlocks(Ljava/util/function/Predicate;Ljava/util/function/BiPredicate;Ljava/util/function/BiConsumer;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState findBlocks_getBlockState(LevelChunkSection section, int sectionX, int sectionY, int sectionZ, @Share("w") LocalIntRef w) {
		return ((LevelChunkSection4) section).getBlockState(sectionX, sectionY, sectionZ, w.get());
	}
	@Redirect(method = "findBlocks(Ljava/util/function/Predicate;Ljava/util/function/BiPredicate;Ljava/util/function/BiConsumer;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos findBlocks_setWithOffset(BlockPos.MutableBlockPos mutablePos, Vec3i pos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) mutablePos).setWithOffset(pos, x, y, z, w.get());
	}

	@Overwrite
	@Deprecated
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ) {
		throw Err4.arguments3("ChunkAccess4#getNoiseBiome");
	}
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW) {
		try {
			int quartMinY = QuartPos.fromBlock(this.getMinY());
			int quartMaxY = quartMinY + QuartPos.fromBlock(this.getHeight()) - 1;
			int clampedQuartY = Mth.clamp(quartY, quartMinY, quartMaxY);
			int sectionIndex = ((LevelHeightAccessor) this).getSectionIndex(QuartPos.toBlock(clampedQuartY));
			return ((LevelChunkSection4) this.sections[sectionIndex]).getNoiseBiome(quartX & 3, clampedQuartY & 3, quartZ & 3, quartW & 3);
		} catch (Throwable var8) {
			CrashReport report = CrashReport.forThrowable(var8, "Getting biome");
			CrashReportCategory category = report.addCategory("Biome being got");
			category.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor) this, quartX, quartY, quartZ)); // TODO formatLocation4
			throw new ReportedException(report);
		}
	}

	@Definition(id = "quartMinZ", local = @Local(type = int.class, name = "quartMinZ"))
	@Expression("quartMinZ = @(?)")
	@Inject(method = "fillBiomesFromNoise", at = @At("MIXINEXTRAS:EXPRESSION"))
	void fillBiomesFromNoise_quartMinW(
		BiomeResolver biomeResolver, Climate.Sampler sampler, CallbackInfo ci,
		@Local(name = "pos") ChunkPos pos,
		@Share("quartMinW") LocalIntRef quartMinW
	) {
		quartMinW.set(QuartPos.fromBlock(ChunkPos4.as(pos).getMinBlockW()));
	}
	@Redirect(method = "fillBiomesFromNoise", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;fillBiomesFromNoise(Lnet/minecraft/world/level/biome/BiomeResolver;Lnet/minecraft/world/level/biome/Climate$Sampler;III)V"
	))
	void fillBiomesFromNoise(
		LevelChunkSection section, BiomeResolver biomeResolver, Climate.Sampler sampler,
		int quartMinX, int quartMinY, int quartMinZ, @Share("quartMinW") LocalIntRef quartMinW
	) {
		if (!(section instanceof LevelChunkSection4 section4 && biomeResolver instanceof BiomeResolver4 biomeResolver4))
			throw Err4.container3();
		section4.fillBiomesFromNoise(biomeResolver4, sampler, quartMinX, quartMinY, quartMinZ, quartMinW.get());
	}
}
