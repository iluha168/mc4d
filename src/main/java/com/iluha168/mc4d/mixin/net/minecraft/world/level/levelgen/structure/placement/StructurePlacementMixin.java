package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.structure.placement;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkGeneratorStructureState4;
import com.iluha168.mc4d.world.level.levelgen.structure.placement.StructurePlacement4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(StructurePlacement.class)
abstract class StructurePlacementMixin implements StructurePlacement4 {
	@Shadow @Final private float frequency;
	@Shadow @Final private int salt;
	@Shadow @Final private StructurePlacement.FrequencyReductionMethod frequencyReductionMethod;
	@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "deprecation"})
	@Shadow @Final private Optional<StructurePlacement.ExclusionZone> exclusionZone;

	@Shadow
	protected abstract int salt();

	@Overwrite
	@Deprecated
	public boolean isStructureChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ) {
		throw Err4.arguments2("StructurePlacement4#isStructureChunk");
	}
	@Override
	public boolean isStructureChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW) {
		return this.isPlacementChunk(state, sourceX, sourceZ, sourceW)
			&& this.applyAdditionalChunkRestrictions(sourceX, sourceZ, sourceW, state.getLevelSeed())
			&& this.applyInteractionsWithOtherStructures(state, sourceX, sourceZ, sourceW);
	}

	@Overwrite
	@Deprecated
	public boolean applyAdditionalChunkRestrictions(int sourceX, int sourceZ, long levelSeed) {
		throw Err4.arguments2("StructurePlacement4#applyAdditionalChunkRestrictions");
	}
	@Override
	public boolean applyAdditionalChunkRestrictions(int sourceX, int sourceZ, int sourceW, long levelSeed) {
		return !(this.frequency < 1.0F) || StructurePlacement4.FrequencyReductionMethod.as(this.frequencyReductionMethod).shouldGenerate(levelSeed, this.salt, sourceX, sourceZ, sourceW, this.frequency);
	}

	@Overwrite
	@Deprecated
	public boolean applyInteractionsWithOtherStructures(ChunkGeneratorStructureState state, int sourceX, int sourceZ) {
		throw Err4.arguments2("StructurePlacement4#applyInteractionsWithOtherStructures");
	}
	@Override
	public boolean applyInteractionsWithOtherStructures(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW) {
		return this.exclusionZone.isEmpty() || !StructurePlacement4.ExclusionZone.as(this.exclusionZone.get()).isPlacementForbidden(state, sourceX, sourceZ, sourceW);
	}

	@Unique
	protected abstract boolean isPlacementChunk(final ChunkGeneratorStructureState state, final int sourceX, final int sourceZ, final int sourceW);

	@ModifyExpressionValue(method = "getLocatePos", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getLocatePos(BlockPos original, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		Vec4i.setW(original, ChunkPos4.as(chunkPos).getMinBlockW());
		return original;
	}

	@Overwrite
	@Deprecated
	private static boolean probabilityReducer(long seed, int salt, int sourceX, int sourceZ, float probability) {
		throw Err4.arguments2("StructurePlacement4#probabilityReducer");
	}

	@Overwrite
	@Deprecated
	private static boolean legacyProbabilityReducerWithDouble(long seed, int salt, int sourceX, int sourceZ, float probability) {
		throw Err4.arguments2("StructurePlacement4#legacyProbabilityReducer");
	}

	@Overwrite
	@Deprecated
	private static boolean legacyArbitrarySaltProbabilityReducer(long seed, int salt, int sourceX, int sourceZ, float probability) {
		throw Err4.arguments2("StructurePlacement4#legacyArbitrarySaltProbabilityReducer");
	}

	@Overwrite
	@Deprecated
	private static boolean legacyPillagerOutpostReducer(long seed, int salt, int sourceX, int sourceZ, float probability) {
		throw Err4.arguments2("StructurePlacement4#legacyPillagerOutpostReducer");
	}

	@SuppressWarnings("deprecation")
	@Mixin(StructurePlacement.ExclusionZone.class)
	static class ExclusionZoneMixin implements StructurePlacement4.ExclusionZone {
		@Shadow
		@Final
		private Holder<StructureSet> otherSet;

		@Shadow
		@Final
		private int chunkCount;

		@Overwrite
		@Deprecated
		private boolean isPlacementForbidden(ChunkGeneratorStructureState state, int sourceX, int sourceZ) {
			throw Err4.arguments2("StructurePlacement4.ExclusionZone#isPlacementForbidden");
		}
		@Override
		public boolean isPlacementForbidden(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW) {
			return ((ChunkGeneratorStructureState4) state).hasStructureChunkInRange(this.otherSet, sourceX, sourceZ, sourceW, this.chunkCount);
		}
	}

	@Mixin(StructurePlacement.FrequencyReductionMethod.class)
	static class FrequencyReductionMethodMixin implements StructurePlacement4.FrequencyReductionMethod {
		@Shadow
		@Final
		private StructurePlacement.FrequencyReducer reducer;

		@Definition(id = "FrequencyReductionMethod", type = StructurePlacement.FrequencyReductionMethod.class)
		@Definition(id = "probabilityReducer", method = "Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;probabilityReducer(JIIIF)Z")
		@Expression("new FrequencyReductionMethod(?, ?, ?, ::probabilityReducer)")
		@ModifyArg(method = "<clinit>", index = 3, at = @At("MIXINEXTRAS:EXPRESSION"))
		private static StructurePlacement.FrequencyReducer DEFAULT(StructurePlacement.FrequencyReducer frequencyReducer) {
			return new StructurePlacement4.FrequencyReducer.Impl(StructurePlacement4::probabilityReducer);
		}
		@Definition(id = "FrequencyReductionMethod", type = StructurePlacement.FrequencyReductionMethod.class)
		@Definition(id = "legacyPillagerOutpostReducer", method = "Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;legacyPillagerOutpostReducer(JIIIF)Z")
		@Expression("new FrequencyReductionMethod(?, ?, ?, ::legacyPillagerOutpostReducer)")
		@ModifyArg(method = "<clinit>", index = 3, at = @At("MIXINEXTRAS:EXPRESSION"))
		private static StructurePlacement.FrequencyReducer LEGACY_TYPE_1(StructurePlacement.FrequencyReducer frequencyReducer) {
			return new StructurePlacement4.FrequencyReducer.Impl(StructurePlacement4::legacyPillagerOutpostReducer);
		}
		@Definition(id = "FrequencyReductionMethod", type = StructurePlacement.FrequencyReductionMethod.class)
		@Definition(id = "legacyArbitrarySaltProbabilityReducer", method = "Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;legacyArbitrarySaltProbabilityReducer(JIIIF)Z")
		@Expression("new FrequencyReductionMethod(?, ?, ?, ::legacyArbitrarySaltProbabilityReducer)")
		@ModifyArg(method = "<clinit>", index = 3, at = @At("MIXINEXTRAS:EXPRESSION"))
		private static StructurePlacement.FrequencyReducer LEGACY_TYPE_2(StructurePlacement.FrequencyReducer frequencyReducer) {
			return new StructurePlacement4.FrequencyReducer.Impl(StructurePlacement4::legacyArbitrarySaltProbabilityReducer);
		}
		@Definition(id = "FrequencyReductionMethod", type = StructurePlacement.FrequencyReductionMethod.class)
		@Definition(id = "legacyProbabilityReducerWithDouble", method = "Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;legacyProbabilityReducerWithDouble(JIIIF)Z")
		@Expression("new FrequencyReductionMethod(?, ?, ?, ::legacyProbabilityReducerWithDouble)")
		@ModifyArg(method = "<clinit>", index = 3, at = @At("MIXINEXTRAS:EXPRESSION"))
		private static StructurePlacement.FrequencyReducer LEGACY_TYPE_3(StructurePlacement.FrequencyReducer frequencyReducer) {
			return new StructurePlacement4.FrequencyReducer.Impl(StructurePlacement4::legacyProbabilityReducerWithDouble);
		}

		@Overwrite
		@Deprecated
		public boolean shouldGenerate(long seed, int salt, int sourceX, int sourceZ, float probability) {
			throw Err4.arguments2("StructurePlacement4.FrequencyReductionMethod#shouldGenerate");
		}
		@Override
		public boolean shouldGenerate(long seed, int salt, int sourceX, int sourceZ, int sourceW, float probability) {
			return ((StructurePlacement4.FrequencyReducer) this.reducer).shouldGenerate(seed, salt, sourceX, sourceZ, sourceW, probability);
		}
	}
}
