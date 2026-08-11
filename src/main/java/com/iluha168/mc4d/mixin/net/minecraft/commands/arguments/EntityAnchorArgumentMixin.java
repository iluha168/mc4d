package com.iluha168.mc4d.mixin.net.minecraft.commands.arguments;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAnchorArgument.class)
class EntityAnchorArgumentMixin {
	@Mixin(EntityAnchorArgument.Anchor.class)
	static class AnchorMixin {
		@Redirect(method = "lambda$static$1", at = @At(
			value = "NEW",
			target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
		))
		private static Vec3 EYES(double x, double y, double z, @Local(argsOnly = true, name = "p") Vec3 p) {
			return new Vec4(x, y, z, ((Vec4) p).w);
		}
	}
}
