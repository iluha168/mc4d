package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.chunk.SectionCompiler4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionRenderDispatcher.class)
class SectionRenderDispatcherMixin {
	@Shadow
	private SectionCompiler sectionCompiler;

	@Inject(method = "setCameraPosition", at = @At("TAIL"))
	void setCameraPosition(Vec3 cameraPosition, CallbackInfo ci) {
		// The method is valid as is, this just tells the renderer the current slice.
		((SectionCompiler4) this.sectionCompiler).setSliceW(Mth.floor(((Position4) cameraPosition).w()));
	}

	@Mixin(SectionRenderDispatcher.RenderSection.class)
	static class RenderSectionMixin {
		@Redirect(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
		))
		long init_asLong(int x, int y, int z) {
			return SectionPos4.asLong(x, y, z, z);
		}
		@Redirect(method = "<init>", at = @At(
			value = "NEW",
			target = "(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos init_mutablePos(int x, int y, int z) {
			return BlockPos4.MutableBlockPos.from(x, y, z, z);
		}

		@Redirect(method = "doesChunkExistAt", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;"
		))
		ChunkAccess doesChunkExistAt(ClientLevel level, int x, int z, ChunkStatus chunkStatus, boolean loadOrGenerate, @Local(argsOnly = true, name = "sectionNode") long sectionNode) {
			return ((LevelReader4) level).getChunk(x, z, SectionPos4.w(sectionNode), chunkStatus, loadOrGenerate);
		}

		@Redirect(method = "hasAllNeighbors", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/SectionPos;offset(JIII)J"
		))
		long hasAllNeighbors_offset(long sectionNode, int stepX, int stepY, int stepZ) {
			return SectionPos4.offset(sectionNode, stepX, stepY, stepZ, 0);
		}
		// TODO hasAllNeighbors add W offsets when 4D renderer

		@Redirect(method = "setSectionNode", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
		))
		BlockPos.MutableBlockPos setSectionNode_set(BlockPos.MutableBlockPos renderOrigin, int x, int y, int z, @Share("w") LocalIntRef w, @Local(argsOnly = true, name = "sectionNode") long sectionNode) {
			w.set(SectionPos.sectionToBlockCoord(SectionPos4.w(sectionNode)));
			return ((BlockPos4.MutableBlockPos) renderOrigin).set(x, y, z, w.get());
		}
		@Redirect(method = "setSectionNode", at = @At(
			value = "NEW",
			target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
		))
		AABB setSectionNode_bb(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Share("w") LocalIntRef w) {
			return new AABB4(minX, minY, minZ, w.get(), maxX, maxY, maxZ, w.get() + SectionRenderDispatcher.RenderSection.SIZE);
		}

		// TODO createVertexSorting when 4D renderer
	}
}
