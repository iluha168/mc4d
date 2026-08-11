package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundPlayerLookAtPacket4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundPlayerLookAtPacket.class)
class ClientboundPlayerLookAtPacketMixin implements ClientboundPlayerLookAtPacket4 {
	@Unique
	private double w = Double.NaN;

	@Inject(method = "<init>(Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;)V", at = @At("TAIL"))
	void init(CallbackInfo ci, @Local(name = "pos") Vec3 pos) {
		this.w = ((Vec4) pos).w;
	}

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	void init(FriendlyByteBuf input, CallbackInfo ci) {
		this.w = input.readDouble();
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeDouble(this.getW());
	}

	@Redirect(method = "getPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition(double x, double y, double z) {
		return new Vec4(x, y, z, this.getW());
	}

	@Override
	public void setW(double w) {
		this.w = w;
	}
	@Override
	public double getW() {
		if (Double.isNaN(this.w)) throw Err4.field4missing("w");
		return this.w;
	}
}
