package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.client.renderer.LevelRenderer4;
import com.iluha168.mc4d.client.renderer.ViewArea4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.expression.Definition;import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
class LevelRendererMixin implements LevelRenderer4 {
	// TODO the rest

	@Shadow
	private @Nullable ViewArea viewArea;

	@Definition(id = "zOld", field = "Lnet/minecraft/world/entity/Entity;zOld:D")
	@Definition(id = "entity", local = @Local(type = Entity.class, name = "entity"))
	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/Entity;getZ()D")
	@Expression("entity.zOld = entity.getZ()")
	@Inject(method = "extractVisibleEntities", at = @At("MIXINEXTRAS:EXPRESSION"))
	void setWOld(
		Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState output, CallbackInfo ci,
		@Local(name = "entity") Entity entity
	) {
		Entity4 entity4 = (Entity4) entity;
		entity4.setWOld(entity4.getW());
	}

	// TODO the rest

	@Overwrite
	@Deprecated
	public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("LevelRenderer4#setSectionDirtyWithNeighbors");
	}
	@Override
	public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ, int sectionW) {
		this.setSectionRangeDirty(sectionX - 1, sectionY - 1, sectionZ - 1, sectionW - 1, sectionX + 1, sectionY + 1, sectionZ + 1, sectionW + 1);
	}

	@Overwrite
	@Deprecated
	public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int maxSectionX, int maxSectionY, int maxSectionZ) {
		throw Err4.arguments3("LevelRenderer4#setSectionRangeDirty");
	}
	@Override
	public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int minSectionW, int maxSectionX, int maxSectionY, int maxSectionZ, int maxSectionW) {
		for (int w = minSectionW; w <= maxSectionW; w++)
			for (int z = minSectionZ; z <= maxSectionZ; z++)
				for (int x = minSectionX; x <= maxSectionX; x++)
					for (int y = minSectionY; y <= maxSectionY; y++)
						this.setSectionDirty(x, y, z, w);
	}

	@Overwrite
	@Deprecated
	public void setSectionDirty(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("LevelRenderer4#setSectionDirty");
	}
	@Override
	public void setSectionDirty(int sectionX, int sectionY, int sectionZ, int sectionW) {
		this.setSectionDirty(sectionX, sectionY, sectionZ, sectionW, false);
	}

	@Overwrite
	@Deprecated
	private void setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged) {
		throw Err4.arguments3(null);
	}
	@Unique
	private void setSectionDirty(int sectionX, int sectionY, int sectionZ, int sectionW, boolean playerChanged) {
		//noinspection DataFlowIssue
		((ViewArea4) this.viewArea).setDirty(sectionX, sectionY, sectionZ, sectionW, playerChanged);
	}

	// TODO the rest
}
