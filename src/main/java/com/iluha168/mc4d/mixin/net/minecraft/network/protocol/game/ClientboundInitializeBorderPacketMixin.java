package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundInitializeBorderPacket4;
import com.iluha168.mc4d.world.level.border.WorldBorder4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundInitializeBorderPacket.class)
class ClientboundInitializeBorderPacketMixin implements ClientboundInitializeBorderPacket4 {
	@Unique	private double newCenterW;

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	void init(FriendlyByteBuf input, CallbackInfo ci) {
		this.newCenterW = input.readDouble();
	}

	@Inject(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At("TAIL"))
	void init(WorldBorder border, CallbackInfo ci) {
		this.newCenterW = ((WorldBorder4) border).getCenterW();
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeDouble(this.newCenterW);
	}

	@Override
	public double getNewCenterW() {
		return this.newCenterW;
	}
}
