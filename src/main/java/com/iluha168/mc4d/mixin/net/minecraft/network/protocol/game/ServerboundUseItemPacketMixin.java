package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ServerboundUseItemPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundUseItemPacket.class)
class ServerboundUseItemPacketMixin implements ServerboundUseItemPacket4 {
	@Unique
	private float wRot = Float.NaN;
	@Unique
	private float vRot = Float.NaN;

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	private void init(FriendlyByteBuf input, CallbackInfo ci) {
		this.wRot = input.readFloat();
		this.vRot = input.readFloat();
	}

	@Inject(method = "write", at = @At("TAIL"))
	private void write(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeFloat(this.getWRot());
		output.writeFloat(this.getVRot());
	}

	@Override
	public float getWRot() {
		if (Float.isNaN(this.wRot)) throw Err4.field4missing("wRot");
		return this.wRot;
	}
	@Override
	public void setWRot(float wRot) {
		this.wRot = wRot;
	}
	@Override
	public float getVRot() {
		if (Float.isNaN(this.vRot)) throw Err4.field4missing("vRot");
		return this.vRot;
	}
	@Override
	public void setVRot(float vRot) {
		this.vRot = vRot;
	}
}
