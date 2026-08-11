package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundPlayerRotationPacket}.
 */
public interface ClientboundPlayerRotationPacket4 {
	float wRot();
	@ApiStatus.Internal
	void setWRot(float wRot);

	boolean relativeW();
	@ApiStatus.Internal
	void setRelativeW(boolean relativeW);

	float vRot();
	@ApiStatus.Internal
	void setVRot(float vRot);

	boolean relativeV();
	@ApiStatus.Internal
	void setRelativeV(boolean relativeV);

	static ClientboundPlayerRotationPacket4 as(ClientboundPlayerRotationPacket packet) {
		return (ClientboundPlayerRotationPacket4) (Object) packet;
	}

	static float wRot(ClientboundPlayerRotationPacket packet) {
		return ClientboundPlayerRotationPacket4.as(packet).wRot();
	}
	static boolean relativeW(ClientboundPlayerRotationPacket packet) {
		return ClientboundPlayerRotationPacket4.as(packet).relativeW();
	}
	static float vRot(ClientboundPlayerRotationPacket packet) {
		return ClientboundPlayerRotationPacket4.as(packet).vRot();
	}
	static boolean relativeV(ClientboundPlayerRotationPacket packet) {
		return ClientboundPlayerRotationPacket4.as(packet).relativeV();
	}

	static ClientboundPlayerRotationPacket from(float yRot, boolean relativeY, float xRot, boolean relativeX, float wRot, boolean relativeW, float vRot, boolean relativeV) {
		final ClientboundPlayerRotationPacket packet = new ClientboundPlayerRotationPacket(yRot, relativeY, xRot, relativeX);
		final ClientboundPlayerRotationPacket4 packet4 = ClientboundPlayerRotationPacket4.as(packet);
		packet4.setWRot(wRot);
		packet4.setRelativeW(relativeW);
		packet4.setVRot(vRot);
		packet4.setRelativeV(relativeV);
		return packet;
	}
}
