package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.piston;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonMath.class)
class PistonMathMixin {
	@Inject(method = "getMovementArea", cancellable = true, at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/level/block/piston/PistonMath$1;$SwitchMap$net$minecraft$core$Direction:[I",
		opcode = Opcodes.GETSTATIC
	))
	private static void getMovementArea_switch(
		AABB aabb, Direction direction, double amount, CallbackInfoReturnable<AABB> cir,
		@Local(name = "min") double min,
		@Local(name = "max") double max
	) {
		if (direction.getAxis() != Direction4.Axis.W)
			return;
		final AABB4 aabb4 = (AABB4) aabb;
		final double w = direction == Direction4.KATA ? aabb4.minW : aabb4.maxW;
		cir.setReturnValue(new AABB4(
			aabb.minX, aabb.minY, aabb.minZ, w + min,
			aabb.maxX, aabb.maxY, aabb.maxZ, w + max
		));
	}
	@Redirect(method = "getMovementArea", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getMovementArea_3(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "aabb") AABB aabb
	) {
		final AABB4 aabb4 = (AABB4) aabb;
		return new AABB4(minX, minY, minZ, aabb4.minW, maxX, maxY, maxZ, aabb4.maxW);
	}
}