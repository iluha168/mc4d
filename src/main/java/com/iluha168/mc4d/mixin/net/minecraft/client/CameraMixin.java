package com.iluha168.mc4d.mixin.net.minecraft.client;

import com.iluha168.mc4d.client.Camera4;
import com.iluha168.mc4d.client.renderer.culling.Frustum4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
abstract
class CameraMixin implements Camera4 {
	@Shadow
	protected abstract void setPosition(Vec3 position);

	@Shadow
	private @Nullable Level level;

	@Shadow
	private @Nullable Entity entity;

	@Shadow
	private Vec3 position;

	@Unique	private Vec4 offsetTarget = Vec4.ZERO;
	@Unique	private Vec4 offsetOld = Vec4.ZERO;
	@Unique	private Vec4 offset = Vec4.ZERO;
	@Unique	private Vec4 offsetApplied = Vec4.ZERO;
	@Unique	private boolean offsetAppliedNonZero = false;

	@Override
	public void setOffsetDirection(@NonNull Vec4 target) {
		this.offsetTarget = target.normalize().scale(Camera4.OFFSET_DISTANCE);
	}
	@Override
	public void unsetOffsetDirection() {
		this.offsetTarget = Vec4.ZERO;
	}

	@Override
	public @NonNull Vec4 getOffsetApplied() {
		return this.offsetApplied;
	}
	@Override
	public boolean hasOffset() {
		return this.offsetAppliedNonZero;
	}

	@Definition(id = "position", field = "Lnet/minecraft/client/Camera;position:Lnet/minecraft/world/phys/Vec3;")
	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;")
	@Expression("this.position = @(ZERO)")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	Vec3 initialPosition(Vec3 original) {
		return Vec4.ZERO;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	void tick(CallbackInfo ci) {
		this.offsetOld = this.offset;
		// This is the part responsible for the ease function
		final Vec4 approachDirection = this.offsetTarget.subtract(this.offset);
		this.offset = approachDirection.lengthSqr() > Mth.EPSILON
			? this.offset.add(approachDirection.scale(Camera4.OFFSET_DISTANCE_REACH_SPEED))
			: this.offsetTarget;
	}

	@Redirect(method = "prepareCullFrustum", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/culling/Frustum;prepare(DDD)V"
	))
	void prepareCullFrustum(Frustum frustum, double camX, double camY, double camZ, @Local(argsOnly = true, name = "cameraPos") Vec3 cameraPos) {
		((Frustum4) frustum).prepare(camX, camY, camZ, ((Vec4) cameraPos).w);
	}

	@Redirect(method = "alignWithEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"
	))
	void alignWithEntity_position(
		Camera This, double x, double y, double z,
		@Local(argsOnly = true, name = "partialTicks") float partialTicks
	) {
		Entity4 entity4 = (Entity4) this.entity;
		assert entity4 != null;
		double w = Mth.lerp(partialTicks, entity4.getWO(), entity4.getW());
		this.setPosition(new Vec4(x, y, z, w));
	}
	@Definition(id = "detached", field = "Lnet/minecraft/client/Camera;detached:Z")
	@Expression("this.detached = ?")
	@Inject(method = "alignWithEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
	void alignWithEntity_offset(float partialTicks, CallbackInfo ci) {
		this.offsetApplied = this.getMaxOffset(this.offsetOld.lerp(this.offset, partialTicks));
		this.offsetAppliedNonZero = this.offsetApplied.lengthSqr() != 0;
		if (this.offsetAppliedNonZero) {
			this.setPosition(((Vec4) this.position).add(this.offsetApplied));
		}
	}

	@ModifyConstant(method = "getMaxZoom", constant = @Constant(intValue = 8))
	int getMaxZoom_iMax(int constant) {
		return 16;
	}
	@Redirect(method = "getMaxZoom", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getMaxZoom_from(Vec3 position, double x, double y, double z, @Local(name = "i") int i) {
		float offsetW = (i >> 3 & 1) * 2 - 1;
		return ((Vec4) position).add(x, y, z, offsetW * 0.1F);
	}

	/**
	 * Prevents xray glitches due to camera offset feature.
	 * This is basically a slightly edited {@link Camera#getMaxZoom} - same logic as second/third person mode.
	 */
	@Unique	@NonNull Vec4 getMaxOffset(@NonNull Vec4 offset) {
		final double length = offset.length();
		if (this.level == null || this.entity == null || length < Mth.EPSILON) {
			return offset;
		}
		final double jitterScale = 0.1;
		final Vec4 position4 = (Vec4) this.position;

		double maxOffsetScale = 1.0;

		for (int i = 0; i < 16; i++) {
			final double jitterX = ((i      & 1) * 2 - 1) * jitterScale;
			final double jitterY = ((i >> 1 & 1) * 2 - 1) * jitterScale;
			final double jitterZ = ((i >> 2 & 1) * 2 - 1) * jitterScale;
			final double jitterW = ((i >> 3 & 1) * 2 - 1) * jitterScale;
			final Vec4 from = position4.add(jitterX, jitterY, jitterZ, jitterW);
			final Vec4 to = from.add(offset);
			final HitResult hitResult = this.level.clip(new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity));
			if (hitResult.getType() != HitResult.Type.MISS) {
				final double probeOffsetScale = (hitResult.getLocation().distanceTo(from) - OFFSET_COLLISION_MARGIN) / length;
				if (probeOffsetScale < maxOffsetScale) {
					maxOffsetScale = probeOffsetScale;
				}
			}
		}
		return offset.scale(Math.max(maxOffsetScale, 0.0));
	}

	@Definition(id = "setPosition", method = "Lnet/minecraft/client/Camera;setPosition(Lnet/minecraft/world/phys/Vec3;)V")
	@Expression("this.setPosition(?)")
	@Redirect(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move(Camera This, Vec3 position) {
		// TODO rework when 4D renderer
		this.setPosition(Vec4.of(position, ((Vec4) this.position).w));
	}

	@Redirect(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(DDD)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos setPosition(BlockPos.MutableBlockPos blockPosition, double x, double y, double z, @Local(argsOnly = true, name = "position") Vec3 position) {
		if (!(position instanceof Vec4 pos4)) {
			throw Err4.container3();
		}
		return ((BlockPos4.MutableBlockPos) blockPosition).set(x, y, z, pos4.w);
	}

	// TODO? NearPlane
}
