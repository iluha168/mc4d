package com.iluha168.mc4d.world.level.levelgen.structure;

import com.iluha168.mc4d.core.Direction4;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Implemented by {@link BoundingBox}.
 */
public interface BoundingBox4 {
	static BoundingBox from(int minX, int minY, int minZ, int minW, int maxX, int maxY, int maxZ, int maxW) {
		if (maxW < minW) {
			Util.logAndPauseIfInIde("Invalid bounding box data, inverted bounds");
			int tmp = minW;
			minW = maxW;
			maxW = tmp;
		}

		BoundingBox bb = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		BoundingBox4 bb4 = (BoundingBox4) bb;
		bb4.setMinW(minW);
		bb4.setMaxW(maxW);
		return bb;
	}

	int minW();
	int maxW();
	void setMinW(int minW);
	void setMaxW(int maxW);

	static BoundingBox orientBox(
		int footX, int footY, int footZ, int footW,
		int offX, int offY, int offZ, int offW,
		int sizeX, int sizeY, int sizeZ, int sizeW,
		Direction direction
	) {
		// Ambiguous in 4D, but what can we do?
		if (direction == Direction4.ANA) return BoundingBox4.from(
			footX + offX,
			footY + offY,
			footZ + offW,
			footW + offZ,
			footX + sizeX - 1 + offX,
			footY + sizeY - 1 + offY,
			footZ + sizeW - 1 + offW,
			footW + sizeZ - 1 + offZ
		);
		if (direction == Direction4.KATA) return BoundingBox4.from(
			footX + offX,
			footY + offY,
			footZ + offW,
			footW - sizeZ + 1 + offZ,
			footX + sizeX - 1 + offX,
			footY + sizeY - 1 + offY,
			footZ + sizeW - 1 + offW,
			footW + offZ
		);
		return switch (direction) {
			case Direction.NORTH -> BoundingBox4.from(
				footX + offX,
				footY + offY,
				footZ - sizeZ + 1 + offZ,
				footW + offW,
				footX + sizeX - 1 + offX,
				footY + sizeY - 1 + offY,
				footZ + offZ,
				footW + sizeW - 1 + offW
			);
			case Direction.WEST -> BoundingBox4.from(
				footX - sizeZ + 1 + offZ,
				footY + offY,
				footZ + offX,
				footW + offW,
				footX + offZ,
				footY + sizeY - 1 + offY,
				footZ + sizeX - 1 + offX,
				footW + sizeW - 1 + offW
			);
			case Direction.EAST -> BoundingBox4.from(
				footX + offZ,
				footY + offY,
				footZ + offX,
				footW + offW,
				footX + sizeZ - 1 + offZ,
				footY + sizeY - 1 + offY,
				footZ + sizeX - 1 + offX,
				footW + sizeW - 1 + offW
			);
			default /* SOUTH and vertical */ -> BoundingBox4.from(
				footX + offX,
				footY + offY,
				footZ + offZ,
				footW + offW,
				footX + sizeX - 1 + offX,
				footY + sizeY - 1 + offY,
				footZ + sizeZ - 1 + offZ,
				footW + sizeW - 1 + offW
			);
		};
	}

	boolean intersects(int minX, int minZ, int minW, int maxX, int maxZ, int maxW);

	BoundingBox move(int dx, int dy, int dz, int dw);
	BoundingBox moved(int dx, int dy, int dz, int dw);

	BoundingBox inflatedBy(int inflateX, int inflateY, int inflateZ, int inflateW);

	boolean isInside(int x, int y, int z, int w);

	int getWSpan();
}
