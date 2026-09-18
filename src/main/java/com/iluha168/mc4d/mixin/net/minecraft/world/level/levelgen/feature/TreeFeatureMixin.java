package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.structure.BoundingBox4;
import com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.shapes.DiscreteVoxelShape4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TreeFeature.class)
class TreeFeatureMixin {
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "r", local = @Local(type = int.class, name = "r"))
	@Expression("z = @(-r)")
	@ModifyExpressionValue(method = "getMaxFreeTreeHeight", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int getMaxFreeTreeHeight_w(int minusR, @Share("w") LocalIntRef w) {
		w.set(minusR);
		return minusR;
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "getMaxFreeTreeHeight", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int getMaxFreeTreeHeight_incrementW(int one, @Share("w") LocalIntRef w, @Local(name = "r") int r) {
		final int wInc = w.get() + 1;
		if (wInc <= r) {
			w.set(wInc);
			return 0;
		}
		w.set(-r);
		return 1;
	}
	@Redirect(method = "getMaxFreeTreeHeight", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos getMaxFreeTreeHeight_posW(BlockPos.MutableBlockPos blockPos, Vec3i pos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) blockPos).setWithOffset(pos, x, y, z, w.get());
	}

	@Redirect(method = "lambda$place$4", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;updateShapeAtEdge(Lnet/minecraft/world/level/LevelAccessor;ILnet/minecraft/world/phys/shapes/DiscreteVoxelShape;III)V"
	))
	private static void place(LevelAccessor level, int updateMode, DiscreteVoxelShape shape, int startX, int startY, int startZ, @Local(argsOnly = true, name = "bounds") BoundingBox bounds) {
		StructureTemplate4.updateShapeAtEdge(level, updateMode, shape, startX, startY, startZ, ((BoundingBox4) bounds).minW());
	}

	@Redirect(method = "updateLeaves", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/world/phys/shapes/BitSetDiscreteVoxelShape;"
	))
	private static BitSetDiscreteVoxelShape updateLeaves_shape(
		int xSize, int ySize, int zSize,
		@Local(argsOnly = true, name = "bounds") BoundingBox bounds,
		@Share("shape") LocalRef<DiscreteVoxelShape4> shape
	) {
		shape.set(new BitSetDiscreteVoxelShape4(xSize, ySize, zSize, ((BoundingBox4) bounds).getWSpan()));
		return BitSetDiscreteVoxelShape4.__unsafe_new3D(0, 0, 0);
	}
	@Definition(id = "fill", method = "Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;fill(III)V")
	@Definition(id = "pos", local = @Local(type = BlockPos.class, name = "pos"))
	@Expression("?.fill(?, pos.?() - ?, ?)")
	@Redirect(method = "updateLeaves", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void updateLeaves_fill(
		DiscreteVoxelShape instance, int x, int y, int z,
		@Share("shape") LocalRef<DiscreteVoxelShape4> shape,
		@Local(name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "bounds") BoundingBox bounds
	) {
		shape.get().fill(x, y, z, Vec4i.getW(pos) - ((BoundingBox4) bounds).minW());
	}
	@ModifyReturnValue(method = "updateLeaves", at = @At("RETURN"))
	private static DiscreteVoxelShape updateLeaves_return(DiscreteVoxelShape original, @Share("shape") LocalRef<DiscreteVoxelShape4> shape) {
		return shape.get();
	}
	@Definition(id = "fill", method = "Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;fill(III)V")
	@Definition(id = "posx", local = @Local(type = BlockPos.class, name = "posx"))
	@Expression("?.fill(?, posx.?() - ?, ?)")
	@Redirect(method = "updateLeaves", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void updateLeaves_fillx(
		DiscreteVoxelShape instance, int x, int y, int z,
		@Share("shape") LocalRef<DiscreteVoxelShape4> shape,
		@Local(name = "posx") BlockPos posx,
		@Local(argsOnly = true, name = "bounds") BoundingBox bounds
	) {
		shape.get().fill(x, y, z, Vec4i.getW(posx) - ((BoundingBox4) bounds).minW());
	}
	@Redirect(method = "updateLeaves", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;isFull(III)Z"
	))
	private static boolean updateLeaves_isFull(
		DiscreteVoxelShape instance, int x, int y, int z,
		@Share("shape") LocalRef<DiscreteVoxelShape4> shape,
		@Local(name = "neighborPos") BlockPos.MutableBlockPos neighborPos,
		@Local(argsOnly = true, name = "bounds") BoundingBox bounds
	) {
		final int wInShape = Vec4i.getW(neighborPos) - ((BoundingBox4) bounds).minW();
		return shape.get().isFull(x, y, z, wInShape);
	}
}
