package com.iluha168.mc4d.mixin.net.minecraft.commands;

import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Commands.class)
class CommandsMixin {
	@Redirect(method = "createCompilationContext", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec3 createCompilationContext_position() {
		return Vec4.ZERO;
	}
	@Redirect(method = "createCompilationContext", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;ZERO:Lnet/minecraft/world/phys/Vec2;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec2 createCompilationContext_rotation() {
		return RotationVec.ZERO;
	}
}
