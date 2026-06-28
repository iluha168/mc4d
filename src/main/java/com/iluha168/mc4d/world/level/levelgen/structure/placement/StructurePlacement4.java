package com.iluha168.mc4d.world.level.levelgen.structure.placement;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.WorldgenRandom4;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

/**
 * Implemented by {@link StructurePlacement}.
 */
public interface StructurePlacement4 {
	boolean isStructureChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW);

	boolean applyAdditionalChunkRestrictions(int sourceX, int sourceZ, int sourceW, long levelSeed);

	boolean applyInteractionsWithOtherStructures(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW);

	static boolean probabilityReducer(long seed, int salt, int sourceX, int sourceZ, int sourceW, float probability) {
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		((WorldgenRandom4) random).setLargeFeatureWithSalt(seed, salt, sourceX, sourceZ, sourceW);
		return random.nextFloat() < probability;
	}
	static boolean legacyProbabilityReducerWithDouble(long seed, int ignoredSalt, int sourceX, int sourceZ, int sourceW, float probability) {
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		((WorldgenRandom4) random).setLargeFeatureSeed(seed, sourceX, sourceZ, sourceW);
		return random.nextDouble() < probability;
	}
	static boolean legacyArbitrarySaltProbabilityReducer(long seed, int ignoredSalt, int sourceX, int sourceZ, int sourceW, float probability) {
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		((WorldgenRandom4) random).setLargeFeatureWithSalt(seed, sourceX, sourceZ, sourceW, 10387320);
		return random.nextFloat() < probability;
	}
	static boolean legacyPillagerOutpostReducer(long seed, int ignoredSalt, int sourceX, int sourceZ, int sourceW, float probability) {
		int cx = sourceX >> 4;
		int cz = sourceZ >> 4;
		int cw = sourceW >> 4;
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		random.setSeed(cx ^ cz ^ cw << 4 ^ seed);
		random.nextInt();
		return random.nextInt((int) (1.0F / probability)) == 0;
	}

	/**
	 * Implemented by {@link StructurePlacement.ExclusionZone}.
	 */
	@SuppressWarnings("deprecation")
	interface ExclusionZone {
		static StructurePlacement4.ExclusionZone as(StructurePlacement.ExclusionZone zone) {
			return (StructurePlacement4.ExclusionZone) (Object) zone;
		}

		boolean isPlacementForbidden(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW);
	}

	/**
	 * Implemented by the same classes that implement {@link StructurePlacement.FrequencyReducer}.
	 */
	@FunctionalInterface
	interface FrequencyReducer {
		boolean shouldGenerate(long seed, final int salt, final int sourceX, final int sourceZ, final int sourceW, float probability);

		/**
		 * Can be assigned to variables of type {@link StructurePlacement.FrequencyReducer}, and can be cast to {@link StructurePlacement4.FrequencyReducer}.
		 */
		class Impl implements StructurePlacement.FrequencyReducer, StructurePlacement4.FrequencyReducer {
			private final StructurePlacement4.FrequencyReducer resolver;

			public Impl(StructurePlacement4.FrequencyReducer resolver) {
				this.resolver = resolver;
			}

			@Override
			public boolean shouldGenerate(long seed, int salt, int sourceX, int sourceZ, float probability) {
				throw Err4.arguments2("StructurePlacement4.FrequencyReducer#shouldGenerate");
			}
			@Override
			public boolean shouldGenerate(long seed, int salt, int sourceX, int sourceZ, int sourceW, float probability) {
				return this.resolver.shouldGenerate(seed, salt, sourceX, sourceZ, sourceW, probability);
			}
		}
	}

	/**
	 * Implemented by {@link StructurePlacement.FrequencyReductionMethod}.
	 */
	interface FrequencyReductionMethod {
		static StructurePlacement4.FrequencyReductionMethod as(StructurePlacement.FrequencyReductionMethod method) {
			return (StructurePlacement4.FrequencyReductionMethod) (Object) method;
		}

		boolean shouldGenerate(long seed, int salt, int sourceX, int sourceZ, int sourceW, float probability);
	}
}
