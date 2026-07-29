package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.InterpolationHandler4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(InterpolationHandler.class)
abstract class InterpolationHandlerMixin implements InterpolationHandler4 {
	@Shadow
	@Final
	private Entity entity;

	@Shadow
	private int interpolationSteps;

	@Shadow
	@Final
	private InterpolationHandler.InterpolationData interpolationData;

	@Shadow
	private @Nullable Vec3 previousTickPosition;

	@Shadow
	private @Nullable Vec2 previousTickRot;

	@Shadow
	@Final
	private @Nullable Consumer<InterpolationHandler> onInterpolationStart;

	@Shadow
	public abstract Vec3 position();

	@Shadow
	public abstract float yRot();

	@Shadow
	public abstract float xRot();

	@Shadow
	public abstract boolean hasActiveInterpolation();

	@Shadow
	public abstract void cancel();

	@Redirect(method = "<init>(Lnet/minecraft/world/entity/Entity;ILjava/util/function/Consumer;)V", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	Vec3 init() {
		return Vec4.ZERO;
	}

	@Override
	public float wRot() {
		return this.interpolationData.steps > 0
			? ((InterpolationHandler4.InterpolationData) this.interpolationData).wRot()
			: ((Entity4) this.entity).getWRot();
	}
	@Override
	public float vRot() {
		return this.interpolationData.steps > 0
			? ((InterpolationHandler4.InterpolationData) this.interpolationData).vRot()
			: ((Entity4) this.entity).getVRot();
	}

	@Overwrite
	@Deprecated
	public void interpolateTo(Vec3 position, float yRot, float xRot) {
		throw Err4.rotation("InterpolationHandler4#interpolateTo");
	}
	@Override
	public void interpolateTo(Vec3 position, float yRot, float xRot, float wRot, float vRot) {
		final Entity4 entity = (Entity4) this.entity;
		if (this.interpolationSteps == 0) {
			entity.snapTo(position, yRot, xRot, wRot, vRot);
			this.cancel();
		} else if (!this.hasActiveInterpolation()
			|| !Objects.equals(this.yRot(), yRot)
			|| !Objects.equals(this.xRot(), xRot)
			|| !Objects.equals(this.wRot(), wRot)
			|| !Objects.equals(this.vRot(), vRot)
			|| !Objects.equals(this.position(), position)) {
			this.interpolationData.steps = this.interpolationSteps;
			this.interpolationData.position = position;
			this.interpolationData.yRot = yRot;
			this.interpolationData.xRot = xRot;
			final InterpolationHandler4.InterpolationData data = (InterpolationHandler4.InterpolationData) this.interpolationData;
			data.setWRot(wRot);
			data.setVRot(vRot);
			this.previousTickPosition = this.entity.position();
			this.previousTickRot = new RotationVec(this.entity.getXRot(), this.entity.getYRot(), entity.getWRot(), entity.getVRot());
			if (this.onInterpolationStart != null) {
				this.onInterpolationStart.accept((InterpolationHandler) (Object) this);
			}
		}
	}

	@Redirect(method = "interpolate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/InterpolationHandler$InterpolationData;addRotation(FF)V"
	))
	void interpolate_addRotation(InterpolationHandler.InterpolationData interpolationData, float yRot, float xRot) {
		final RotationVec previousTickRot = (RotationVec) this.previousTickRot;
		final Entity4 entity = (Entity4) this.entity;
		@SuppressWarnings("DataFlowIssue")
		final float deltaWRotSinceLastInterpolation = entity.getWRot() - previousTickRot.w;
		final float deltaVRotSinceLastInterpolation = entity.getVRot() - previousTickRot.v;
		((InterpolationHandler4.InterpolationData) interpolationData).addRotation(yRot, xRot, deltaWRotSinceLastInterpolation, deltaVRotSinceLastInterpolation);
	}
	@Redirect(method = "interpolate", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 interpolate_newPosition(double x, double y, double z, @Local(name = "alpha") double alpha) {
		final double w = Mth.lerp(alpha, ((Entity4) this.entity).getW(), ((Vec4) this.interpolationData.position).w);
		return new Vec4(x, y, z, w);
	}
	@Redirect(method = "interpolate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setRot(FF)V"
	))
	void interpolate_setRot(Entity entity, float yRot, float xRot, @Local(name = "alpha") double alpha) {
		final Entity4 entity4 = (Entity4) entity;
		final InterpolationHandler4.InterpolationData data = (InterpolationHandler4.InterpolationData) this.interpolationData;
		final float newWRot = (float) Mth.lerp(alpha, entity4.getWRot(), data.wRot());
		final float newVRot = (float) Mth.rotLerp(alpha, entity4.getVRot(), data.vRot());
		entity4.setRot(yRot, xRot, newWRot, newVRot);
	}
	@Redirect(method = "interpolate", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	Vec2 interpolate_previousTickRot(float x, float y) {
		final Entity4 entity = (Entity4) this.entity;
		return new RotationVec(x, y, entity.getWRot(), entity.getVRot());
	}

	@Mixin(targets = "net.minecraft.world.entity.InterpolationHandler$InterpolationData")
	static class InterpolationDataMixin implements InterpolationHandler4.InterpolationData {
		@Shadow
		public float yRot, xRot;
		@Unique
		private float wRot, vRot;

		@Override
		public float wRot() {
			return this.wRot;
		}
		@Override
		public void setWRot(float wRot) {
			this.wRot = wRot;
		}
		@Override
		public float vRot() {
			return this.vRot;
		}
		@Override
		public void setVRot(float vRot) {
			this.vRot = vRot;
		}

		@Overwrite
		@Deprecated
		public void addRotation(float yRot, float xRot) {
			throw Err4.rotation("InterpolationHandler4.InterpolationData#addRotation");
		}
		@Override
		public void addRotation(float yRot, float xRot, float wRot, float vRot) {
			this.yRot += yRot;
			this.xRot += xRot;
			this.wRot += wRot;
			this.vRot += vRot;
		}
	}
}
