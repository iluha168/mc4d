package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ServerboundMovePlayerPacket4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundMovePlayerPacket.class)
public class ServerboundMovePlayerPacketMixin implements ServerboundMovePlayerPacket4 {
	@Shadow
	@Final
	protected boolean hasPos;
	@Shadow
	@Final
	protected boolean hasRot;

	@Unique	private double w = Double.NaN;
	@Unique protected double w() {
		if (Double.isNaN(this.w)) throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public void setW(double w) {
		this.w = w;
	}

	@Unique	private float wRot = Float.NaN;
	@Unique protected float wRot() {
		if (Float.isNaN(this.wRot)) throw Err4.field4missing("wRot");
		return this.wRot;
	}
	@Override
	public void setWRot(float wRot) {
		this.wRot = wRot;
	}

	@Unique	private float vRot = Float.NaN;
	@Unique protected float vRot() {
		if (Float.isNaN(this.vRot)) throw Err4.field4missing("vRot");
		return this.vRot;
	}
	@Override
	public void setVRot(float vRot) {
		this.vRot = vRot;
	}

	@Override
	public double getW(double fallback) {
		return this.hasPos ? this.w() : fallback;
	}
	@Override
	public float getWRot(float fallback) {
		return this.hasRot ? this.wRot() : fallback;
	}
	@Override
	public float getVRot(float fallback) {
		return this.hasRot ? this.vRot() : fallback;
	}

	@Mixin(ServerboundMovePlayerPacket.Pos.class)
	public static class PosMixin extends ServerboundMovePlayerPacketMixin {
		@Inject(method = "<init>(Lnet/minecraft/world/phys/Vec3;ZZ)V", at = @At("TAIL"))
		private void init(Vec3 pos, boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			this.setW(((Vec4) pos).w);
			this.setWRot(0.0F);
			this.setVRot(0.0F);
		}

		@Inject(method = "<init>(DDDZZ)V", at = @At("TAIL"))
		private void init(double x, double y, double z, boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			this.setWRot(0.0F);
			this.setVRot(0.0F);
		}

		@ModifyExpressionValue(method = "read", at = @At(
			value = "NEW",
			target = "(DDDZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$Pos;"
		))
		private static ServerboundMovePlayerPacket.Pos read(ServerboundMovePlayerPacket.Pos packet, @Local(argsOnly = true, name = "input") FriendlyByteBuf input) {
			((ServerboundMovePlayerPacket4) packet).setW(input.readDouble());
			return packet;
		}

		@Inject(method = "write", at = @At("TAIL"))
		private void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeDouble(this.w());
		}
	}

	@Mixin(ServerboundMovePlayerPacket.PosRot.class)
	public static class PosRotMixin extends ServerboundMovePlayerPacketMixin {
		@Inject(method = "<init>(Lnet/minecraft/world/phys/Vec3;FFZZ)V", at = @At("TAIL"))
		private void init(Vec3 pos, float yRot, float xRot, boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			this.setW(((Vec4) pos).w);
		}

		@ModifyExpressionValue(method = "read", at = @At(
			value = "NEW",
			target = "(DDDFFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$PosRot;"
		))
		private static ServerboundMovePlayerPacket.PosRot read(ServerboundMovePlayerPacket.PosRot packet, @Local(argsOnly = true, name = "input") FriendlyByteBuf input) {
			final ServerboundMovePlayerPacket4 packet4 = (ServerboundMovePlayerPacket4) packet;
			packet4.setW(input.readDouble());
			packet4.setWRot(input.readFloat());
			packet4.setVRot(input.readFloat());
			return packet;
		}

		@Inject(method = "write", at = @At("TAIL"))
		private void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeDouble(this.w());
			output.writeFloat(this.wRot());
			output.writeFloat(this.vRot());
		}
	}

	@Mixin(ServerboundMovePlayerPacket.Rot.class)
	public static class RotMixin extends ServerboundMovePlayerPacketMixin {
		@Inject(method = "<init>", at = @At("TAIL"))
		private void init(float yRot, float xRot, boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			this.setW(0.0);
		}

		@ModifyExpressionValue(method = "read", at = @At(
			value = "NEW",
			target = "(FFZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$Rot;"
		))
		private static ServerboundMovePlayerPacket.Rot read(ServerboundMovePlayerPacket.Rot packet, @Local(argsOnly = true, name = "input") FriendlyByteBuf input) {
			final ServerboundMovePlayerPacket4 packet4 = (ServerboundMovePlayerPacket4) packet;
			packet4.setWRot(input.readFloat());
			packet4.setVRot(input.readFloat());
			return packet;
		}

		@Inject(method = "write", at = @At("TAIL"))
		private void write(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeFloat(this.wRot());
			output.writeFloat(this.vRot());
		}
	}

	@Mixin(ServerboundMovePlayerPacket.StatusOnly.class)
	public static class StatusOnlyMixin extends ServerboundMovePlayerPacketMixin {
		@Inject(method = "<init>", at = @At("TAIL"))
		private void init(boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			this.setW(0.0);
			this.setWRot(0.0F);
			this.setVRot(0.0F);
		}
	}
}
