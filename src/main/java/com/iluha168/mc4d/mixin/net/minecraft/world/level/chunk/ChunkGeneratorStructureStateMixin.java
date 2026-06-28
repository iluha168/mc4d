package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.biome.BiomeSource4;
import com.iluha168.mc4d.world.level.chunk.ChunkGeneratorStructureState4;
import com.iluha168.mc4d.world.level.levelgen.structure.placement.StructurePlacement4;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

@Mixin(ChunkGeneratorStructureState.class)
abstract class ChunkGeneratorStructureStateMixin implements ChunkGeneratorStructureState4 {
	@Shadow @Final private RandomState randomState;
	@Shadow @Final private BiomeSource biomeSource;

	@ModifyArg(method = "generateRingPositions", at = @At(
		value = "INVOKE",
		target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
	))
	private Supplier<ChunkPos> generateRingPositions(
		Supplier<ChunkPos> supplier,
		@Local(name = "angle") double angle,
		@Local(name = "dist") double dist,
		@Local(name = "random") RandomSource random,
		@Local(name = "preferredBiomes") HolderSet<Biome> preferredBiomes,
		@Local(name = "biomeSearchGenerator") RandomSource biomeSearchGenerator
	) {
		double cosPhi = random.nextDouble() * 2.0 - 1.0;
		double horizontalScale = Math.sqrt(1.0 - cosPhi * cosPhi);
		// Point on a sphere with radius dist, not a circle anymore
		int initialX = (int) Math.round(Math.cos(angle) * dist * horizontalScale);
		int initialZ = (int) Math.round(Math.sin(angle) * dist * horizontalScale);
		int initialW = (int) Math.round(cosPhi * dist);
		BiomeSource4 biomeSource4 = (BiomeSource4) this.biomeSource;
		return () -> {
			Pair<BlockPos, Holder<Biome>> closestBiome = biomeSource4.findBiomeHorizontal(
				SectionPos.sectionToBlockCoord(initialX, 8),
				0,
				SectionPos.sectionToBlockCoord(initialZ, 8),
				SectionPos.sectionToBlockCoord(initialW, 8),
				112,
				preferredBiomes::contains,
				biomeSearchGenerator,
				this.randomState.sampler()
			);
			if (closestBiome != null) {
				BlockPos position = closestBiome.getFirst();
				return ChunkPos4.from(
					SectionPos.blockToSectionCoord(position.getX()),
					SectionPos.blockToSectionCoord(position.getZ()),
					SectionPos.blockToSectionCoord(Vec4i.getW(position))
				);
			} else {
				return ChunkPos4.from(initialX, initialZ, initialW);
			}
		};
	}

	@Overwrite
	@Deprecated
	public boolean hasStructureChunkInRange(Holder<StructureSet> structureSet, int sourceX, int sourceZ, int range) {
		throw Err4.arguments2("ChunkGeneratorStructureState4#hasStructureChunkInRange");
	}
	@Override
	public boolean hasStructureChunkInRange(Holder<StructureSet> structureSet, int sourceX, int sourceZ, int sourceW, int range) {
		StructurePlacement4 placement = (StructurePlacement4) structureSet.value().placement();
		ChunkGeneratorStructureState This = (ChunkGeneratorStructureState) (Object) this;
		for (int testX = sourceX - range; testX <= sourceX + range; testX++)
			for (int testZ = sourceZ - range; testZ <= sourceZ + range; testZ++)
				for (int testW = sourceW - range; testW <= sourceW + range; testW++)
					if (placement.isStructureChunk(This, testX, testZ, testW))
						return true;
		return false;
	}
}
