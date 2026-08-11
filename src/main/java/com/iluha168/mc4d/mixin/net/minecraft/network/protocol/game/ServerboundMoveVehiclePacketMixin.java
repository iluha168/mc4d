package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ServerboundMoveVehiclePacket4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.InterpolationHandler4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Function4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(ServerboundMoveVehiclePacket.class)
class ServerboundMoveVehiclePacketMixin implements ServerboundMoveVehiclePacket4 {
	@Unique	private float wRot = Float.NaN;
	@Unique private float vRot = Float.NaN;

	@Override
	public float wRot() {
		if (Float.isNaN(this.wRot)) throw Err4.field4missing("wRot");
		return this.wRot;
	}
	@Override
	public void setWRot(float wRot) {
		this.wRot = wRot;
	}
	@Override
	public float vRot() {
		if (Float.isNaN(this.vRot)) throw Err4.field4missing("vRot");
		return this.vRot;
	}
	@Override
	public void setVRot(float vRot) {
		this.vRot = vRot;
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"
	))
	private static StreamCodec<FriendlyByteBuf, ServerboundMoveVehiclePacket> STREAM_CODEC(
		StreamCodec<FriendlyByteBuf, Vec3> codec1, Function<ServerboundMoveVehiclePacket, Vec3> getter1,
		StreamCodec<FriendlyByteBuf, Float> codec2, Function<ServerboundMoveVehiclePacket, Float> getter2,
		StreamCodec<FriendlyByteBuf, Float> codec3, Function<ServerboundMoveVehiclePacket, Float> getter3,
		StreamCodec<FriendlyByteBuf, Boolean> codec4, Function<ServerboundMoveVehiclePacket, Boolean> getter4,
		Function4<Vec3, Float, Float, Boolean, ServerboundMoveVehiclePacket> constructor
	) {
		return StreamCodec.composite(
			Vec4.STREAM_CODEC, ServerboundMoveVehiclePacket4::position,
			codec2, getter2,
			codec3, getter3,
			codec3, ServerboundMoveVehiclePacket4::wRot,
			codec2, ServerboundMoveVehiclePacket4::vRot,
			codec4, getter4,
			ServerboundMoveVehiclePacket4::from
		);
	}

	@Definition(id = "ServerboundMoveVehiclePacket", type = ServerboundMoveVehiclePacket.class)
	@Definition(id = "yRot", method = "Lnet/minecraft/world/entity/InterpolationHandler;yRot()F")
	@Expression("new ServerboundMoveVehiclePacket(?, ?.yRot(), ?, ?)")
	@ModifyExpressionValue(method = "fromEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ServerboundMoveVehiclePacket fromEntity_interpolating(ServerboundMoveVehiclePacket result, @Local(argsOnly = true, name = "entity") Entity entity) {
		final ServerboundMoveVehiclePacket4 result4 = ServerboundMoveVehiclePacket4.as(result);
		final InterpolationHandler4 interpolation = (InterpolationHandler4) entity.getInterpolation();
		//noinspection DataFlowIssue
		result4.setWRot(interpolation.wRot());
		result4.setVRot(interpolation.vRot());
		return result;
	}
	@Definition(id = "ServerboundMoveVehiclePacket", type = ServerboundMoveVehiclePacket.class)
	@Definition(id = "getYRot", method = "Lnet/minecraft/world/entity/Entity;getYRot()F")
	@Expression("new ServerboundMoveVehiclePacket(?, ?.getYRot(), ?, ?)")
	@ModifyExpressionValue(method = "fromEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ServerboundMoveVehiclePacket fromEntity_notInterpolating(ServerboundMoveVehiclePacket result, @Local(argsOnly = true, name = "entity") Entity entity) {
		final ServerboundMoveVehiclePacket4 result4 = ServerboundMoveVehiclePacket4.as(result);
		final Entity4 entity4 = (Entity4) entity;
		result4.setWRot(entity4.getWRot());
		result4.setVRot(entity4.getVRot());
		return result;
	}
}
