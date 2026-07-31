package com.iluha168.mc4d.gametest.framework;

import com.iluha168.mc4d.MC4D;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Test functions, like in {@link net.minecraft.gametest.framework.BuiltinTestFunctions}.
 * Each one is referenced by {@code data/mc4d/test_instance/*.json}.
 */
public final class StatesTestFunctions {
	private static final int TICK_ATTEMPTS = 100;

	public static final DeferredRegister<Consumer<GameTestHelper>> DEFERRED_REGISTER = DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, MC4D.MODID);
	private static final int PLACE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS;
	private static final BlockState AIR = Blocks.AIR.defaultBlockState();

	static {
		final Predicate<BlockState> ANY_STATE = _ -> true;
		final Predicate<BlockState> HAS_FLUID = state -> !state.getFluidState().isEmpty();

		registerStatesTest("all_states_animate_tick", ANY_STATE,
			(state, level, pos, random) -> state.getBlock().animateTick(state, level, pos, random)
		);
		registerStatesTest("all_states_tick", ANY_STATE, BlockBehaviour.BlockStateBase::tick);
		registerStatesTest("all_states_random_tick", BlockState::isRandomlyTicking, BlockBehaviour.BlockStateBase::randomTick);
		registerStatesTest("all_states_block_entity_tick", BlockState::hasBlockEntity, StatesTestFunctions::tickBlockEntity);
		registerStatesTest("all_states_fluid_animate_tick", HAS_FLUID,
			(state, level, pos, random) -> state.getFluidState().animateTick(level, pos, random)
		);
		registerStatesTest("all_states_fluid_tick", HAS_FLUID,
			(state, level, pos, _) -> state.getFluidState().tick(level, pos, state)
		);
		registerStatesTest("all_states_fluid_random_tick", state -> state.getFluidState().isRandomlyTicking(),
			(state, level, pos, random) -> state.getFluidState().randomTick(level, pos, random)
		);
		registerStatesTest("all_states_precipitation_tick", ANY_STATE,
			(state, level, pos, _) -> {
				state.getBlock().handlePrecipitation(state, level, pos, Biome.Precipitation.RAIN);
				state.getBlock().handlePrecipitation(state, level, pos, Biome.Precipitation.SNOW);
			}
		);
	}
	private static void registerStatesTest(String name, Predicate<BlockState> filter, StateTick tick) {
		DEFERRED_REGISTER.register(name, () -> helper -> statesTest(helper, filter, tick));
	}
	/**
	 * Mirrors {@link net.minecraft.world.level.chunk.LevelChunk.BoundTickingBlockEntity#tick}.
	 */
	@SuppressWarnings({"unchecked", "JavadocReference"})
	private static <T extends BlockEntity> void tickBlockEntity(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		final T blockEntity = (T) level.getBlockEntity(pos);
		if (blockEntity == null || blockEntity.isRemoved() || !blockEntity.getType().isValid(state)) return;
		final BlockEntityTicker<T> ticker = state.getTicker(level, (BlockEntityType<T>) blockEntity.getType());
		if (ticker != null) ticker.tick(level, pos, state, blockEntity);
	}

	private StatesTestFunctions() {}

	@FunctionalInterface
	private interface StateTick {
		void exec(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
	}

	/**
	 * Places every {@link DebugLevelSource#ALL_BLOCKS every blockstate in the game} matching {@code predicate},
	 * in the middle of the volume without any side effects, and runs {@code tick} on it {@value #TICK_ATTEMPTS} times.
	 */
	private static void statesTest(GameTestHelper helper, Predicate<BlockState> predicate, StateTick tick) {
		final ServerLevel level = helper.getLevel();

		final BoundingBox bounds = helper.testInfo.getTestInstanceBlockEntity().getStructureBoundingBox();
		final BoundingBox4 bounds4 = (BoundingBox4) bounds;
		final BlockPos center = bounds.getCenter();

		final BlockPos4.MutableBlockPos mutablePos4 = (BlockPos4.MutableBlockPos) new BlockPos.MutableBlockPos();

		for (final BlockState state : DebugLevelSource.ALL_BLOCKS) {
			if (!predicate.test(state)) continue;
			// Ticking
			level.setBlock(center, state, PLACE_FLAGS);
			for (int attempt = 0; attempt < TICK_ATTEMPTS; attempt++)
				tick.exec(state, level, center, level.getRandom());

			// Clearing
			level.getBlockTicks().clearArea(bounds);
			level.getFluidTicks().clearArea(bounds);
			helper.killAllEntities();

			for (int x = bounds.minX(); x < bounds.maxX(); x++)
				for (int y = bounds.minY(); y < bounds.maxY(); y++)
					for (int z = bounds.minZ(); z < bounds.maxZ(); z++)
						for (int w = bounds4.minW(); w < bounds4.maxW(); w++)
							level.setBlock(mutablePos4.set(x, y, z, w), AIR, PLACE_FLAGS);
		}

		helper.succeed();
	}
}
