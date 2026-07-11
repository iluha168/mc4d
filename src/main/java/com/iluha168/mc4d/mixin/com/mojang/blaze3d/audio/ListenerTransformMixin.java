package com.iluha168.mc4d.mixin.com.mojang.blaze3d.audio;

import com.iluha168.mc4d.world.phys.Vec4;
import com.mojang.blaze3d.audio.ListenerTransform;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ListenerTransform.class)
class ListenerTransformMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec3 INITIAL() {
		return Vec4.ZERO;
	}
}
