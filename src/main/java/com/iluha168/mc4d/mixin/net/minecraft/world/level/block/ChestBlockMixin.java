package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChestBlock.class)
class ChestBlockMixin {
	@Mixin(targets = "net.minecraft.world.level.block.ChestBlock$2$1")
	static class MenuProviderCombinerMixin {
		@Redirect(method = "createMenu", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
		))
		Vec3 createMenu(Vec3 firstCenter, double x, double y, double z, @Local(name = "connectedDirection") Direction connectedDirection) {
			return ((Vec4) firstCenter).add(x, y, z, Direction4.as(connectedDirection).getStepW() / 2.0);
		}
	}

	@Redirect(method = "isCatSittingOnChest", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB isCatSittingOnChest(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = Vec4i.getW(pos);
		return new AABB4(minX, minY, minZ, w, maxX, maxY, maxZ, w + 1);
	}
}
