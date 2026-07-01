package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.network.protocol.game.ClientboundMoveEntityPacket4;
import com.iluha168.mc4d.network.protocol.game.VecDeltaCodec4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
abstract class ServerEntityMixin {
	@Shadow
	@Final
	private VecDeltaCodec positionCodec;

	// TODO everything else

	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;getRequiresPrecisePosition()Z"
	))
	boolean sendChanges_deltaTooBig(
		boolean requiresPrecisePosition,
		@Local(name = "currentPosition") Vec3 currentPosition,
		@Share("wa") LocalLongRef wa
	) {
		wa.set(((VecDeltaCodec4) this.positionCodec).encodeW((Vec4) currentPosition));
		return requiresPrecisePosition || wa.get() < -32768L || wa.get() > 32767L;
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "NEW",
		target = "(ISSSZ)Lnet/minecraft/network/protocol/game/ClientboundMoveEntityPacket$Pos;"
	))
	ClientboundMoveEntityPacket.Pos sendChanges_pos(ClientboundMoveEntityPacket.Pos packet, @Share("wa") LocalLongRef wa) {
		((ClientboundMoveEntityPacket4) packet).setWa((short) wa.get());
		return packet;
	}
	@ModifyExpressionValue(method = "sendChanges", at = @At(
		value = "NEW",
		target = "(ISSSBBZ)Lnet/minecraft/network/protocol/game/ClientboundMoveEntityPacket$PosRot;"
	))
	ClientboundMoveEntityPacket.PosRot sendChanges_posRot(ClientboundMoveEntityPacket.PosRot packet, @Share("wa") LocalLongRef wa) {
		((ClientboundMoveEntityPacket4) packet).setWa((short) wa.get());
		return packet;
	}

	// TODO everything else
}
