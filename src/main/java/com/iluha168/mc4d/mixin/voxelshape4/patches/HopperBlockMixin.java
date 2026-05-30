package com.iluha168.mc4d.mixin.voxelshape4.patches;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlock.class)
public class HopperBlockMixin {
	@Redirect(method = "makeShapes", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 makeShapes(double x, double y, double z) {
		return new Vec4(x, y, z, z);
	}
}
