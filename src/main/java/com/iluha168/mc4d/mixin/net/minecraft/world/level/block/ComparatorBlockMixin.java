package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComparatorBlock.class)
class ComparatorBlockMixin extends BlockMixin {
	@Redirect(method = "getItemFrame", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB getItemFrame(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "tPos") BlockPos tPos
	) {
		final int tPosW = Vec4i.getW(tPos);
		return new AABB4(
			minX, minY, minZ, tPosW,
			maxX, maxY, maxZ, tPosW + 1
		);
	}
}
