package com.iluha168.mc4d.mixin.net.minecraft.server.dedicated;

import com.iluha168.mc4d.world.level.Level4;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DedicatedServerProperties.class)
class DedicatedServerPropertiesMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 29999984))
	private static int maxWorldSize(int constant) {
		return Level4.MAX_LEVEL_SIZE - 16;
	}
	@ModifyConstant(method = "lambda$new$0", constant = @Constant(intValue = 29999984))
	private static int maxWorldSize_lambda(int constant) {
		return Level4.MAX_LEVEL_SIZE - 16;
	}
}
