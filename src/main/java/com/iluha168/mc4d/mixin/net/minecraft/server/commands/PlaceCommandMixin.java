package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.api.net.minecraft.world.level.ChunkPos4;
import com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.SectionPos;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlaceCommand.class)
class PlaceCommandMixin {
	@Definition(id = "ChunkPos", type = ChunkPos.class)
	@Expression("new ChunkPos(? - 1, ? - 1)")
	@ModifyExpressionValue(method = "placeFeature", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ChunkPos placeFeature_chunkMin(ChunkPos original, @Local(name = "chunkPos") ChunkPos chunkPos) {
		ChunkPos4.as(original).setW(ChunkPos4.as(chunkPos).w() - 1);
		return original;
	}
	@Definition(id = "ChunkPos", type = ChunkPos.class)
	@Expression("new ChunkPos(? + 1, ? + 1)")
	@ModifyExpressionValue(method = "placeFeature", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ChunkPos placeFeature_chunkMax(ChunkPos original, @Local(name = "chunkPos") ChunkPos chunkPos) {
		ChunkPos4.as(original).setW(ChunkPos4.as(chunkPos).w() + 1);
		return original;
	}
	// TODO placeFeature success message

	// TODO placeJigsaw success message

	@Definition(id = "ChunkPos", type = ChunkPos.class)
	@Definition(id = "blockToSectionCoord", method = "Lnet/minecraft/core/SectionPos;blockToSectionCoord(I)I")
	@Definition(id = "minX", method = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;minX()I")
	@Expression("new ChunkPos(blockToSectionCoord(?.minX()), ?)")
	@ModifyExpressionValue(method = "placeStructure", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ChunkPos placeStructure_chunkMin(ChunkPos original, @Local(name = "boundingBox") BoundingBox boundingBox) {
		ChunkPos4.as(original).setW(SectionPos.blockToSectionCoord(((BoundingBox4) boundingBox).minW()));
		return original;
	}
	@Definition(id = "ChunkPos", type = ChunkPos.class)
	@Definition(id = "blockToSectionCoord", method = "Lnet/minecraft/core/SectionPos;blockToSectionCoord(I)I")
	@Definition(id = "maxX", method = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;maxX()I")
	@Expression("new ChunkPos(blockToSectionCoord(?.maxX()), ?)")
	@ModifyExpressionValue(method = "placeStructure", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ChunkPos placeStructure_chunkMax(ChunkPos original, @Local(name = "boundingBox") BoundingBox boundingBox) {
		ChunkPos4.as(original).setW(SectionPos.blockToSectionCoord(((BoundingBox4) boundingBox).maxW()));
		return original;
	}
	@ModifyExpressionValue(method = "lambda$placeStructure$1", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private static BoundingBox placeStructure_placeInChunk(BoundingBox bb, @Local(argsOnly = true, name = "c") ChunkPos c) {
		final ChunkPos4 pos4 = ChunkPos4.as(c);
		final BoundingBox4 bb4 = (BoundingBox4) bb;
		bb4.setMinW(pos4.getMinBlockW());
		bb4.setMaxW(pos4.getMaxBlockW());
		return bb;
	}
	// TODO placeStructure success message

	// TODO placeTemplate success message
}
