package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.level.block.Block4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FenceGateBlock.class)
public abstract class FenceGateBlockMixin {
	@Shadow
	protected abstract boolean isWall(BlockState state);

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;cube(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(double sizeX, double sizeY, double sizeZ) {
		return Block4.cube(sizeX, sizeY, sizeZ, sizeX);
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE_COLLISION_SUPPORT(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, sizeX, minY, maxY);
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE_OCCLUSION(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ
	) {
		return Block4.box(
			minX, minY, minZ, minZ,
			maxX, maxY, maxZ, maxZ
		);
	}

	@Definition(id = "setValue", method = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;")
	@Definition(id = "IN_WALL", field = "Lnet/minecraft/world/level/block/FenceGateBlock;IN_WALL:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;")
	@Expression("?.setValue(IN_WALL, @(?))")
	@ModifyExpressionValue(method = "getStateForPlacement", at = @At("MIXINEXTRAS:EXPRESSION")) // ModifyArg is wonky with generics
	boolean getStateForPlacement(
		boolean original,
		@Local(name = "level") Level level,
		@Local(name = "pos") BlockPos pos,
		@Local(name = "axis") Direction.Axis axis
	) {
		BlockPos4 pos4 = (BlockPos4) pos;
		return original || axis == Direction4.Axis.W && (this.isWall(level.getBlockState(pos4.kata())) || this.isWall(level.getBlockState(pos4.ana())));
	}
}
