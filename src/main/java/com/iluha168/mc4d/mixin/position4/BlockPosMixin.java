package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPos.class)
public class BlockPosMixin implements BlockPos4 {
	// TODO everything else

	@Redirect(method = "betweenCornersInDirection(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/phys/Vec3;)Ljava/lang/Iterable;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenCornersInDirection(IIIIIILnet/minecraft/world/phys/Vec3;)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> betweenCornersInDirection_fromAABB(
		int firstCornerX, int firstCornerY, int firstCornerZ,
		int secondCornerX, int secondCornerY, int secondCornerZ,
		Vec3 direction,
		@Local(name = "minCorner") Vec3 minCorner,
		@Local(name = "maxCorner") Vec3 maxCorner
	) {
		int firstCornerW = Mth.floor(((Position4) minCorner).w());
		int secondCornerW = Mth.floor(((Position4) maxCorner).w());
		return BlockPos4.betweenCornersInDirection(
			firstCornerX, firstCornerY, firstCornerZ, firstCornerW,
			secondCornerX, secondCornerY, secondCornerZ, secondCornerW,
			(Vec4) direction
		);
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public static Iterable<BlockPos> betweenCornersInDirection(
		int firstCornerX, int firstCornerY, int firstCornerZ,
		int secondCornerX, int secondCornerY, int secondCornerZ,
		Vec3 direction
	) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BlockPos4.betweenCornersInDirection instead."));
	}
}
