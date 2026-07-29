package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundMoveEntityPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.util.Mth;
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
	@Unique private byte wRot;
	@Unique private boolean wRotNotSet;
	@Unique private byte vRot;
	@Unique private boolean vRotNotSet;

	@Inject(method = "<init>(ISSSBBZZZ)V", at = @At("TAIL"))
	void init(int entityId, short xa, short ya, short za, byte yRot, byte xRot, boolean onGround, boolean hasRot, boolean hasPos, CallbackInfo ci) {
		this.waNotSet = true;
		this.wRotNotSet = true;
		this.vRotNotSet = true;
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
	@Override
	public void setWRotPacked(byte wRot) {
		this.wRot = wRot;
		this.wRotNotSet = false;
	}
	@Override
	public byte getWRotPacked() {
		if (this.wRotNotSet) throw Err4.field4missing("wRot");
		return this.wRot;
	}
	@Override
	public float getWRot() {
		return Mth.unpackDegrees(this.getWRotPacked());
	}
	@Override
	public void setVRotPacked(byte vRot) {
		this.vRot = vRot;
		this.vRotNotSet = false;
	}
	@Override
	public byte getVRotPacked() {
		if (this.vRotNotSet) throw Err4.field4missing("vRot");
		return this.vRot;
	}
	@Override
	public float getVRot() {
		return Mth.unpackDegrees(this.getVRotPacked());
	}

	@Mixin(ClientboundMoveEntityPacket.Pos.class)
	static class PosMixin extends ClientboundMoveEntityPacketMixin {
		@Inject(method = "<init>(ISSSZ)V", at = @At("TAIL"))
		void init(int id, short xa, short ya, short za, boolean onGround, CallbackInfo ci) {
			this.setWRotPacked((byte) 0);
			this.setVRotPacked((byte) 0);
		}

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
			final ClientboundMoveEntityPacket4 packet = (ClientboundMoveEntityPacket4) cir.getReturnValue();
			packet.setWa(input.readShort());
			packet.setWRotPacked(input.readByte());
			packet.setVRotPacked(input.readByte());
		}

		@Inject(method = "write", at = @At("TAIL"))
		void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeShort(this.getWa());
			output.writeByte(this.getWRotPacked());
			output.writeByte(this.getVRotPacked());
		}
	}

	@Mixin(ClientboundMoveEntityPacket.Rot.class)
	static class RotMixin extends ClientboundMoveEntityPacketMixin {
		@Inject(method = "<init>(IBBZ)V", at = @At("TAIL"))
		void init(int id, byte yRot, byte xRot, boolean onGround, CallbackInfo ci) {
			this.setWa((short) 0);
		}

		@Inject(method = "read", at = @At("TAIL"))
		private static void read(FriendlyByteBuf input, CallbackInfoReturnable<ClientboundMoveEntityPacket.Rot> cir) {
			final ClientboundMoveEntityPacket4 packet = (ClientboundMoveEntityPacket4) cir.getReturnValue();
			packet.setWRotPacked(input.readByte());
			packet.setVRotPacked(input.readByte());
		}

		@Inject(method = "write", at = @At("TAIL"))
		void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeByte(this.getWRotPacked());
			output.writeByte(this.getVRotPacked());
		}
	}
}
