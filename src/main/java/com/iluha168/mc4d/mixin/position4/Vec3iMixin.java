package com.iluha168.mc4d.mixin.position4;

import com.google.common.base.MoreObjects;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(Vec3i.class)
public abstract class Vec3iMixin implements Vec4i {
	@Shadow
	@Final
	public static Vec3i ZERO;

	@Definition(id = "CODEC", field = "Lnet/minecraft/core/Vec3i;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Codec<Vec3i> CODEC(Codec<Vec3i> original) {
		return Codec.INT_STREAM
			.comapFlatMap(
				input -> Util
					.fixedSize(input, 4)
					.map(ints -> Vec4i.from(ints[0], ints[1], ints[2], ints[3])),
				pos -> IntStream
					.of(pos.getX(), pos.getY(), pos.getZ(), Vec4i.getW(pos))
			);
	}

	@Definition(id = "STREAM_CODEC", field = "Lnet/minecraft/core/Vec3i;STREAM_CODEC:Lnet/minecraft/network/codec/StreamCodec;")
	@Expression("STREAM_CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static StreamCodec<ByteBuf, Vec3i> STREAM_CODEC(StreamCodec<ByteBuf, Vec3i> original) {
		return StreamCodec.composite(
			ByteBufCodecs.VAR_INT, Vec3i::getX,
			ByteBufCodecs.VAR_INT, Vec3i::getY,
			ByteBufCodecs.VAR_INT, Vec3i::getZ,
			ByteBufCodecs.VAR_INT, Vec4i::getW,
			Vec4i::from
		);
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void ZERO(CallbackInfo ci) {
		Vec4i.setW(ZERO, 0);
	}

	@Unique	private int w;
	@Unique	private boolean wNotSet;

	@Expression("? < ?")
	@ModifyExpressionValue(method = "lambda$offsetCodec$0", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static boolean offsetCodec(
		boolean original,
		@Local(argsOnly = true, name = "value") Vec3i value,
		@Local(argsOnly = true, name = "maxOffsetPerAxis") int maxOffsetPerAxis
	) {
		return original && Math.abs(Vec4i.getW(value)) < maxOffsetPerAxis;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void constructor(int x, int y, int z, CallbackInfo ci) {
		this.wNotSet = true;
	}

	@Expression("? == ?")
	@ModifyExpressionValue(method = "equals", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	boolean equals(boolean original, @Local(argsOnly = true, name = "o") Object o) {
		return original && this.getW() == ((Vec4i) o).getW();
	}

	@ModifyExpressionValue(method = "hashCode", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;getZ()I"
	))
	int hashCode(int z) {
		return this.getW() * 31 + z;
	}

	@Definition(id = "getX", method = "Lnet/minecraft/core/Vec3i;getX()I")
	@Definition(id = "pos", local = @Local(type = Vec3i.class, name = "pos", argsOnly = true))
	@Expression("this.getX() - pos.getX()")
	@ModifyExpressionValue(method = "compareTo(Lnet/minecraft/core/Vec3i;)I", at = @At("MIXINEXTRAS:EXPRESSION"))
	int compareTo(int dx, @Local(argsOnly = true, name = "pos") Vec3i pos) {
		return dx == 0 ? this.getW() - Vec4i.getW(pos) : dx;
	}

	@Override
	public int getW() {
		if (wNotSet) {
			throw Err4.field4missing("w");
		}
		return w;
	}

	@Override
	public Vec4i setW(int w) {
		this.w = w;
		this.wNotSet = false;
		return this;
	}

	@Overwrite
	public Vec3i offset(int x, int y, int z) {
		throw Err4.arguments3("Vec4i#offset");
	}

	@Redirect(method = "offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/Vec3i;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;offset(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i offset(Vec3i This, int x, int y, int z, @Local(argsOnly = true, name = "vec") Vec3i vec) {
		return ((Vec4i) This).offset(x, y, z, Vec4i.getW(vec));
	}

	@Redirect(method = "subtract", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;offset(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i subtract(Vec3i This, int x, int y, int z, @Local(argsOnly = true, name = "vec") Vec3i vec) {
		return ((Vec4i) This).offset(x, y, z, -Vec4i.getW(vec));
	}

	@Redirect(method = "multiply(I)Lnet/minecraft/core/Vec3i;", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i multiplyScalar(int x, int y, int z, @Local(argsOnly = true, name = "scale") int scale) {
		return Vec4i.from(x, y, z, this.getW() * scale);
	}

	@Overwrite
	public Vec3i multiply(int xScale, int yScale, int zScale) {
		throw Err4.arguments3("Vec4i#multiply");
	}

	@Redirect(method = "relative(Lnet/minecraft/core/Direction;I)Lnet/minecraft/core/Vec3i;", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i relative(
		int x, int y, int z,
		@Local(argsOnly = true, name = "direction") Direction direction,
		@Local(argsOnly = true, name = "steps") int steps
	) {
		return Vec4i.from(x, y, z, this.getW() + Direction4.as(direction).getStepW() * steps);
	}

	@Redirect(method = "relative(Lnet/minecraft/core/Direction$Axis;I)Lnet/minecraft/core/Vec3i;", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i relative(
		int x, int y, int z,
		@Local(argsOnly = true, name = "axis") Direction.Axis axis,
		@Local(argsOnly = true, name = "steps") int steps
	) {
		final int wStep = axis == Direction4.Axis.W ? steps : 0;
		return Vec4i.from(x, y, z, this.getW() + wStep);
	}

	@Overwrite
	public Vec3i cross(Vec3i upVector) {
		throw Err4.math("No cross product defined for 4D space");
	}

	// `closerThan` does not need an override.
	// `closerToCenterThan` does not need an override.

	@Redirect(method = "distSqr", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;distToLowCornerSqr(DDD)D"
	))
	double distSqr(
		Vec3i This, double x, double y, double z,
		@Local(argsOnly = true, name = "pos") Vec3i pos
	) {
		return ((Vec4i) This).distToLowCornerSqr(x, y, z, Vec4i.getW(pos));
	}

	@WrapMethod(method = "distToCenterSqr(Lnet/minecraft/core/Position;)D")
	double distToCenterSqr(Position pos, Operation<Double> original) {
		return pos instanceof Vec4 vec4
			? this.distToCenterSqr(pos.x(), pos.y(), pos.z(), vec4.w())
			: original.call(pos);
	}

	@Overwrite
	public double distToCenterSqr(double x, double y, double z) {
		throw Err4.arguments3("Vec4i#distToCenterSqr");
	}

	@Overwrite
	public double distToLowCornerSqr(double x, double y, double z) {
		throw Err4.arguments3("Vec4i#distToLowCornerSqr");
	}

	@Expression("return (int) (@(?))")
	@ModifyExpressionValue(method = "distManhattan", at = @At("MIXINEXTRAS:EXPRESSION"))
	float distManhattan(float d3, @Local(argsOnly = true, name = "pos") Vec3i pos) {
		final float wd = Math.abs(Vec4i.getW(pos) - this.getW());
		return d3 + wd;
	}

	@Expression("return @(?)")
	@ModifyExpressionValue(method = "distChessboard", at = @At("MIXINEXTRAS:EXPRESSION"))
	int distChessboard(int d3, @Local(argsOnly = true, name = "pos") Vec3i pos) {
		final int wd = Math.abs(this.getW() - Vec4i.getW(pos));
		return Math.max(d3, wd);
	}

	@Redirect(method = "get", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction$Axis;choose(III)I"
	))
	int get(Direction.Axis axis, int x, int y, int z) {
		return Direction4.Axis.as(axis).choose(x, y, z, this.getW());
	}

	@Overwrite
	public Vector3i toMutable()	{
		throw Err4.return3(null);
	}

	@Redirect(method = "toString", at = @At(
		value = "INVOKE",
		target = "Lcom/google/common/base/MoreObjects$ToStringHelper;toString()Ljava/lang/String;"
	))
	String toString(MoreObjects.ToStringHelper helper) {
		return helper.add("w", this.getW()).toString();
	}

	@Expression("return @(?)")
	@ModifyExpressionValue(method = "toShortString", at = @At("MIXINEXTRAS:EXPRESSION"))
	String toShortString(String original) {
		return original + ", " + this.getW();
	}
}
