package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
class ServerPlayerMixin {
	@Definition(id = "lastKnownClientMovement", field = "Lnet/minecraft/server/level/ServerPlayer;lastKnownClientMovement:Lnet/minecraft/world/phys/Vec3;")
	@Expression("this.lastKnownClientMovement = @(?)")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Vec3 lastKnownClientMovement(Vec3 original) {
		return Vec4.ZERO;
	}

	@Inject(method = "setKnownMovement", at = @At("HEAD"))
	void setKnownMovement(Vec3 lastKnownClientMovement, CallbackInfo ci) {
		if (!(lastKnownClientMovement instanceof Vec4)) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: supply a Vec4."));
		}
	}
}
