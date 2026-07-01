package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundMoveEntityPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundMoveEntityPacket.class)
class ClientboundMoveEntityPacketMixin implements ClientboundMoveEntityPacket4 {
	@Unique private short wa;
	@Unique private boolean waNotSet;

	@Inject(method = "<init>(ISSSBBZZZ)V", at = @At("TAIL"))
	void init(int entityId, short xa, short ya, short za, byte yRot, byte xRot, boolean onGround, boolean hasRot, boolean hasPos, CallbackInfo ci) {
		this.waNotSet = true;
	}

	@Override
	public void setWa(short wa) {
		this.wa = wa;
		this.waNotSet = false;
	}
	@Override
	public short getWa() {
		if (this.waNotSet) throw Err4.field4missing("wa");
		return this.wa;
	}

	@Mixin(ClientboundMoveEntityPacket.Pos.class)
	static class PosMixin extends ClientboundMoveEntityPacketMixin {
		@Inject(method = "read", at = @At("TAIL"))
		private static void read(FriendlyByteBuf input, CallbackInfoReturnable<ClientboundMoveEntityPacket.Pos> cir) {
			((ClientboundMoveEntityPacket4) cir.getReturnValue()).setWa(input.readShort());
		}

		@Inject(method = "write", at = @At("TAIL"))
		void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeShort(this.getWa());
		}
	}

	@Mixin(ClientboundMoveEntityPacket.PosRot.class)
	static class PosRotMixin extends ClientboundMoveEntityPacketMixin {
		@Inject(method = "read", at = @At("TAIL"))
		private static void read(FriendlyByteBuf input, CallbackInfoReturnable<ClientboundMoveEntityPacket.PosRot> cir) {
			((ClientboundMoveEntityPacket4) cir.getReturnValue()).setWa(input.readShort());
		}

		@Inject(method = "write", at = @At("TAIL"))
		void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeShort(this.getWa());
		}
	}
}
