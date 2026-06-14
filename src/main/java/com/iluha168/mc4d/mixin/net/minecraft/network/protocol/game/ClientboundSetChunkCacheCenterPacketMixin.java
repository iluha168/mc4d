package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundSetChunkCacheCenterPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSetChunkCacheCenterPacket.class)
class ClientboundSetChunkCacheCenterPacketMixin implements ClientboundSetChunkCacheCenterPacket4 {
	@Unique	private int w;
	@Unique	private boolean wNotSet;

	@Inject(method = "<init>(II)V", at = @At("TAIL"))
	void initXZ(int x, int z, CallbackInfo ci) {
		this.wNotSet = true;
	}

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	void initBuffer(FriendlyByteBuf input, CallbackInfo ci) {
		this.wNotSet = true;
		this.setW(input.readVarInt());
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeVarInt(this.getW());
	}

	@Override
	public int getW() {
		if (this.wNotSet) throw Err4.field4missing("w");
		return w;
	}
	@Override
	public void setW(int w) {
		this.wNotSet = false;
		this.w = w;
	}
}
