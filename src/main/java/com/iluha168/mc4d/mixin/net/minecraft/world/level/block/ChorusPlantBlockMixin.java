package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChorusPlantBlock.class)
public class ChorusPlantBlockMixin {
	@WrapOperation(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/ChorusPlantBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"
	))
	void registerDefaultState(ChorusPlantBlock instance, BlockState state, Operation<Void> original) {
		original.call(instance, state
			.setValue(BlockStateProperties4.ANA, false)
			.setValue(BlockStateProperties4.KATA, false)
		);
	}

	@ModifyReturnValue(method = "getStateWithConnections", at = @At("RETURN"))
	private static BlockState getStateWithConnections(
		BlockState original,
		@Local(argsOnly = true, name = "level") BlockGetter level,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(name = "block") Block block
	) {
		final BlockPos4 pos4 = (BlockPos4) pos;
		final BlockState ana = level.getBlockState(pos4.ana());
		final BlockState kata = level.getBlockState(pos4.kata());
		return original
			.trySetValue(BlockStateProperties4.ANA, ana.is(block) || ana.is(Blocks.CHORUS_FLOWER))
			.trySetValue(BlockStateProperties4.KATA, kata.is(block) || kata.is(Blocks.CHORUS_FLOWER));
	}

	@Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
	void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(BlockStateProperties4.ANA, BlockStateProperties4.KATA);
	}
}
