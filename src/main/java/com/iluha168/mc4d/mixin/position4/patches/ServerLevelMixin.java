package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.core.Vec4i;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
class ServerLevelMixin {
	@Shadow
	@Final
	public static BlockPos END_SPAWN_POINT;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void staticFixes(CallbackInfo ci) {
		Vec4i.setW(END_SPAWN_POINT, END_SPAWN_POINT.getZ());
	}
}
