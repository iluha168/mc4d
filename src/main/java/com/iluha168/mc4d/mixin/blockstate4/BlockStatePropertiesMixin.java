package com.iluha168.mc4d.mixin.blockstate4;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockStateProperties.class)
class BlockStatePropertiesMixin {
	@Definition(id = "create", method = "Lnet/minecraft/world/level/block/state/properties/IntegerProperty;create(Ljava/lang/String;II)Lnet/minecraft/world/level/block/state/properties/IntegerProperty;")
	@Definition(id = "FLOWER_AMOUNT", field = "Lnet/minecraft/world/level/block/state/properties/BlockStateProperties;FLOWER_AMOUNT:Lnet/minecraft/world/level/block/state/properties/IntegerProperty;")
	@Expression("FLOWER_AMOUNT = @(create(?, ?, ?))")
	@ModifyArg(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"), index = 2)
	private static int flowerAmount(int constant) {
		return 8; // 3^2, double the petals per each horizontal dimension
	}

	@Definition(id = "create", method = "Lnet/minecraft/world/level/block/state/properties/IntegerProperty;create(Ljava/lang/String;II)Lnet/minecraft/world/level/block/state/properties/IntegerProperty;")
	@Definition(id = "SEGMENT_AMOUNT", field = "Lnet/minecraft/world/level/block/state/properties/BlockStateProperties;SEGMENT_AMOUNT:Lnet/minecraft/world/level/block/state/properties/IntegerProperty;")
	@Expression("SEGMENT_AMOUNT = @(create(?, ?, ?))")
	@ModifyArg(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"), index = 2)
	private static int segmentAmount(int constant) {
		return 8; // 3^2, double the segments per each horizontal dimension
	}
}
