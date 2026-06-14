package com.iluha168.mc4d.mixin.net.minecraft.world.phys;

import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.IAABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

	@Inject(method = "<init>(Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"))
	private static void init_blockPos(BlockPos pos, CallbackInfo ci) {
		throw Err4.return3("AABB4#new");
	}

	@Redirect(method = "of", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB of(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(argsOnly = true, name = "box") BoundingBox box) {
		BoundingBox4 bb4 = (BoundingBox4) box;
		return new AABB4(
			minX, minY, minZ, bb4.minW(),
			maxX, maxY, maxZ, bb4.maxW() + 1
		);
	}

	@WrapMethod(method = "unitCubeFromLowerCorner")
	private static AABB unitCubeFromLowerCorner(Vec3 pos, Operation<AABB> original) {
		return pos instanceof Vec4 pos4 ? new AABB4(
			pos.x, pos.y, pos.z, pos4.w,
			pos.x + 1, pos.y + 1, pos.z + 1, pos4.w + 1
		) : original.call(pos);
	}

	@Redirect(method = "encapsulatingFullBlocks", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB encapsulatingFullBlocks(
		double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "pos0") BlockPos pos0,
		@Local(argsOnly = true, name = "pos1") BlockPos pos1
	) {
		final int pos0w = Vec4i.getW(pos0);
		final int pos1w = Vec4i.getW(pos1);
		return new AABB4(
			minX, minY, minZ, Math.min(pos0w, pos1w),
			maxX, maxY, maxZ, Math.max(pos0w, pos1w) + 1
		);
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
