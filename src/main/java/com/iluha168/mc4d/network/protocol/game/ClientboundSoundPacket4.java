package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundSoundPacket}.
 */
public interface ClientboundSoundPacket4 {
	static ClientboundSoundPacket from(
		Holder<SoundEvent> sound, SoundSource source,
		double x, double y, double z, double w,
		float volume, float pitch, long seed
	) {
		ClientboundSoundPacket packet = new ClientboundSoundPacket(sound, source, x, y, z, volume, pitch, seed);
		((ClientboundSoundPacket4) packet).setW(w);
		return packet;
	}

	double getW();
	@ApiStatus.Internal
	void setW(double w);
}
