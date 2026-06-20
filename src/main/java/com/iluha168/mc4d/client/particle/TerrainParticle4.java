package com.iluha168.mc4d.client.particle;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link TerrainParticle}.
 */
public interface TerrainParticle4 {
	static TerrainParticle from(
		ClientLevel level,
		double x, double y, double z, double w,
		double xa, double ya, double za, double wa,
		BlockState blockState
	) {
		return from(level, x, y, z, w, xa, ya, za, wa, blockState, BlockPos4.containing(x, y, z, w));
	}

	static TerrainParticle from(
		ClientLevel level,
		double x, double y, double z, double w,
		double xa, double ya, double za, double wa,
		BlockState blockState, BlockPos pos
	) {
		TerrainParticle particle = new TerrainParticle(level, x, y, z, xa, ya, za, blockState, pos);
		((Particle4) particle).init_finish(w, wa);
		return particle;
	}

	static @Nullable TerrainParticle createTerrainParticle(
		BlockParticleOption options, ClientLevel level,
		double x, double y, double z, double w,
		double xAux, double yAux, double zAux, double wAux
	) {
		BlockState state = options.getState();
		//noinspection DataFlowIssue
		return !state.isAir() && !state.is(Blocks.MOVING_PISTON) && state.shouldSpawnTerrainParticles()
			? TerrainParticle4.from(level, x, y, z, w, xAux, yAux, zAux, wAux, state).updateSprite(state, options.getPos())
			: null;
	}
}
