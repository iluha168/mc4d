package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Relative4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PositionMoveRotation.class)
public class PositionMoveRotationMixin {
	@Shadow
	private static double calculateDelta(double currentDelta, double deltaChange, Set<Relative> relatives, Relative relative) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@ModifyArg(
		method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"),
		index = 0
	)
	private static StreamCodec<ByteBuf, Vec4> codecPosition(StreamCodec<ByteBuf, Vec3> codec1) {
		return Vec4.STREAM_CODEC;
	}

	@ModifyArg(
		method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"),
		index = 2
	)
	private static StreamCodec<ByteBuf, Vec4> codecDeltaMovement(StreamCodec<ByteBuf, Vec3> codec1) {
		return Vec4.STREAM_CODEC;
	}

	@Inject(method = "<init>", at = @At("HEAD"))
	private static void constructor(Vec3 position, Vec3 deltaMovement, float yRot, float xRot, CallbackInfo ci) {
		if (!(position instanceof Vec4 && deltaMovement instanceof Vec4)) {
			throw Err4.container3();
		}
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
		final double w = calculateDelta(((Vec4) rotatedCurrentMovement).w, ((Vec4) change.deltaMovement()).w, relatives, Relative4.DELTA_W);
		return new Vec4(x, y, z, w);
	}
}
