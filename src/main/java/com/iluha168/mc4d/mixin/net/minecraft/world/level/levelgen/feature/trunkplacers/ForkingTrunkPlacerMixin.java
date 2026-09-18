package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Direction4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(ForkingTrunkPlacer.class)
class ForkingTrunkPlacerMixin {
	@Inject(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;getX()I"
	))
	private void placeTrunk_tw(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, BlockPos origin, TreeConfiguration config, CallbackInfoReturnable<List<FoliagePlacer.FoliageAttachment>> cir,
		@Share("tw") LocalIntRef tw
	) {
		tw.set(Vec4i.getW(origin));
	}
	@ModifyReceiver(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getStepX()I"
	))
	private Direction placeTrunk_getStepW(Direction direction, @Share("tw") LocalIntRef tw) {
		tw.set(tw.get() + Direction4.as(direction).getStepW());
		return direction;
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos placeTrunk_logPos(BlockPos.MutableBlockPos logPos, int x, int y, int z, @Share("tw") LocalIntRef tw) {
		return ((BlockPos4.MutableBlockPos) logPos).set(x, y, z, tw.get());
	}
	@ModifyExpressionValue(method = "placeTrunk", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos placeTrunk_BlockPos(BlockPos original, @Share("tw") LocalIntRef tw) {
		Vec4i.setW(original, tw.get());
		return original;
	}
}
