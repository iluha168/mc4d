package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.level.block.Block4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(Block.class)
public class BlockMixin implements Block4 {
	@Shadow
	public BlockState defaultBlockState() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Redirect(method = "pushEntitiesUp", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB pushEntitiesUp_move(AABB instance, double xa, double ya, double za) {
		return ((AABB4) instance).move(xa, ya, za, za);
	}
	@Redirect(method = "pushEntitiesUp", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;teleportRelative(DDD)V"
	))
	private static void pushEntitiesUp_teleportRelative(Entity instance, double dx, double dy, double dz) {
		((Entity4) instance).teleportRelative(dx, dy, dz, dz);
	}

	@Overwrite
	@Deprecated
	public static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		throw Err4.arguments3("Block4#box");
	}

	@Redirect(method = "cube(D)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;cube(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape cube_equal(double sizeX, double sizeY, double sizeZ) {
		return Block4.cube(sizeX, sizeY, sizeZ, sizeZ);
	}

	@Overwrite
	@Deprecated
	public static VoxelShape cube(double sizeX, double sizeY, double sizeZ) {
		throw Err4.arguments3("Block4#cube");
	}

	@Redirect(method = "column(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape column_XZW(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, sizeZ, minY, maxY);
	}

	@Overwrite
	@Deprecated
	public static VoxelShape column(double sizeX, double sizeZ, double minY, double maxY) {
		throw Err4.arguments3("Block4#column");
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

	@ModifyArg(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", index = 1, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V"
	))
	private static Supplier<ItemEntity> popResource(
		Supplier<ItemEntity> entityFactory,
		@Local(argsOnly = true, name = "level") Level level,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "itemStack") ItemStack itemStack,
		@Local(name = "random") RandomSource random,
		@Local(name = "x") double x,
		@Local(name = "y") double y,
		@Local(name = "z") double z
	) {
		final double w = Vec4i.getW(pos) + 0.5 + Mth.nextDouble(random, -0.25, 0.25);
		return () -> ItemEntity4.from(level, x, y, z, w, itemStack);
	}

	@ModifyArg(method = "popResourceFromFace", index = 1, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V"
	))
	private static Supplier<ItemEntity> popResourceFromFace(
		Supplier<ItemEntity> entityFactory,
		@Local(argsOnly = true, name = "level") Level level,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "face") Direction face,
		@Local(argsOnly = true, name = "itemStack") ItemStack itemStack,
		@Local(name = "halfWidth") double halfWidth,
		@Local(name = "random") RandomSource random,
		@Local(name = "x") double x,
		@Local(name = "y") double y,
		@Local(name = "z") double z,
		@Local(name = "deltaX") double deltaX,
		@Local(name = "deltaY") double deltaY,
		@Local(name = "deltaZ") double deltaZ
	) {
		final int stepW = Direction4.as(face).getStepW();
		final double w = Vec4i.getW(pos) + 0.5 + (stepW == 0 ? Mth.nextDouble(random, -0.25, 0.25) : stepW * (0.5 + halfWidth));
		final double deltaW = stepW == 0 ? Mth.nextDouble(random, -0.1, 0.1) : stepW * 0.1;
		return () -> ItemEntity4.from(level, x, y, z, w, itemStack, deltaX, deltaY, deltaZ, deltaW);
	}

	@Redirect(method = "updateEntityMovementAfterFallOn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 updateEntityMovementAfterFallOn(Vec3 movement, double xScale, double yScale, double zScale) {
		return ((Vec4) movement).multiply(xScale, yScale, zScale, zScale);
	}
}
