package com.iluha168.mc4d.mixin.position4;

import net.minecraft.core.Direction;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Orientation.class)
public class OrientationMixin {
	@Redirect(method = "generateContext", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	private static Direction[] DIRECTION_values() {
		// TODO: remove the method and make Orientation actually 4D
		return new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
	}
}
