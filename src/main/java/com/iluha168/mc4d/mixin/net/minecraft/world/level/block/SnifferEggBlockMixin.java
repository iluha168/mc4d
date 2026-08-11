package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.block.Block4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.block.SnifferEggBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnifferEggBlock.class)
class SnifferEggBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, sizeX, minY, maxY);
	}

	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/animal/sniffer/Sniffer;snapTo(DDDFF)V"
	))
	void tick(
		Sniffer sniffer, double x, double y, double z, float yRot, float xRot,
		@Local(argsOnly = true, name = "level") ServerLevel level,
		@Local(name = "spawnAt") Vec3 spawnAt
	) {
		final Entity4 sniffer4 = (Entity4) sniffer;
		sniffer4.snapTo(
			x, y, z,
			((Vec4) spawnAt).w,
			yRot, xRot,
			RotationVec.randomWRotDeg(level.getRandom()),
			0.0F
		);
	}
}
