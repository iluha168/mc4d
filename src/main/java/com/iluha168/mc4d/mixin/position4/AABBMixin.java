package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.IAABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.iluha168.mc4d.world.phys.AABB4.EPSILON;

@Mixin(AABB.class)
public class AABBMixin implements IAABB4 {
	@Override
	public double minW() {
		return 0;
	}

	@Override
	public double maxW() {
		return EPSILON;
	}

	@Definition(id = "to", local = @Local(type = Vec3.class, name = "to", argsOnly = true))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Definition(id = "from", local = @Local(type = Vec3.class, name = "from", argsOnly = true))
	@Expression("to.z - from.z")
	@Inject(method = "clip(Ljava/lang/Iterable;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void clip(
		Iterable<AABB> aabBs, Vec3 from, Vec3 _to, BlockPos pos, CallbackInfoReturnable<BlockHitResult> cir,
		@Share("dw") LocalDoubleRef dw
	) {
		// double dw = to.w - from.w;
		dw.set(((Position4) _to).w() - ((Position4) from).w());
	}

	@Redirect(method = "clip(Ljava/lang/Iterable;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;getDirection(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/phys/Vec3;[DLnet/minecraft/core/Direction;DDD)Lnet/minecraft/core/Direction;"
	))
	private static Direction clip_getDirection(
		AABB aabb, Vec3 from, double[] scaleReference, Direction direction,
		double dx, double dy, double dz, @Share("dw") LocalDoubleRef dw
	) {
		IAABB4 aabb4 = (IAABB4) aabb;
		return AABB4.getDirection(
			aabb.minX, aabb.minY, aabb.minZ, aabb4.minW(),
			aabb.maxX, aabb.maxY, aabb.maxZ, aabb4.maxW(),
			from,
			scaleReference,
			direction,
			dx, dy, dz, dw.get()
		);
	}

	@Redirect(method = "clip(Ljava/lang/Iterable;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 clip_fromAdd(
		Vec3 from, double x, double y, double z, @Share("dw") LocalDoubleRef dw,
		@Local(name = "scale") double scale
	) {
		return ((Vec4) from).add(x, y, z, scale * dw.get());
	}
}
