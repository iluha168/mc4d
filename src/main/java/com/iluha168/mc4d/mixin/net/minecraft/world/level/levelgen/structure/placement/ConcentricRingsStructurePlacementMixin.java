package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.structure.placement;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(ConcentricRingsStructurePlacement.class)
abstract class ConcentricRingsStructurePlacementMixin extends StructurePlacementMixin {
	@Overwrite
	@Deprecated
	protected boolean isPlacementChunk(ChunkGeneratorStructureState generatorState, int sourceX, int sourceZ) {
		throw Err4.arguments2("StructurePlacement4#isPlacementChunk");
	}
	@Override
	public boolean isPlacementChunk(ChunkGeneratorStructureState generatorState, int sourceX, int sourceZ, int sourceW) {
		List<ChunkPos> positions = generatorState.getRingPositionsFor((ConcentricRingsStructurePlacement) (Object) this);
		return positions != null && positions.contains(ChunkPos4.from(sourceX, sourceZ, sourceW));
	}
}
