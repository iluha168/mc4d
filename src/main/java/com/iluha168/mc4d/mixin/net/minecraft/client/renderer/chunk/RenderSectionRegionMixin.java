package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.chunk.RenderSectionRegion4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSectionRegion.class)
class RenderSectionRegionMixin implements RenderSectionRegion4 {
	@Shadow @Final private int minSectionX;
	@Shadow @Final private int minSectionY;
	@Shadow @Final private int minSectionZ;
	@Shadow @Final private SectionCopy[] sections;

	@Unique private int minSectionW;
	@Unique private boolean minSectionWNotSet;

	@Override
	public int minSectionW() {
		if (this.minSectionWNotSet)
			throw Err4.field4missing("minSectionW");
		return this.minSectionW;
	}
	@Override
	public void setMinSectionW(int w) {
		this.minSectionW = w;
		this.minSectionWNotSet = false;
	}

	@Inject(
		method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;III[Lnet/minecraft/client/renderer/chunk/SectionCopy;Lit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)V",
		at = @At("TAIL")
	)
	void init(CallbackInfo ci) {
		this.minSectionWNotSet = true;
	}

	@Redirect(method = "getBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getSection(III)Lnet/minecraft/client/renderer/chunk/SectionCopy;"
	))
	private SectionCopy getBlockState(RenderSectionRegion self, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getSection(sectionX, sectionY, sectionZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Redirect(method = "getFluidState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getSection(III)Lnet/minecraft/client/renderer/chunk/SectionCopy;"
	))
	private SectionCopy getFluidState(RenderSectionRegion self, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getSection(sectionX, sectionY, sectionZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Redirect(method = "getBlockEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getSection(III)Lnet/minecraft/client/renderer/chunk/SectionCopy;"
	))
	private SectionCopy getBlockEntity(RenderSectionRegion self, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return this.getSection(sectionX, sectionY, sectionZ, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Overwrite
	@Deprecated
	private SectionCopy getSection(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private SectionCopy getSection(int sectionX, int sectionY, int sectionZ, int sectionW) {
		return this.sections[RenderSectionRegion4.index(this.minSectionX, this.minSectionY, this.minSectionZ, this.minSectionW, sectionX, sectionY, sectionZ, sectionW)];
	}

	@Redirect(method = "getAuxLightManager", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getSection(III)Lnet/minecraft/client/renderer/chunk/SectionCopy;"
	))
	private SectionCopy getAuxLightManager(RenderSectionRegion self, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return this.getSection(sectionX, sectionY, sectionZ, ChunkPos4.as(pos).w());
	}

	@Overwrite
	@Deprecated
	public static int index(int minSectionX, int minSectionY, int minSectionZ, int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("RenderSectionRegion4#index");
	}
}