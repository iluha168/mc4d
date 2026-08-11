package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAttachment.class)
class EntityAttachmentMixin {
	@Mixin(EntityAttachment.Fallback.class)
	interface FallbackMixin {
		@Redirect(method = {"lambda$static$1", "lambda$static$2"}, at = @At(
			value = "NEW",
			target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
		))
		private static Vec3 AT_HEIGHT_AT_CENTER(double x, double y, double z) {
			return new Vec4(x, y, z, z);
		}
	}
}
