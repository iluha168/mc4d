package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Vec3.class)
class Vec3Mixin {
	@WrapMethod(method = "applyLocalCoordinatesToRotation")
	private static Vec3 applyLocalCoordinatesToRotation(Vec2 rotation, Vec3 direction, Operation<Vec3> original) {
		return direction instanceof Vec4 direction4
			? Vec4.applyLocalCoordinatesToRotation(rotation, direction4)
			: original.call(rotation, direction);
	}
}
