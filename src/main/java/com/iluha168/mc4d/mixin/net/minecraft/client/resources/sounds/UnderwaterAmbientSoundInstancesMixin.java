package com.iluha168.mc4d.mixin.net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UnderwaterAmbientSoundInstances.class)
class UnderwaterAmbientSoundInstancesMixin {
	@Mixin(UnderwaterAmbientSoundInstances.SubSound.class)
	static class SubSoundMixin extends AbstractSoundInstanceMixin {
		@Inject(method = "<init>", at = @At("TAIL"))
		void init(LocalPlayer player, SoundEvent event, CallbackInfo ci) {
			this.setW(0.0);
		}
	}

	@Mixin(UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance.class)
	static class UnderwaterAmbientSoundInstanceMixin extends AbstractSoundInstanceMixin {
		@Inject(method = "<init>", at = @At("TAIL"))
		void init(LocalPlayer player, CallbackInfo ci) {
			this.setW(0.0);
		}
	}
}
