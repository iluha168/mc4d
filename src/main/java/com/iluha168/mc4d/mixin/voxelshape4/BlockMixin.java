package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.world.level.block.Block4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class BlockMixin {
	@Redirect(method = "cube(D)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;cube(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape tesseract(double sizeX, double sizeY, double sizeZ) {
		return Block4.cube(sizeX, sizeY, sizeZ, sizeZ);
	}

	@WrapMethod(method = "cube(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;")
	private static VoxelShape cube(double sizeX, double sizeY, double sizeZ, Operation<VoxelShape> original) {
		if (sizeX == sizeY) return Block4.cube(sizeX, sizeY, sizeZ, sizeX);
		return original.call(sizeX, sizeY, sizeZ);
	}

	@Redirect(method = "column(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape columnXZW(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, sizeZ, minY, maxY);
	}

	@Redirect(method = "boxZ(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;boxZ(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape boxZ_XYW(double sizeX, double sizeY, double minZ, double maxZ) {
		return Block4.boxZ(sizeX, sizeY, minZ, maxZ, sizeX);
	}

	@Redirect(method = "boxZ(DDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape boxZ_XW(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "sizeX") double sizeX
	) {
		double halfW = sizeX / 2.0;
		return Block4.box(
			minX, minY, minZ, 8 - halfW,
			maxX, maxY, maxZ, 8 + halfW
		);
	}

	@Redirect(method = "updateEntityMovementAfterFallOn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 updateEntityMovementAfterFallOn(Vec3 movement, double xScale, double yScale, double zScale) {
		return ((Vec4) movement).multiply(xScale, yScale, zScale, zScale);
	}

	// TODO everything else
}
