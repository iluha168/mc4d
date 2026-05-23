package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.MC4D;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(PositionMoveRotation.class)
public class PositionMoveRotationMixin {
	@ModifyArg(
		method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"),
		index = 0
	)
	private static StreamCodec<ByteBuf, Vec4> a(StreamCodec<ByteBuf, Vec3> codec1) {
		return Vec4.STREAM_CODEC;
	}

	@Redirect(method = "calculateAbsolute", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 calculateAbsolute(
		double x, double y, double z,
		@Local(argsOnly = true, name = "source") PositionMoveRotation source,
		@Local(argsOnly = true, name = "change") PositionMoveRotation change,
		@Local(argsOnly = true, name = "relatives") Set<Relative> relatives
	) {
		double offsetW = relatives.contains(MC4D.RelativeW) ? ((Vec4) source.position()).w : 0.0;
		return new Vec4(x, y, z, offsetW + ((Vec4) change.position()).w);
	}
}
