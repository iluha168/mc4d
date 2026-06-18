package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.renderer.chunk.SectionCompiler4;
import com.iluha168.mc4d.core.BlockPos4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionCompiler.class)
class SectionCompilerMixin implements SectionCompiler4 {
	@Unique private volatile double cameraW;
	@Unique private volatile int closestIntegerNeighbourSliceDeltaW;
	@Unique private volatile float ghostBlockShrinkFactor;
	@Unique private volatile int tintColor;
	@Unique private boolean enableNeighbouringSliceRenderer;

	@Override
	public void setCameraW(double cameraW) {
		this.cameraW = cameraW;
		final double distanceToCurrentSliceMiddle = this.cameraW - Math.floor(this.cameraW) - 0.5;
		this.closestIntegerNeighbourSliceDeltaW = distanceToCurrentSliceMiddle > 0 ? 1 : -1;
		this.ghostBlockShrinkFactor = Math.abs((float) distanceToCurrentSliceMiddle) * 2.0F;
		this.tintColor = ARGB.color(
			1.0F - this.ghostBlockShrinkFactor, // Makes blocks that are closer less tinted
			ARGB.transparent(distanceToCurrentSliceMiddle > 0 ? MC4DClient.COLOR_ANA : MC4DClient.COLOR_KATA)
		);
	}

	@ModifyExpressionValue(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;origin()Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos compile_origin(BlockPos origin) {
		this.enableNeighbouringSliceRenderer = Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NEIGHBOURING_3D_SLICE_RENDERER);
		return ((BlockPos4) origin).offset(0, 0, 0, SectionPos.sectionRelative((int) Math.floor(this.cameraW)));
	}
	@Redirect(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos compile_offset(BlockPos minPos, int x, int y, int z) {
		return ((BlockPos4) minPos).offset(x, y, z, 0);
	}
	@WrapOperation(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	private BlockState compile_peek(RenderSectionRegion region, BlockPos pos, Operation<BlockState> original, @Share("peeking") LocalBooleanRef peeking) {
		final BlockState state = original.call(region, pos);
		// If the current slice's block is invisible, show the next closest slice.
		if (this.enableNeighbouringSliceRenderer && state.isAir()) {
			final BlockState neighbour = original.call(region, ((BlockPos4) pos).offset(0, 0, 0, this.closestIntegerNeighbourSliceDeltaW));
			if (!neighbour.isAir()) {
				peeking.set(true);
				return neighbour;
			}
		}
		peeking.set(false);
		return state;
	}
	@ModifyArg(index = 0, method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"
	))
	private BlockQuadOutput compile_tint(BlockQuadOutput output, @Share("peeking") LocalBooleanRef peeking) {
		if (!peeking.get()) return output;
		return (x, y, z, quad, instance) -> {
			for (int vertex = 0; vertex < 4; vertex++)
				instance.setColor(vertex, ARGB.alphaBlend(instance.getColor(vertex), this.tintColor));
			output.put(x, y, z, scale(quad, this.ghostBlockShrinkFactor), instance);
		};
	}

	// --------------------------------------- NOT VANILLA+ ---------------------------------------
	/** Scales quad using (0.5; 0.5; 0.5) as the origin. */
	@Unique
	private static BakedQuad scale(BakedQuad quad, float scale) {
		return new BakedQuad(
			mulAtBlockCenter(quad.position0(), scale),
			mulAtBlockCenter(quad.position1(), scale),
			mulAtBlockCenter(quad.position2(), scale),
			mulAtBlockCenter(quad.position3(), scale),
			quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
			quad.direction(), quad.materialInfo(), quad.bakedNormals(), quad.bakedColors()
		);
	}
	/** Multiplies vector using (0.5; 0.5; 0.5) as the origin. */
	@Unique
	private static Vector3fc mulAtBlockCenter(Vector3fc pos, float scale) {
		return new Vector3f(
			0.5F + (pos.x() - 0.5F) * scale,
			0.5F + (pos.y() - 0.5F) * scale,
			0.5F + (pos.z() - 0.5F) * scale
		);
	}
}