package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundSoundPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSoundPacket.class)
class ClientboundSoundPacketMixin implements ClientboundSoundPacket4 {
	@Unique	private int w;
	@Unique	private boolean wNotSet;

	@Unique
	private int w() {
		if (this.wNotSet) throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public void setW(double w) {
		this.w = (int) (w * 8.0);
		this.wNotSet = false;
	}

	@Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V", at = @At("TAIL"))
	void init(Holder<SoundEvent> sound, SoundSource source, double x, double y, double z, float volume, float pitch, long seed, CallbackInfo ci) {
		this.wNotSet = true;
	}

	@Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
	void init(RegistryFriendlyByteBuf input, CallbackInfo ci) {
		this.w = input.readInt();
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(RegistryFriendlyByteBuf output, CallbackInfo ci) {
		output.writeInt(this.w());
	}

	@Override
	public double getW() {
		return this.w() / 8.0F;
	}
}
