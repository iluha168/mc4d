package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VineBlock.class)
public class VineBlockMixin extends BlockMixin {
	@Shadow
	public static BooleanProperty getPropertyForFace(Direction direction) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public static boolean isAcceptableNeighbour(BlockGetter level, BlockPos neighbourPos, Direction directionToNeighbour) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/VineBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"
	))
	BlockState registerDefaultState(BlockState state) {
		return state
			.setValue(BlockStateProperties4.ANA, false)
			.setValue(BlockStateProperties4.KATA, false);
	}

	@Inject(method = "randomTick", cancellable = true, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getClockWise()Lnet/minecraft/core/Direction;"
	))
	void randomTick(
		BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci,
		@Local(name = "testDirection") Direction testDirection,
		@Local(name = "testPos") BlockPos testPos
	) {
		ci.cancel();
		final Direction[] neighborDirections = Direction4.as(testDirection).getHorizontalPerpendiculars();
		final boolean[] sideHasConnectingFace = new boolean[neighborDirections.length];
		final BlockPos[] sideTestPos = new BlockPos[neighborDirections.length];
		for (int i = 0; i < neighborDirections.length; i++) {
			final Direction sideDirection = neighborDirections[i];
			sideHasConnectingFace[i] = state.getValue(getPropertyForFace(sideDirection));
			sideTestPos[i] = testPos.relative(sideDirection);
			if (sideHasConnectingFace[i] && isAcceptableNeighbour(level, sideTestPos[i], sideDirection)) {
				level.setBlock(testPos, this.defaultBlockState().setValue(getPropertyForFace(sideDirection), true), 2);
				return;
			}
		}
		final Direction opposite = testDirection.getOpposite();
		//noinspection ConstantValue
		for (int i = 0; i < neighborDirections.length; i++) {
			if (sideHasConnectingFace[i]
				&& level.isEmptyBlock(sideTestPos[i])
				&& isAcceptableNeighbour(level, pos.relative(neighborDirections[i]), opposite)
			) {
				level.setBlock(sideTestPos[i], this.defaultBlockState().setValue(getPropertyForFace(opposite), true), 2);
				return;
			}
		}
		//noinspection ConstantValue
		if (random.nextFloat() < 0.05 && isAcceptableNeighbour(level, testPos.above(), Direction.UP)) {
			level.setBlock(testPos, this.defaultBlockState().setValue(BlockStateProperties.UP, true), 2);
		}
	}

	@ModifyReturnValue(method = "hasHorizontalConnection", at = @At("RETURN"))
	boolean hasHorizontalConnection(boolean original, @Local(argsOnly = true, name = "state") BlockState state) {
		return original || state.getValue(BlockStateProperties4.ANA) || state.getValue(BlockStateProperties4.KATA);
	}

	@Redirect(method = "canSpread", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosed(IIIIII)Ljava/lang/Iterable;"
	))
	Iterable<BlockPos> canSpread(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = Vec4i.getW(pos);
		return BlockPos4.betweenClosed(minX, minY, minZ, w - 4, maxX, maxY, maxZ, w + 4);
	}

	@Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
	void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(BlockStateProperties4.ANA, BlockStateProperties4.KATA);
	}

	// TODO rotate
	// TODO mirror
}
