package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DispenserBlock.class)
class DispenserBlockMixin extends BlockMixin {
	@Redirect(method = "getDispensePosition(Lnet/minecraft/core/dispenser/BlockSource;DLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/core/Position;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 getDispensePosition(
		Vec3 instance, double x, double y, double z,
		@Local(argsOnly = true, name = "scale") double scale,
		@Local(argsOnly = true, name = "offset") Vec3 offset,
		@Local(name = "direction") Direction direction
	) {
		return ((Vec4) instance).add(x, y, z, scale * Direction4.as(direction).getStepW() + ((Vec4) offset).w);
	}
}
