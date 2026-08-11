package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CandleCakeBlock.class)
class CandleCakeBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 PARTICLE_OFFSETS(double x, double y, double z) {
		return new Vec4(x, y, z, z);
	}
}
