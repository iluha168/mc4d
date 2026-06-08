package com.iluha168.mc4d.mixin.commands4.patches;

import com.iluha168.mc4d.world.level.Level4;
import net.minecraft.server.commands.ForceLoadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ForceLoadCommand.class)
class ForceLoadCommandMixin {
	@ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = 30000000))
	private static int changeForceLoad(int value) {
		return Level4.MAX_LEVEL_SIZE;
	}
	@ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = -30000000))
	private static int changeForceLoad_(int value) {
		return -Level4.MAX_LEVEL_SIZE;
	}
}
