package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.culling;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.renderer.culling.Frustum4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frustum.class)
class FrustumMixin implements Frustum4 {
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

	@Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
	void isVisible(AABB bb, CallbackInfoReturnable<Boolean> cir) {
		if (!(bb instanceof AABB4 bb4)) {
			throw Err4.container3();
		}
		final double wExtent = bb4.getWsize() * 0.5;
		final double distanceToCamera = bb4.minW + wExtent - this.camW;
		final boolean neighbouringSlice = Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NEIGHBOURING_SLICE_ENTITY_RENDERER);
		if (Math.abs(distanceToCamera) > wExtent + (neighbouringSlice ? 1 : 0)) {
			// Do not render the entity when it is outside camera's 3D slice.
			cir.setReturnValue(false);
		}
	}

	// TODO cubeInFrustum
	// TODO pointInFrustum
	// TODO getFrustumPoints

	@Override
	public double getCamW() {
		return this.camW;
	}
}
