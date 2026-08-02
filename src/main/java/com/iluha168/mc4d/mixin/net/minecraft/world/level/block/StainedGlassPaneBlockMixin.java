package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StainedGlassPaneBlock.class)
class StainedGlassPaneBlockMixin {
	@WrapOperation(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/StainedGlassPaneBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"
	))
	void registerDefaultState(StainedGlassPaneBlock instance, BlockState state, Operation<Void> original) {
		original.call(instance, state
			.setValue(BlockStateProperties4.ANA, false)
			.setValue(BlockStateProperties4.KATA, false)
		);
	}
}
