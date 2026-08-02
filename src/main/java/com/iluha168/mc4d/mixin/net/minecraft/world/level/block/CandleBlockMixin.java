package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.Block4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CandleBlock.class)
public class CandleBlockMixin {
	@Redirect(method = "lambda$static$1", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 PARTICLE_OFFSETS(double x, double y, double z) {
		return new Vec4(x, y, z, x);
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return Block4.box(minX, minY, minZ, minX, maxX, maxY, maxZ, maxX);
	}
}
