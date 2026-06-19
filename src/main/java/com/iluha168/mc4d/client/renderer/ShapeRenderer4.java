package com.iluha168.mc4d.client.renderer;

import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

/**
 * 4D version of {@link net.minecraft.client.renderer.ShapeRenderer}.
 */
public interface ShapeRenderer4 {
	static void renderShape(
		PoseStack poseStack, VertexConsumer builder, VoxelShape shape,
		double x, double y, double z, double w, int color, float width
	) {
		// Grab the 3D slice of the 4D shape to not render edges outside camera's slice.
		VoxelShape slice = ((VoxelShape4) shape).sliceW(-w);

		PoseStack.Pose pose = poseStack.last();
		((VoxelShape4) slice).forAllEdges((x1, y1, z1, w1, x2, y2, z2, w2) -> {
			// TODO: use w1 and w2 in 4D renderer, and remove slicing
			float sizeX = (float) (x2 - x1);
			float sizeY = (float) (y2 - y1);
			float sizeZ = (float) (z2 - z1);
			if (sizeX == 0 && sizeY == 0 && sizeZ == 0) {
				return; // Prevent division by zero in normalize
			}
			Vector3f normal = new Vector3f(sizeX, sizeY, sizeZ).normalize();
			builder.addVertex(pose, (float) (x1 + x), (float) (y1 + y), (float) (z1 + z)).setColor(color).setNormal(pose, normal).setLineWidth(width);
			builder.addVertex(pose, (float) (x2 + x), (float) (y2 + y), (float) (z2 + z)).setColor(color).setNormal(pose, normal).setLineWidth(width);
		});
	}
}