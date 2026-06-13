package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronBarsBlock.class)
public class IronBarsBlockMixin {
	@WrapOperation(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/IronBarsBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"
	))
	void registerDefaultState(IronBarsBlock instance, BlockState state, Operation<Void> original) {
		original.call(instance, state
			.setValue(BlockStateProperties4.ANA, false)
			.setValue(BlockStateProperties4.KATA, false)
		);
	}

	@Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
	void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(BlockStateProperties4.ANA, BlockStateProperties4.KATA);
	}
}
