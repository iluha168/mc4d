package com.iluha168.mc4d.mixin.net.minecraft.commands.arguments.coordinates;

import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;

@Mixin(Vec3Argument.class)
class Vec3ArgumentMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Ljava/util/Arrays;asList([Ljava/lang/Object;)Ljava/util/List;"
	))
	private static <T> List<String> EXAMPLES(T[] a) {
		return Arrays.asList("0 0 0 0", "~ ~ ~ ~", "^ ^ ^ ^", "^1 ^ ^-5 ^0.5", "0.1 -0.5 .9 -.2", "~0.5 ~1 ~-5 ~-.2");
	}

	// TODO ERROR_NOT_COMPLETE
}
