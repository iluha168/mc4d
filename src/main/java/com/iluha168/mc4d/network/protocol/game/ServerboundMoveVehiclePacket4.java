package com.iluha168.mc4d.network.protocol.game;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ServerboundMoveVehiclePacket}.
 */
public interface ServerboundMoveVehiclePacket4 {
	static ServerboundMoveVehiclePacket4 as(ServerboundMoveVehiclePacket packet) {
		return (ServerboundMoveVehiclePacket4) (Object) packet;
	}

	float wRot();
	@ApiStatus.Internal
	void setWRot(float wRot);

	float vRot();
	@ApiStatus.Internal
	void setVRot(float vRot);

	static Vec4 position(ServerboundMoveVehiclePacket packet) {
		return ((Vec4) packet.position());
	}
	static float wRot(ServerboundMoveVehiclePacket packet) {
		return ServerboundMoveVehiclePacket4.as(packet).wRot();
	}
	static float vRot(ServerboundMoveVehiclePacket packet) {
		return ServerboundMoveVehiclePacket4.as(packet).vRot();
	}

	static ServerboundMoveVehiclePacket from(Vec4 position, float yRot, float xRot, float wRot, float vRot, boolean onGround) {
		final ServerboundMoveVehiclePacket packet = new ServerboundMoveVehiclePacket(position, yRot, xRot, onGround);
		final ServerboundMoveVehiclePacket4 packet4 = ServerboundMoveVehiclePacket4.as(packet);
		packet4.setWRot(wRot);
		packet4.setVRot(vRot);
		return packet;
	}
}
