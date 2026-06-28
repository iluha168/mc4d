package com.iluha168.mc4d.mixin.net.minecraft.client.resources.sounds;

import com.iluha168.mc4d.client.resources.sounds.SoundInstance4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSoundInstance.class)
class AbstractSoundInstanceMixin implements SoundInstance4 {
	@Unique protected double w;
	@Unique protected boolean wNotSet;

	@Override
	public double getW() {
		if (this.wNotSet) {
			throw Err4.field4missing("w");
		}
		return this.w;
	}
	@Override
	public void setW(double w) {
		this.w = w;
		this.wNotSet = false;
	}

	@Inject(method = "<init>(Lnet/minecraft/resources/Identifier;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/util/RandomSource;)V", at = @At("TAIL"))
	void init(Identifier identifier, SoundSource source, RandomSource random, CallbackInfo ci) {
		this.wNotSet = true;
	}
}
