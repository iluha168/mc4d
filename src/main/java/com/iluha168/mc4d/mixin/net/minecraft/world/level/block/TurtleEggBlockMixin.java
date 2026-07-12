package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.block.Block4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TurtleEggBlock.class)
class TurtleEggBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE_SINGLE(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return Block4.box(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	@Redirect(method = "randomTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/animal/turtle/Turtle;snapTo(DDDFF)V"
	))
	void randomTick_snapTo(Turtle instance, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		instance.snapTo(new Vec4(x, y, z, Vec4i.getW(pos) + 0.3), yRot, xRot);
	}
}
