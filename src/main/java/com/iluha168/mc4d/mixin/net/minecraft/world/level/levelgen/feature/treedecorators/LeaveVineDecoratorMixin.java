package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.treedecorators;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.world.level.block.state.properties.BlockStateProperties4;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeaveVineDecorator.class)
class LeaveVineDecoratorMixin {
	@Shadow
	@Final
	private float probability;

	@Shadow
	private static void addHangingVine(BlockPos pos, BooleanProperty direction, TreeDecorator.Context context) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Inject(method = "lambda$place$0", at = @At("TAIL"))
	private void place(RandomSource random, TreeDecorator.Context context, BlockPos pos, CallbackInfo ci) {
		if (random.nextFloat() < this.probability) {
			final BlockPos kata = ((BlockPos4) pos).kata();
			if (context.isAir(kata)) {
				addHangingVine(kata, BlockStateProperties4.ANA, context);
			}
		}

		if (random.nextFloat() < this.probability) {
			final BlockPos ana = ((BlockPos4) pos).ana();
			if (context.isAir(ana)) {
				addHangingVine(ana, BlockStateProperties4.KATA, context);
			}
		}
	}
}
