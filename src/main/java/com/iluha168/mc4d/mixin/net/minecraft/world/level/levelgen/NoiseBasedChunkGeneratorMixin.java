package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.world.level.levelgen.NoiseBasedChunkGenerator4;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NoiseBasedChunkGenerator.class)
class NoiseBasedChunkGeneratorMixin implements NoiseBasedChunkGenerator4 {
	// TODO createFluidPicker
	// TODO getBaseHeight
	// TODO getBaseColumn
	// TODO getInterpolatedNoiseValue
	// TODO addDebugScreenInfo
	// TODO iterateNoiseColumn
	// TODO applyCarvers
	// TODO doFill
	// TODO debugPreliminarySurfaceLevel
	// TODO spawnOriginalMobs
}
