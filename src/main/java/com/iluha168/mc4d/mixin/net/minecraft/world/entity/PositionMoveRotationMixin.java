package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.InterpolationHandler4;
import com.iluha168.mc4d.world.entity.PositionMoveRotation4;
import com.iluha168.mc4d.world.entity.Relative4;
import com.iluha168.mc4d.world.level.portal.TeleportTransition4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.datafixers.util.Function4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.Function;

@Mixin(PositionMoveRotation.class)
public abstract class PositionMoveRotationMixin implements PositionMoveRotation4 {
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

	@Shadow
	private static double calculateDelta(double currentDelta, double deltaChange, Set<Relative> relatives, Relative relative) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public abstract Vec3 position();

	@Shadow
	public abstract Vec3 deltaMovement();

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"
	))
	private static StreamCodec<FriendlyByteBuf, PositionMoveRotation> STREAM_CODEC(
		StreamCodec<FriendlyByteBuf, Vec3> codec1, Function<PositionMoveRotation, Vec3> getter1,
		StreamCodec<FriendlyByteBuf, Vec3> codec2, Function<PositionMoveRotation, Vec3> getter2,
		StreamCodec<FriendlyByteBuf, Float> codec3, Function<PositionMoveRotation, Float> getter3,
		StreamCodec<FriendlyByteBuf, Float> codec4, Function<PositionMoveRotation, Float> getter4,
		Function4<Vec3, Vec3, Float, Float, PositionMoveRotation> constructor
	) {
		return StreamCodec.composite(
			Vec4.STREAM_CODEC, getter1.andThen(Vec4.class::cast),
			Vec4.STREAM_CODEC, getter2.andThen(Vec4.class::cast),
			codec3, getter3,
			codec4, getter4,
			codec4, PositionMoveRotation4::wRot,
			codec3, PositionMoveRotation4::vRot,
			PositionMoveRotation4::from
		);
	}

	@Inject(method = "<init>", at = @At("HEAD"))
	private static void init(Vec3 position, Vec3 deltaMovement, float yRot, float xRot, CallbackInfo ci) {
		if (!(position instanceof Vec4 && deltaMovement instanceof Vec4)) {
			throw Err4.container3();
		}
	}

	@Definition(id = "PositionMoveRotation", type = PositionMoveRotation.class)
	@Definition(id = "xRot", method = "Lnet/minecraft/world/entity/InterpolationHandler;xRot()F")
	@Expression("new PositionMoveRotation(?, ?, ?, ?.xRot())")
	@ModifyExpressionValue(method = "of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/entity/PositionMoveRotation;", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static PositionMoveRotation of_interpolating(PositionMoveRotation rotation, @Local(argsOnly = true, name = "entity") Entity entity) {
		final PositionMoveRotation4 rotation4 = PositionMoveRotation4.as(rotation);
		final InterpolationHandler4 interpolation4 = (InterpolationHandler4) entity.getInterpolation();
		//noinspection DataFlowIssue
		rotation4.setWRot(interpolation4.wRot());
		rotation4.setVRot(interpolation4.vRot());
		return rotation;
	}
	@Definition(id = "PositionMoveRotation", type = PositionMoveRotation.class)
	@Definition(id = "getXRot", method = "Lnet/minecraft/world/entity/Entity;getXRot()F")
	@Expression("new PositionMoveRotation(?, ?, ?, ?.getXRot())")
	@ModifyExpressionValue(method = "of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/entity/PositionMoveRotation;", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static PositionMoveRotation of_real(PositionMoveRotation rotation, @Local(argsOnly = true, name = "entity") Entity entity) {
		final PositionMoveRotation4 rotation4 = PositionMoveRotation4.as(rotation);
		final Entity4 entity4 = (Entity4) entity;
		rotation4.setWRot(entity4.getWRot());
		rotation4.setVRot(entity4.getVRot());
		return rotation;
	}

	@Overwrite
	@Deprecated
	public PositionMoveRotation withRotation(float yRot, float xRot) {
		throw Err4.rotation("PositionMoveRotation4#withRotation");
	}
	@Override
	public PositionMoveRotation withRotation(float yRot, float xRot, float wRot, float vRot) {
		return PositionMoveRotation4.from(this.position(), this.deltaMovement(), yRot, xRot, wRot, vRot);
	}

	@ModifyReturnValue(method = "of(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/world/entity/PositionMoveRotation;", at = @At("RETURN"))
	private static PositionMoveRotation of_transition(PositionMoveRotation rotation, @Local(argsOnly = true, name = "transition") TeleportTransition transition) {
		final PositionMoveRotation4 rotation4 = PositionMoveRotation4.as(rotation);
		final TeleportTransition4 transition4 = TeleportTransition4.as(transition);
		rotation4.setWRot(transition4.wRot());
		rotation4.setVRot(transition4.vRot());
		return rotation;
	}

	@Redirect(method = "calculateAbsolute", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 0
	))
	private static Vec3 calculateAbsolute_absolutePosition(
		double x, double y, double z,
		@Local(argsOnly = true, name = "source") PositionMoveRotation source,
		@Local(argsOnly = true, name = "change") PositionMoveRotation change,
		@Local(argsOnly = true, name = "relatives") Set<Relative> relatives
	) {
		double offsetW = relatives.contains(Relative4.W) ? ((Vec4) source.position()).w : 0.0;
		return new Vec4(x, y, z, offsetW + ((Vec4) change.position()).w);
	}
	@Definition(id = "absoluteYRot", local = @Local(type = float.class, name = "absoluteYRot"))
	@Expression("absoluteYRot = @(?)")
	@Inject(method = "calculateAbsolute", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void calculateAbsolute_absoluteWVRot(
		PositionMoveRotation source, PositionMoveRotation change, Set<Relative> relatives, CallbackInfoReturnable<PositionMoveRotation> cir,
		@Share("absoluteWRot") LocalFloatRef absoluteWRot,
		@Share("absoluteVRot") LocalFloatRef absoluteVRot
	) {
		final PositionMoveRotation4 source4 = PositionMoveRotation4.as(source);
		final PositionMoveRotation4 change4 = PositionMoveRotation4.as(change);
		final float offsetWRot = relatives.contains(Relative4.W_ROT) ? source4.wRot() : 0.0F;
		final float offsetVRot = relatives.contains(Relative4.V_ROT) ? source4.vRot() : 0.0F;
		absoluteWRot.set(Mth.clamp(offsetWRot + change4.wRot(), -90.0F, 90.0F));
		absoluteVRot.set(offsetVRot + change4.vRot());
	}
	@Redirect(method = "calculateAbsolute", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;xRot(F)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 calculateAbsolute_rotatedCurrentMovement_xRot(Vec3 rotatedCurrentMovement, float radians) {
		// See yRot redirect below.
		return rotatedCurrentMovement;
	}
	@Redirect(method = "calculateAbsolute", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;yRot(F)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 calculateAbsolute_rotatedCurrentMovement_yRot(
		Vec3 sourceDeltaMovement, float radians,
		@Local(argsOnly = true, name = "source") PositionMoveRotation source,
		@Local(name = "absoluteYRot") float absoluteYRot,
		@Local(name = "absoluteXRot") float absoluteXRot,
		@Share("absoluteWRot") LocalFloatRef absoluteWRot,
		@Share("absoluteVRot") LocalFloatRef absoluteVRot
	) {
		final PositionMoveRotation4 source4 = PositionMoveRotation4.as(source);
		final RotationVec sourceRotation = new RotationVec(source.xRot(), source.yRot(), source4.wRot(), source4.vRot());
		final RotationVec absoluteRotation = new RotationVec(absoluteXRot, absoluteYRot, absoluteWRot.get(), absoluteVRot.get());
		// The delta is taken apart in the source frame and rebuilt in the absolute one.
		return Vec4.applyLocalCoordinatesToRotation(
			absoluteRotation,
			Vec4.applyRotationToWorldCoordinates(sourceRotation, ((Vec4) sourceDeltaMovement))
		);
	}
	@Redirect(method = "calculateAbsolute", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 1
	))
	private static Vec3 calculateAbsolute_absoluteDeltaMovement(
		double x, double y, double z,
		@Local(argsOnly = true, name = "change") PositionMoveRotation change,
		@Local(argsOnly = true, name = "relatives") Set<Relative> relatives,
		@Local(name = "rotatedCurrentMovement") Vec3 rotatedCurrentMovement
	) {
		return new Vec4(
			x, y, z,
			calculateDelta(((Vec4) rotatedCurrentMovement).w, ((Vec4) change.deltaMovement()).w, relatives, Relative4.DELTA_W)
		);
	}
	@ModifyReturnValue(method = "calculateAbsolute", at = @At("RETURN"))
	private static PositionMoveRotation calculateAbsolute_return(
		PositionMoveRotation result,
		@Share("absoluteWRot") LocalFloatRef absoluteWRot,
		@Share("absoluteVRot") LocalFloatRef absoluteVRot
	) {
		final PositionMoveRotation4 result4 = PositionMoveRotation4.as(result);
		result4.setWRot(absoluteWRot.get());
		result4.setVRot(absoluteVRot.get());
		return result;
	}

	@WrapMethod(method = "equals")
	public boolean equals(Object o, Operation<Boolean> original) {
		if (!original.call(o)) {
			return false;
		}
		final PositionMoveRotation4 o4 = (PositionMoveRotation4) o;
		return this.wRot == o4.wRot() && this.vRot == o4.vRot();
	}

	@WrapMethod(method = "hashCode")
	public int hashCode(Operation<Integer> original) {
		return (original.call() * 31 + Float.hashCode(this.wRot)) * 31 + Float.hashCode(this.vRot);
	}
}
