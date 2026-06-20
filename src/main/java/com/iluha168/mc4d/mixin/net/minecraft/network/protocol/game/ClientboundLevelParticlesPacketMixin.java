package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundLevelParticlesPacket4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundLevelParticlesPacket.class)
class ClientboundLevelParticlesPacketMixin implements ClientboundLevelParticlesPacket4 {
	@Unique	private double w;
	@Unique	private boolean wNotSet;
	@Unique	private float wDist;
	@Unique	private boolean wDistNotSet;

	@Inject(method = "<init>(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDFFFFI)V", at = @At("TAIL"))
	void init(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, float xDist, float yDist, float zDist, float maxSpeed, int count, CallbackInfo ci) {
		this.wNotSet = true;
		this.wDistNotSet = true;
	}

	@Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
	void init(RegistryFriendlyByteBuf input, CallbackInfo ci) {
		this.setW(input.readDouble());
		this.setWDist(input.readFloat());
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(RegistryFriendlyByteBuf output, CallbackInfo ci) {
		output.writeDouble(this.getW());
		output.writeFloat(this.getWDist());
	}

	@Override
	public double getW() {
		if (this.wNotSet) throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public void setW(double w) {
		this.wNotSet = false;
		this.w = w;
	}

	@Override
	public float getWDist() {
		if (this.wDistNotSet) throw Err4.field4missing("wDist");
		return this.wDist;
	}
	@Override
	public void setWDist(float wDist) {
		this.wDistNotSet = false;
		this.wDist = wDist;
	}
}
