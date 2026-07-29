package com.iluha168.mc4d.mixin.net.minecraft.server.rcon;

import com.iluha168.mc4d.world.phys.RotationVec;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.phys.Vec2;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RconConsoleSource.class)
class RconConsoleSourceMixin {
	@Redirect(method = "createCommandSourceStack", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;ZERO:Lnet/minecraft/world/phys/Vec2;",
		opcode = Opcodes.GETSTATIC
	))
	Vec2 createCommandSourceStack() {
		return RotationVec.ZERO;
	}
}
