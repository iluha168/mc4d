package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundRotateHeadPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundRotateHeadPacket.class)
class ClientboundRotateHeadPacketMixin implements ClientboundRotateHeadPacket4 {
	@Unique private byte wHeadRot;
	@Unique private boolean wHeadRotNotSet = true;

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	void init(FriendlyByteBuf input, CallbackInfo ci) {
		this.setWHeadRotPacked(input.readByte());
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeByte(this.getWHeadRotPacked());
	}

	@Override
	public void setWHeadRotPacked(byte wHeadRot) {
		this.wHeadRot = wHeadRot;
		this.wHeadRotNotSet = false;
	}
	@Override
	public byte getWHeadRotPacked() {
		if (this.wHeadRotNotSet) throw Err4.field4missing("wHeadRot");
		return this.wHeadRot;
	}
	@Override
	public float getWHeadRot() {
		return Mth.unpackDegrees(this.getWHeadRotPacked());
	}
}
