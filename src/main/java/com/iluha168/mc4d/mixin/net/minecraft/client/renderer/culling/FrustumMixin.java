package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.culling;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.renderer.culling.Frustum4;
import com.iluha168.mc4d.org.joml.FrustumIntersection4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frustum.class)
class FrustumMixin implements Frustum4 {
	@Shadow @Final private FrustumIntersection intersection;
	@Shadow private double camX;
	@Shadow private double camY;
	@Shadow private double camZ;
	@Unique private double camW;

	@Inject(method = "set", at = @At("TAIL"))
	void set(Frustum frustum, CallbackInfo ci) {
		this.camW = ((Frustum4) frustum).getCamW();
	}

	// TODO offset when 4D renderer
	// TODO offsetToFullyIncludeCameraCube when 4D renderer

	@Overwrite
	@Deprecated
	public void prepare(double camX, double camY, double camZ) {
		throw Err4.arguments3("Frustum4#prepare");
	}
	@Override
	public void prepare(double camX, double camY, double camZ, double camW) {
		this.camX = camX;
		this.camY = camY;
		this.camZ = camZ;
		this.camW = camW;
	}

	@Definition(id = "cubeInFrustum", method = "Lnet/minecraft/client/renderer/culling/Frustum;cubeInFrustum(DDDDDD)I")
	@Expression("this.cubeInFrustum(?, ?, ?, ?, ?, ?)")
	@Redirect(method = "isVisible", at = @At("MIXINEXTRAS:EXPRESSION"))
	int isVisible(Frustum instance, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(argsOnly = true, name = "bb") AABB bb) {
		if (!(bb instanceof AABB4 bb4)) {
			throw Err4.container3();
		}
		return this.cubeInFrustum(minX, minY, minZ, bb4.minW, maxX, maxY, maxZ, bb4.maxW);
	}

	@Redirect(method = "cubeInFrustum(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)I", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/culling/Frustum;cubeInFrustum(DDDDDD)I"
	))
	int cubeInFrustum(Frustum instance, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(argsOnly = true, name = "bb") BoundingBox bb) {
		final BoundingBox4 bb4 = (BoundingBox4) bb;
		return this.cubeInFrustum(minX, minY, minZ, bb4.minW(), maxX, maxY, maxZ, bb4.maxW() + 1);
	}

	@Overwrite
	@Deprecated
	private int cubeInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		throw Err4.arguments3("Frustum4#cubeInFrustum");
	}
	@Override
	public int cubeInFrustum(double minX, double minY, double minZ, double minW, double maxX, double maxY, double maxZ, double maxW) {
		if (minW > this.camW) return FrustumIntersection4.PLANE_PW;
		if (this.camW > maxW) return FrustumIntersection4.PLANE_NW;
		final float x1 = (float)(minX - this.camX);
		final float y1 = (float)(minY - this.camY);
		final float z1 = (float)(minZ - this.camZ);
		final float x2 = (float)(maxX - this.camX);
		final float y2 = (float)(maxY - this.camY);
		final float z2 = (float)(maxZ - this.camZ);
		return this.intersection.intersectAab(x1, y1, z1, x2, y2, z2);
	}

	@Overwrite
	@Deprecated
	public boolean pointInFrustum(double x, double y, double z) {
		throw Err4.arguments3("Frustum4#pointInFrustum");
	}
	@Override
	public boolean pointInFrustum(double x, double y, double z, double minW, double maxW) {
		// This method is only used for particles anyway, this hardcode is fine.
		final double dwMax = Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NEIGHBOURING_SLICE_PARTICLE_RENDERER) ? 1 : 0;
		return minW - dwMax <= this.camW && this.camW <= maxW + dwMax
			&& this.intersection.testPoint((float) (x - this.camX), (float) (y - this.camY), (float) (z - this.camZ));
	}

	// TODO getFrustumPoints

	@Override
	public double getCamW() {
		return this.camW;
	}
}
