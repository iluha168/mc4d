package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.network.protocol.game.ClientboundSoundPacket4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlaySoundCommand.class)
class PlaySoundCommandMixin {
	@Definition(id = "distSqr", local = @Local(type = double.class, name = "distSqr"))
	@Expression("distSqr = @(?)")
	@ModifyExpressionValue(method = "playSound", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static double playSound_distSqr(
		double original,
		@Local(argsOnly = true, name = "position") Vec3 position,
		@Local(name = "player") ServerPlayer player,
		@Share("deltaW") LocalDoubleRef deltaW
	) {
		deltaW.set(((Vec4) position).w - ((Entity4) player).getW());
		return original + deltaW.get() * deltaW.get();
	}
	@Redirect(method = "playSound", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 playSound_localPosition(
		double x, double y, double z,
		@Local(name = "player") ServerPlayer player,
		@Share("deltaW") LocalDoubleRef deltaW,
		@Local(name = "distance") double distance
	) {
		return new Vec4(x, y, z, ((Entity4) player).getW() + deltaW.get() / distance * 2.0);
	}
	@ModifyExpressionValue(method = "playSound", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)Lnet/minecraft/network/protocol/game/ClientboundSoundPacket;"
	))
	private static ClientboundSoundPacket playSound_packet(ClientboundSoundPacket original, @Local(name = "localPosition") Vec3 localPosition) {
		((ClientboundSoundPacket4) original).setW(((Vec4) localPosition).w());
		return original;
	}
}
