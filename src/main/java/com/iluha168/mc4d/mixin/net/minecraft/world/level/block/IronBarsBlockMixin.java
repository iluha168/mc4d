package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronBarsBlock.class)
public abstract class IronBarsBlockMixin {
	@Shadow
	public abstract boolean attachsTo(BlockState state, boolean faceSolid);

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

	@ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
	BlockState getStateForPlacement(
		BlockState state, BlockPlaceContext context,
		@Local(name = "level") BlockGetter level,
		@Local(name = "pos") BlockPos pos
	) {
		final BlockPos4 pos4 = (BlockPos4) pos;
		final BlockPos ana = pos4.ana();
		final BlockPos kata = pos4.kata();
		final BlockState anaState = level.getBlockState(ana);
		final BlockState kataState = level.getBlockState(kata);
		return state
			.setValue(BlockStateProperties4.ANA, this.attachsTo(anaState, anaState.isFaceSturdy(level, ana, Direction4.KATA)))
			.setValue(BlockStateProperties4.KATA, this.attachsTo(kataState, kataState.isFaceSturdy(level, kata, Direction4.ANA)));
	}

	@Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
	void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(BlockStateProperties4.ANA, BlockStateProperties4.KATA);
	}
}
