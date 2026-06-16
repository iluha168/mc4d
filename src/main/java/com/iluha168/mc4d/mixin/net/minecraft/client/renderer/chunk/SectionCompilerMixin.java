package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.chunk.SectionCompiler4;
import com.iluha168.mc4d.core.BlockPos4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionCompiler.class)
class SectionCompilerMixin implements SectionCompiler4 {
	@Unique private volatile int sliceW;

	@Override
	public int sliceW() {
		return this.sliceW;
	}
	@Override
	public void setSliceW(int blockW) {
		this.sliceW = blockW;
	}

	@ModifyExpressionValue(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;origin()Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos compile_origin(BlockPos origin) {
		return ((BlockPos4) origin).offset(0, 0, 0, Math.floorMod(this.sliceW, LevelChunkSection.SECTION_WIDTH));
	}
	@Redirect(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos compile_offset(BlockPos minPos, int x, int y, int z) {
		return ((BlockPos4) minPos).offset(x, y, z, 0);
	}
}
