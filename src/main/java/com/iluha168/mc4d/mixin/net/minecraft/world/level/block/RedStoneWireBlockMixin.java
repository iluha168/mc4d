package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(RedStoneWireBlock.class)
class RedStoneWireBlockMixin {
	@ModifyArg(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lcom/google/common/collect/Maps;newEnumMap(Ljava/util/Map;)Ljava/util/EnumMap;"
	))
	private static Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION(Map<Direction, EnumProperty<RedstoneSide>> map) {
		HashMap<Direction, EnumProperty<RedstoneSide>> total = new HashMap<>(map);
		total.put(Direction4.ANA, BlockStateProperties4.ANA_REDSTONE);
		total.put(Direction4.KATA, BlockStateProperties4.KATA_REDSTONE);
		return total;
	}

	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"
	))
	BlockState registerDefaultState(BlockState state) {
		return state
			.setValue(BlockStateProperties4.ANA_REDSTONE, RedstoneSide.NONE)
			.setValue(BlockStateProperties4.KATA_REDSTONE, RedstoneSide.NONE);
	}

	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/RedStoneWireBlock;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState crossState(BlockState state) {
		return state
			.setValue(BlockStateProperties4.ANA_REDSTONE, RedstoneSide.SIDE)
			.setValue(BlockStateProperties4.KATA_REDSTONE, RedstoneSide.SIDE);
	}

	// This getConnectionState branch makes a redstone dust with exactly 1 SIDE connection impossible
	@Inject(method = "getConnectionState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/state/properties/RedstoneSide;isConnected()Z",
		ordinal = 0
	))
	private static void getConnectionState_anaKataEmpty(
		BlockGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir,
		@Share("ana") LocalBooleanRef ana,
		@Share("kata") LocalBooleanRef kata,
		@Share("anaKataEmpty") LocalBooleanRef anaKataEmpty
	) {
		ana.set(state.getValue(BlockStateProperties4.ANA_REDSTONE).isConnected());
		kata.set(state.getValue(BlockStateProperties4.KATA_REDSTONE).isConnected());
		anaKataEmpty.set(!ana.get() && !kata.get());
	}
	@Definition(id = "northSouthEmpty", local = @Local(type = boolean.class, name = "northSouthEmpty"))
	@Expression("northSouthEmpty")
	@ModifyExpressionValue(method = "getConnectionState", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean getConnectionState_northSouthEmpty(boolean original, @Share("anaKataEmpty") LocalBooleanRef anaKataEmpty) {
		return original && anaKataEmpty.get();
	}
	@Definition(id = "eastWestEmpty", local = @Local(type = boolean.class, name = "eastWestEmpty"))
	@Expression("eastWestEmpty")
	@ModifyExpressionValue(method = "getConnectionState", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean getConnectionState_eastWestEmpty(boolean original, @Share("anaKataEmpty") LocalBooleanRef anaKataEmpty) {
		return original && anaKataEmpty.get();
	}

	@Expression("return @(?)")
	@ModifyExpressionValue(method = "getConnectionState", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private static BlockState getConnectionState_w(
		BlockState state,
		@Share("ana") LocalBooleanRef ana,
		@Share("kata") LocalBooleanRef kata,
		@Local(name = "northSouthEmpty") boolean northSouthEmpty,
		@Local(name = "eastWestEmpty") boolean eastWestEmpty
	) {
		final boolean allOtherEmpty = northSouthEmpty && eastWestEmpty;
		if (!ana.get() && allOtherEmpty) {
			state = state.setValue(BlockStateProperties4.ANA_REDSTONE, RedstoneSide.SIDE);
		}
		if (!kata.get() && allOtherEmpty) {
			state = state.setValue(BlockStateProperties4.KATA_REDSTONE, RedstoneSide.SIDE);
		}
		return state;
	}

	@WrapMethod(method = "isCross")
	private static boolean isCross(BlockState state, Operation<Boolean> original) {
		return original.call(state)
			&& state.getValue(BlockStateProperties4.ANA_REDSTONE).isConnected()
			&& state.getValue(BlockStateProperties4.KATA_REDSTONE).isConnected();
	}

	@WrapMethod(method = "isDot")
	private static boolean isDot(BlockState state, Operation<Boolean> original) {
		return original.call(state)
			&& !state.getValue(BlockStateProperties4.ANA_REDSTONE).isConnected()
			&& !state.getValue(BlockStateProperties4.KATA_REDSTONE).isConnected();
	}

	@Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
	void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(BlockStateProperties4.ANA_REDSTONE, BlockStateProperties4.KATA_REDSTONE);
	}
}
