package com.iluha168.mc4d.mixin.net.minecraft.server.chase;

import com.iluha168.mc4d.world.phys.RotationVec;
import net.minecraft.server.chase.ChaseClient;
import net.minecraft.world.phys.Vec2;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChaseClient.class)
class ChaseClientMixin {
	// TODO handleTeleport
	// TODO parseTarget

	@Redirect(method = "lambda$executeCommand$0", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;ZERO:Lnet/minecraft/world/phys/Vec2;",
		opcode = Opcodes.GETSTATIC
	))
	Vec2 executeCommand() {
		return RotationVec.ZERO;
	}
}
