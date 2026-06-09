package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.redstone.Orientation4;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Orientation.class)
public class OrientationMixin {
	/**
	 * @author iluha168
	 * @reason TODO This would take 3 mixins to patch, and I am lazy.
	 */
	@Overwrite
	private static Orientation[] lambda$static$0() {
		Orientation4[] orientations = new Orientation4[384];
		Orientation4.generateContext(new Orientation4(Direction.UP, Direction.NORTH, Direction.EAST, Orientation.SideBias.LEFT), orientations);
		return orientations;
	}

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;cross(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i stopCrash(Vec3i frontVector, Vec3i upVector) {
		if (!((Orientation) (Object) this instanceof Orientation4)) {
			throw Err4.container3();
		}
		// The actual constructor is ignored and has fields overridden in Orientation4
		return frontVector;
	}
}
