package com.iluha168.mc4d.world.level.block;

import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Block4 {
	public static VoxelShape cube(double sizeX, double sizeY, double sizeZ, double sizeW) {
		double halfY = sizeY / 2.0;
		return Block4.column(sizeX, sizeZ, sizeW, 8.0 - halfY, 8.0 + halfY);
	}

	public static VoxelShape box(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW
	) {
		return Shapes4.box(
			minX / 16.0, minY / 16.0, minZ / 16.0, minW / 16.0,
			maxX / 16.0, maxY / 16.0, maxZ / 16.0, maxW / 16.0
		);
	}

	public static VoxelShape column(double sizeX, double sizeZ, double sizeW, double minY, double maxY) {
		double halfX = sizeX / 2.0;
		double halfZ = sizeZ / 2.0;
		double halfW = sizeW / 2.0;
		return Block4.box(
			8.0 - halfX, minY, 8.0 - halfZ, 8.0 - halfW,
			8.0 + halfX, maxY, 8.0 + halfZ, 8.0 + halfW
		);
	}

	public static VoxelShape boxZ(double sizeX, double sizeY, double minZ, double maxZ, double sizeW) {
		double halfW = sizeW / 2.0;
		return Block4.boxZ(sizeX, sizeY, minZ, maxZ, 8.0 - halfW, 8.0 + halfW);
	}

	public static VoxelShape boxZ(double sizeX, double sizeY, double minZ, double maxZ, double minW, double maxW) {
		double halfY = sizeY / 2.0;
		return Block4.boxZ(sizeX, 8.0 - halfY, 8.0 + halfY, minZ, maxZ, minW, maxW);
	}

	public static VoxelShape boxZ(double sizeX, double minY, double maxY, double minZ, double maxZ, double minW, double maxW) {
		double halfX = sizeX / 2.0;
		return Block4.box(
			8.0 - halfX, minY, minZ, minW,
			8.0 + halfX, maxY, maxZ, maxW
		);
	}
}
