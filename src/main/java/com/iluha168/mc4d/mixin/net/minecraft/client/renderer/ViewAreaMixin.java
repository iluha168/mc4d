package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.client.renderer.ViewArea4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ViewArea.class)
class ViewAreaMixin implements ViewArea4 {
	@Shadow
	@Final
	protected Level level;

	@Shadow
	private SectionPos cameraSectionPos;

	@Shadow
	private int viewDistance;

	@Shadow	protected int sectionGridSizeX;
	@Shadow	protected int sectionGridSizeZ;
	@Shadow
	public SectionRenderDispatcher.RenderSection[] sections;
	@Shadow
	protected int sectionGridSizeY;
	@Unique	protected int sectionGridSizeW;

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;of(III)Lnet/minecraft/core/SectionPos;"
	))
	SectionPos cameraSectionPos(int x, int y, int z) {
		return SectionPos4.of(x, y, z, z);
	}

	@Definition(id = "totalSections", local = @Local(type = int.class, name = "totalSections"))
	@Expression("totalSections = @(?)")
	@ModifyExpressionValue(method = "createSections", at = @At("MIXINEXTRAS:EXPRESSION"))
	int createSections_totalSections(int size3D) {
		return size3D * this.sectionGridSizeW;
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(0)")
	@Inject(method = "createSections", at = @At("MIXINEXTRAS:EXPRESSION"))
	void createSections_w(SectionRenderDispatcher sectionRenderDispatcher, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(0);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "createSections", at = @At("MIXINEXTRAS:EXPRESSION"))
	int createSections_incrementW(int one, @Share("w") LocalIntRef w) {
		w.set(w.get() + 1);
		if (w.get() < this.sectionGridSizeW) return 0;
		w.set(0);
		return 1;
	}
	@Definition(id = "getSectionIndex", method = "Lnet/minecraft/client/renderer/ViewArea;getSectionIndex(III)I")
	@Expression("this.getSectionIndex(?, ?, ?)")
	@Redirect(method = "createSections", at = @At("MIXINEXTRAS:EXPRESSION"))
	int createSections_getSectionIndex(ViewArea This, int x, int y, int z, @Share("w") LocalIntRef w) {
		return this.getSectionIndex(x, y, z, w.get());
	}
	@Redirect(method = "createSections", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long createSections_asLong(int x, int y, int z, @Share("w") LocalIntRef w) {
		return SectionPos4.asLong(x, y, z, w.get());
	}

	@Overwrite
	@Deprecated
	private int getSectionIndex(int x, int y, int z) {
		throw Err4.arguments3(null);
	}
	@Unique
	private int getSectionIndex(int x, int y, int z, int w) {
		return ((w * this.sectionGridSizeZ + z) * this.sectionGridSizeY + y) * this.sectionGridSizeX + x;
	}

	@Definition(id = "sectionGridSizeZ", field = "Lnet/minecraft/client/renderer/ViewArea;sectionGridSizeZ:I")
	@Expression("this.sectionGridSizeZ = @(?)")
	@ModifyExpressionValue(method = "setViewDistance", at = @At("MIXINEXTRAS:EXPRESSION"))
	int setViewDistance(int dist) {
		this.sectionGridSizeW = dist;
		return dist;
	}

	@Definition(id = "gridZ", local = @Local(type = int.class, name = "gridZ"))
	@Expression("gridZ = @(0)")
	@Inject(method = "repositionCamera", at = @At("MIXINEXTRAS:EXPRESSION"))
	void repositionCamera_gridW(SectionPos cameraSectionPos, CallbackInfo ci, @Share("gridW") LocalIntRef gridW) {
		gridW.set(0);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "gridZ", local = @Local(type = int.class, name = "gridZ"))
	@Expression("gridZ = gridZ + @(1)")
	@ModifyExpressionValue(method = "repositionCamera", at = @At("MIXINEXTRAS:EXPRESSION"))
	int repositionCamera_incrementGridW(int one, @Share("gridW") LocalIntRef gridW) {
		gridW.set(gridW.get() + 1);
		if (gridW.get() < this.sectionGridSizeW) return 0;
		gridW.set(0);
		return 1;
	}
	@Definition(id = "newSectionZ", local = @Local(type = int.class, name = "newSectionZ"))
	@Expression("newSectionZ = @(?)")
	@Inject(method = "repositionCamera", at = @At("MIXINEXTRAS:EXPRESSION"))
	void repositionCamera_lowestW_newSectionW(
		SectionPos cameraSectionPos, CallbackInfo ci,
		@Share("newSectionW") LocalIntRef newSectionW,
		@Share("gridW") LocalIntRef gridW
	) {
		final int lowestW = ((SectionPos4) cameraSectionPos).w() - this.viewDistance;
		newSectionW.set(lowestW + Math.floorMod(gridW.get() - lowestW, this.sectionGridSizeZ));
	}
	@Definition(id = "getSectionIndex", method = "Lnet/minecraft/client/renderer/ViewArea;getSectionIndex(III)I")
	@Expression("this.getSectionIndex(?, ?, ?)")
	@Redirect(method = "repositionCamera", at = @At("MIXINEXTRAS:EXPRESSION"))
	int repositionCamera_getSectionIndex(ViewArea This, int x, int y, int z, @Share("gridW") LocalIntRef gridW) {
		return this.getSectionIndex(x, y, z, gridW.get());
	}
	@Redirect(method = "repositionCamera", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long repositionCamera_asLong(int x, int y, int z, @Share("newSectionW") LocalIntRef newSectionW) {
		return SectionPos4.asLong(x, y, z, newSectionW.get());
	}

	@Overwrite
	@Deprecated
	public void setDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged) {
		throw Err4.arguments3(null);
	}
	@Override
	public void setDirty(int sectionX, int sectionY, int sectionZ, int sectionW, boolean playerChanged) {
		SectionRenderDispatcher.RenderSection section = this.getRenderSection(sectionX, sectionY, sectionZ, sectionW);
		if (section != null) {
			section.setDirty(playerChanged);
		}
	}
	@Override
	public void setAllDirty(boolean playerChanged) {
		for (SectionRenderDispatcher.RenderSection section : this.sections) {
			section.setDirty(playerChanged);
		}
	}

	@Definition(id = "getRenderSection", method = "Lnet/minecraft/client/renderer/ViewArea;getRenderSection(III)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;")
	@Expression("this.getRenderSection(?, ?, ?)")
	@Redirect(method = "getRenderSection(J)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;", at = @At("MIXINEXTRAS:EXPRESSION"))
	SectionRenderDispatcher.RenderSection getRenderSection(ViewArea This, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "sectionNode") long sectionNode) {
		return this.getRenderSection(sectionX, sectionY, sectionZ, SectionPos4.w(sectionNode));
	}

	@Overwrite
	@Deprecated
	private SectionRenderDispatcher.@Nullable RenderSection getRenderSection(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private SectionRenderDispatcher.@Nullable RenderSection getRenderSection(int sectionX, int sectionY, int sectionZ, int sectionW) {
		if (!this.containsSection(sectionX, sectionY, sectionZ, sectionW)) {
			return null;
		}
		int y = sectionY - this.level.getMinSectionY();
		int x = Math.floorMod(sectionX, this.sectionGridSizeX);
		int z = Math.floorMod(sectionZ, this.sectionGridSizeZ);
		int w = Math.floorMod(sectionW, this.sectionGridSizeW);
		return this.sections[this.getSectionIndex(x, y, z, w)];
	}

	@Overwrite
	@Deprecated
	private boolean containsSection(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private boolean containsSection(int sectionX, int sectionY, int sectionZ, int sectionW) {
		if (sectionY >= this.level.getMinSectionY() && sectionY <= this.level.getMaxSectionY()) {
			final int cameraSectionPosW = ((SectionPos4) this.cameraSectionPos).w();
			return sectionX >= this.cameraSectionPos.x() - this.viewDistance && sectionX <= this.cameraSectionPos.x() + this.viewDistance
				&& sectionZ >= this.cameraSectionPos.z() - this.viewDistance && sectionZ <= this.cameraSectionPos.z() + this.viewDistance
				&& sectionW >= cameraSectionPosW         - this.viewDistance && sectionW <= cameraSectionPosW         + this.viewDistance;
		}
		return false;
	}
}
