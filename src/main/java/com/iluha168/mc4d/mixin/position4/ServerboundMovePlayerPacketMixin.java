package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.network.protocol.game.ServerboundMovePlayerPacket4;
import com.iluha168.mc4d.world.phys.Vec4;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundMovePlayerPacket.class)
public class ServerboundMovePlayerPacketMixin implements ServerboundMovePlayerPacket4 {
	@Shadow
	@Final
	protected boolean hasPos;

	@Unique
	protected double w;

	@Override
	public double getW(double fallback) {
		return this.hasPos ? this.w : fallback;
	}

	@Mixin(ServerboundMovePlayerPacket.Pos.class)
	public static abstract class PosMixin extends ServerboundMovePlayerPacketMixin {
		@Inject(method = "<init>(DDDZZ)V", at = @At("HEAD"))
		private static void remove3ArgsConstructor(double x, double y, double z, boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			throw new IllegalArgumentException("Not patched 3D space: use new Pos(Vec3, ...) instead.");
		}

		@Inject(method = "<init>(Lnet/minecraft/world/phys/Vec3;ZZ)V", at = @At("TAIL"))
		private void storeWInConstructor(Vec3 pos, boolean onGround, boolean horizontalCollision, CallbackInfo ci) {
			this.w = ((Vec4) pos).w;
		}

		@Redirect(method = "read", at = @At(
			value = "NEW",
			target = "(DDDZZ)Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket$Pos;"
		))
		private static ServerboundMovePlayerPacket.Pos readW(
			double x, double y, double z, boolean onGround, boolean horizontalCollision,
			@Local(argsOnly = true, name = "input") FriendlyByteBuf input
		) {
			final double w = input.readDouble();
			return new ServerboundMovePlayerPacket.Pos(new Vec4(x, y, z, w), onGround, horizontalCollision);
		}

		@Inject(method = "write", at = @At("TAIL"))
		private void writeW(FriendlyByteBuf output, CallbackInfo ci) {
			output.writeDouble(this.w);
		}
	}
}
