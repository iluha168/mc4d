package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WebBlock.class)
class WebBlockMixin {
	@Redirect(method = "entityInside", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 entityInside(double x, double y, double z) {
		return new Vec4(x, y, z, z);
	}
}
