package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.treedecorators;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.world.level.block.state.properties.BlockStateProperties4;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrunkVineDecorator.class)
class TrunkVineDecoratorMixin {
	@Inject(method = "lambda$place$0", at = @At("TAIL"))
	private static void place(RandomSource random, TreeDecorator.Context context, BlockPos pos, CallbackInfo ci) {
		if (random.nextInt(3) > 0) {
			final BlockPos kata = ((BlockPos4) pos).kata();
			if (context.isAir(kata)) {
				context.placeVine(kata, BlockStateProperties4.ANA);
			}
		}

		if (random.nextInt(3) > 0) {
			final BlockPos ana = ((BlockPos4) pos).ana();
			if (context.isAir(ana)) {
				context.placeVine(ana, BlockStateProperties4.KATA);
			}
		}
	}
}
