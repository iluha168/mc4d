package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.server.level.ChunkTrackingView4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionOcclusionGraph.class)
class SectionOcclusionGraphMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	private static Direction[] DIRECTIONS() {
		// TODO remove when 4D renderer
		return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	}

	@Redirect(method = "addNeighbors", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long addNeighbors(int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ChunkPos4.pack(x, z, ChunkPos4.as(pos).w());
	}
	// TODO addNeighbors neighbouring in W when 4D renderer

	// TODO initializeQueueForFullUpdate

	// TODO runUpdates needs rewrite when 4D renderer
	@Redirect(method = "runUpdates", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos runUpdates(
		double x, double y, double z,
		@Local(name = "sectionNode") long sectionNode,
		@Local(name = "direction") Direction direction,
		@Local(name = "cameraSectionCenter") BlockPos cameraSectionCenter
	) {
		final int renderSectionOriginW = SectionPos.sectionToBlockCoord(SectionPos4.w(sectionNode));
		final boolean maxW = direction.getAxis() == Direction4.Axis.W
			? Vec4i.getW(cameraSectionCenter) > renderSectionOriginW
			: Vec4i.getW(cameraSectionCenter) < renderSectionOriginW;
		final double w = renderSectionOriginW + (maxW ? 16 : 0);
		return BlockPos4.containing(x, y, z, w);
	}

	@Redirect(method = "isInViewDistance", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ChunkTrackingView;isInViewDistance(IIIII)Z"
	))
	boolean isInViewDistance(
		int centerX, int centerZ, int viewDistance, int chunkX, int chunkZ,
		@Local(argsOnly = true, name = "cameraSectionNode") long cameraSectionNode,
		@Local(argsOnly = true, name = "sectionNode") long sectionNode
	) {
		return ChunkTrackingView4.isInViewDistance(
			centerX, centerZ, SectionPos4.w(cameraSectionNode),
			viewDistance,
			chunkX, chunkZ, SectionPos4.w(sectionNode)
		);
	}
}
