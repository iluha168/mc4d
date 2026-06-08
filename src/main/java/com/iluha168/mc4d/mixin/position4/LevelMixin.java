package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Level.class)
class LevelMixin implements Level4 {
	@ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = 30000000))
	private static int isInWorldBoundsHorizontal(int value) {
		return Level4.MAX_LEVEL_SIZE;
	}
	@ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = -30000000))
	private static int isInWorldBoundsHorizontal_(int value) {
		return -Level4.MAX_LEVEL_SIZE;
	}
	@WrapMethod(method = "isInWorldBoundsHorizontal")
	private static boolean isInWorldBoundsHorizontal4(BlockPos pos, Operation<Boolean> original) {
		final int w = Vec4i.getW(pos);
		return original.call(pos) && w >= -Level4.MAX_LEVEL_SIZE && w < Level4.MAX_LEVEL_SIZE;
	}

	// TODO the rest
}
