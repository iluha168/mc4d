package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.block.entity.Hopper4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlockEntity.class)
class HopperBlockEntityMixin extends BlockEntityMixin implements Hopper4 {
	@Redirect(method = "suckInItems", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos suckInItems(double x, double y, double z, @Local(argsOnly = true, name = "hopper") Hopper hopper) {
		return BlockPos4.containing(x, y, z, ((Hopper4) hopper).getLevelW());
	}

	// TODO getSourceContainer
	// TODO getItemsAtAndAbove
	// TODO getContainerAt
	// TODO getContainerAt
	// TODO getEntityContainer
	// TODO getSourceContainerOrHandler
	// TODO getContainerOrHandlerAt
	// TODO getContainerOrHandlerAt

	@Override
	public double getLevelW() {
		return Vec4i.getW(this.worldPosition) + 0.5;
	}

	// TODO entityInside
}
