package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.structure.placement;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.levelgen.WorldgenRandom4;
import com.iluha168.mc4d.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement4;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RandomSpreadStructurePlacement.class)
abstract class RandomSpreadStructurePlacementMixin extends StructurePlacementMixin implements RandomSpreadStructurePlacement4 {
	@Shadow @Final private int spacing;
	@Shadow @Final private int separation;
	@Shadow @Final private RandomSpreadType spreadType;

	@Overwrite
	@Deprecated
	public ChunkPos getPotentialStructureChunk(long seed, int sourceX, int sourceZ) {
		throw Err4.arguments2("RandomSpreadStructurePlacement4#getPotentialStructureChunk");
	}
	@Override
	public ChunkPos getPotentialStructureChunk(long seed, int sourceX, int sourceZ, int sourceW) {
		int spacedGridX = Math.floorDiv(sourceX, this.spacing);
		int spacedGridZ = Math.floorDiv(sourceZ, this.spacing);
		int spacedGridW = Math.floorDiv(sourceW, this.spacing);
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		((WorldgenRandom4) random).setLargeFeatureWithSalt(seed, spacedGridX, spacedGridZ, spacedGridW, this.salt());
		int limit = this.spacing - this.separation;
		int spreadX = this.spreadType.evaluate(random, limit);
		int spreadZ = this.spreadType.evaluate(random, limit);
		int spreadW = this.spreadType.evaluate(random, limit);
		return ChunkPos4.from(
			spacedGridX * this.spacing + spreadX,
			spacedGridZ * this.spacing + spreadZ,
			spacedGridW * this.spacing + spreadW
		);
	}

	@Overwrite
	@Deprecated
	protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ) {
		throw Err4.arguments2("StructurePlacement4#isPlacementChunk");
	}
	@Override
	public boolean isPlacementChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ, int sourceW) {
		ChunkPos chunkPos = this.getPotentialStructureChunk(state.getLevelSeed(), sourceX, sourceZ, sourceW);
		return chunkPos.x() == sourceX && chunkPos.z() == sourceZ && ChunkPos4.as(chunkPos).w() == sourceW;
	}
}
