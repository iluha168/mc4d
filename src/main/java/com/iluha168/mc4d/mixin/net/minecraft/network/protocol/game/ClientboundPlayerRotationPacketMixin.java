package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundPlayerRotationPacket4;
import com.iluha168.mc4d.util.Err4;
import com.mojang.datafixers.util.Function4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(ClientboundPlayerRotationPacket.class)
class ClientboundPlayerRotationPacketMixin implements ClientboundPlayerRotationPacket4 {
	@Unique	private float wRot = Float.NaN;
	@Unique private float vRot = Float.NaN;
	@Unique	private boolean relativeW, relativeV;
	@Unique	private boolean relativeWNotSet = true, relativeVNotSet = true;

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
	public boolean relativeW() {
		if (this.relativeWNotSet) throw Err4.field4missing("relativeW");
		return this.relativeW;
	}
	@Override
	public void setRelativeW(boolean relativeW) {
		this.relativeW = relativeW;
		this.relativeWNotSet = false;
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
	@Override
	public boolean relativeV() {
		if (this.relativeVNotSet) throw Err4.field4missing("relativeV");
		return this.relativeV;
	}
	@Override
	public void setRelativeV(boolean relativeV) {
		this.relativeV = relativeV;
		this.relativeVNotSet = false;
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"
	))
	private static StreamCodec<FriendlyByteBuf, ClientboundPlayerRotationPacket> STREAM_CODEC(
		StreamCodec<FriendlyByteBuf, Float> codec1, Function<ClientboundPlayerRotationPacket, Float> getter1,
		StreamCodec<FriendlyByteBuf, Boolean> codec2, Function<ClientboundPlayerRotationPacket, Boolean> getter2,
		StreamCodec<FriendlyByteBuf, Float> codec3, Function<ClientboundPlayerRotationPacket, Float> getter3,
		StreamCodec<FriendlyByteBuf, Boolean> codec4, Function<ClientboundPlayerRotationPacket, Boolean> getter4,
		Function4<Float, Boolean, Float, Boolean, ClientboundPlayerRotationPacket> constructor
	) {
		return StreamCodec.composite(
			codec1, getter1,
			codec2, getter2,
			codec3, getter3,
			codec4, getter4,
			codec1, ClientboundPlayerRotationPacket4::wRot,
			codec2, ClientboundPlayerRotationPacket4::relativeW,
			codec3, ClientboundPlayerRotationPacket4::vRot,
			codec4, ClientboundPlayerRotationPacket4::relativeV,
			ClientboundPlayerRotationPacket4::from
		);
	}
}
