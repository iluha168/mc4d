package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Direction4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(DarkOakTrunkPlacer.class)
class DarkOakTrunkPlacerMixin {
	@WrapOperation(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/feature/trunkplacers/DarkOakTrunkPlacer;placeBelowTrunkBlock(Lnet/minecraft/world/level/WorldGenLevel;Ljava/util/function/BiConsumer;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)V"
	))
	private static void placeTrunk_placeBelowTrunkBlock(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random,
		BlockPos pos, TreeConfiguration config, Operation<Void> original
	) {
		original.call(level, trunkSetter, random, pos, config);
		original.call(level, trunkSetter, random, ((BlockPos4) pos).ana(), config);
	}
	@Inject(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;getX()I"
	))
	private void placeTrunk_tw(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, BlockPos origin, TreeConfiguration config, CallbackInfoReturnable<List<FoliagePlacer.FoliageAttachment>> cir,
		@Share("w") LocalIntRef w, @Share("tw") LocalIntRef tw
	) {
		w.set(Vec4i.getW(origin));
		tw.set(w.get());
	}
	@Inject(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getStepX()I"
	))
	private void placeTrunk_tw_leadDirection(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, BlockPos origin, TreeConfiguration config, CallbackInfoReturnable<List<FoliagePlacer.FoliageAttachment>> cir,
		@Local(name = "leanDirection") Direction leanDirection, @Share("tw") LocalIntRef tw
	) {
		tw.set(tw.get() + Direction4.as(leanDirection).getStepW());
	}
	@Definition(id = "BlockPos", type = BlockPos.class)
	@Definition(id = "tx", local = @Local(type = int.class, name = "tx"))
	@Expression("new BlockPos(tx, ?, ?)")
	@ModifyExpressionValue(method = "placeTrunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	private BlockPos placeTrunk_BlockPos(BlockPos original, @Share("tw") LocalIntRef tw) {
		Vec4i.setW(original, tw.get());
		return original;
	}
	@Definition(id = "placeLog", method = "Lnet/minecraft/world/level/levelgen/feature/trunkplacers/DarkOakTrunkPlacer;placeLog(Lnet/minecraft/world/level/WorldGenLevel;Ljava/util/function/BiConsumer;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)Z")
	@Definition(id = "blockPos", local = @Local(type = BlockPos.class, name = "blockPos"))
	@Expression({ // I wish this had a NOT operator of some kind...
		"?.placeLog(?, ?, ?, blockPos, ?)",
		"?.placeLog(?, ?, ?, blockPos.?(), ?)",
		"?.placeLog(?, ?, ?, blockPos.?().?(), ?)"
	})
	@WrapOperation(method = "placeTrunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean placeTrunk_placeLog(DarkOakTrunkPlacer This, WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos pos, TreeConfiguration config, Operation<Boolean> original) {
		final boolean placeKata = original.call(This, level, trunkSetter, random, pos, config);
		final boolean placeAna = original.call(This, level, trunkSetter, random, ((BlockPos4) pos).ana(), config);
		return placeKata && placeAna;
	}
	@Definition(id = "oz", local = @Local(type = int.class, name = "oz"))
	@Expression("oz = @(-1)")
	@ModifyExpressionValue(method = "placeTrunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int placeTrunk_ow(int minusOne, @Share("ow") LocalIntRef ow) {
		ow.set(minusOne);
		return minusOne;
	}
	// This does apply properly, IDE is lying. Hold on, what?
	@Definition(id = "oz", local = @Local(type = int.class, name = "oz"))
	@Expression("? = oz + @(1)")
	@ModifyExpressionValue(method = "placeTrunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int placeTrunk_incrementOw(int one, @Share("ow") LocalIntRef ow) {
		ow.set(ow.get() + 1);
		if (ow.get() <= 2) return 0;
		ow.set(-1);
		return 1;
	}
	@Definition(id = "oz", local = @Local(type = int.class, name = "oz"))
	@Expression("oz > 1")
	@ModifyExpressionValue(method = "placeTrunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean placeTrunk_outsideTrunk(boolean original, @Share("ow") LocalIntRef ow) {
		return original || ow.get() < 0 || ow.get() > 1;
	}
	@Definition(id = "BlockPos", type = BlockPos.class)
	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Definition(id = "ox", local = @Local(type = int.class, name = "ox"))
	@Expression("new BlockPos(x + ox, ?, ?)")
	@ModifyExpressionValue(method = "placeTrunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	private BlockPos placeTrunk_BlockPos(BlockPos original, @Share("w") LocalIntRef w, @Share("ow") LocalIntRef ow) {
		Vec4i.setW(original, w.get() + ow.get());
		return original;
	}
}
