package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.treedecorators;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlaceOnGroundDecorator.class)
class PlaceOnGroundDecoratorMixin {
	@Definition(id = "origin", local = @Local(type = BlockPos.class, name = "origin"))
	@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
	@Expression("origin.getZ()")
	@Inject(method = "place", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private void place_minW_maxW(
		TreeDecorator.Context context, CallbackInfo ci,
		@Local(name = "origin") BlockPos origin,
		@Share("minW") LocalIntRef minW,
		@Share("maxW") LocalIntRef maxW
	) {
		final int w = Vec4i.getW(origin);
		minW.set(w);
		maxW.set(w);
	}
	@Definition(id = "position", local = @Local(type = BlockPos.class, name = "position"))
	@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
	@Expression("position.getZ()")
	@Inject(method = "place", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private void place_expand(
		TreeDecorator.Context context, CallbackInfo ci,
		@Local(name = "position") BlockPos position,
		@Share("minW") LocalIntRef minW,
		@Share("maxW") LocalIntRef maxW
	) {
		final int w = Vec4i.getW(position);
		minW.set(Math.min(minW.get(), w));
		maxW.set(Math.max(maxW.get(), w));
	}
	@ModifyExpressionValue(method = "place", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private BoundingBox place_BoundingBox(BoundingBox bb, @Share("minW") LocalIntRef minW, @Share("maxW") LocalIntRef maxW) {
		final BoundingBox4 bb4 = (BoundingBox4) bb;
		bb4.setMinW(minW.get());
		bb4.setMaxW(maxW.get());
		return bb;
	}
	@Redirect(method = "place", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;inflatedBy(III)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private BoundingBox place_inflatedBy(BoundingBox instance, int inflateX, int inflateY, int inflateZ) {
		return ((BoundingBox4) instance).inflatedBy(inflateX, inflateY, inflateZ, inflateZ);
	}
	@ModifyExpressionValue(method = "place", at = @At(
		value = "FIELD",
		opcode = Opcodes.GETFIELD,
		target = "Lnet/minecraft/world/level/levelgen/feature/treedecorators/PlaceOnGroundDecorator;tries:I"
	))
	private int place_tries(int tries, @Local(name = "bb") BoundingBox bb) {
		// Looks unfair at first, but I am just trying to preserve the density.
		return tries * ((BoundingBox4) bb).getWSpan();
	}
	@Redirect(method = "place", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos place_set(
		BlockPos.MutableBlockPos instance, int x, int y, int z,
		@Local(name = "random") RandomSource random,
		@Local(name = "bb") BoundingBox bb
	) {
		final BoundingBox4 bb4 = (BoundingBox4) bb;
		return ((BlockPos4.MutableBlockPos) instance).set(
			x, y, z,
			random.nextIntBetweenInclusive(bb4.minW(), bb4.maxW())
		);
	}
}
