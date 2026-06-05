package com.iluha168.mc4d.mixin.soundInstance4;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientLevel.class)
class ClientLevelMixin {
	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of 4D sound engine
	 */
	@Overwrite
	private void playSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed) {

	}
}
