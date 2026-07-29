package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.network.protocol.game.ServerboundUseItemPacket4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
class MultiPlayerGameModeMixin {
	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos init(BlockPos original) {
		Vec4i.setW(original, original.getZ());
		return original;
	}

	@ModifyExpressionValue(method = "lambda$useItem$0", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/InteractionHand;IFF)Lnet/minecraft/network/protocol/game/ServerboundUseItemPacket;"
	))
	ServerboundUseItemPacket useItem(ServerboundUseItemPacket packet, @Local(argsOnly = true, name = "player") Player player) {
		final ServerboundUseItemPacket4 packet4 = (ServerboundUseItemPacket4) packet;
		final Entity4 player4 = (Entity4) player;
		packet4.setWRot(player4.getWRot());
		packet4.setVRot(player4.getVRot());
		return packet;
	}

	@Redirect(method = "interact", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 interact(Vec3 instance, double x, double y, double z, @Local(argsOnly = true, name = "entity") Entity entity) {
		return ((Vec4) instance).subtract(x, y, z, ((Entity4) entity).getW());
	}
}
