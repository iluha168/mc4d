package com.iluha168.mc4d.mixin.net.minecraft.client.player;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.world.entity.player.Input4;
import com.iluha168.mc4d.world.phys.HorizontalVec;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
class KeyboardInputMixin extends ClientInputMixin {
	@Shadow
	private static float calculateImpulse(boolean positive, boolean negative) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@ModifyExpressionValue(method = "tick", at = @At(
		value = "NEW",
		target = "(ZZZZZZZ)Lnet/minecraft/world/entity/player/Input;"
	))
	Input tick_keyPresses(Input original) {
		Input4 keyPresses = Input4.as(original);
		keyPresses.setAna(MC4DClient.keyAna.isDown());
		keyPresses.setKata(MC4DClient.keyKata.isDown());
		return original;
	}

	@Redirect(method = "tick", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	Vec2 tick_moveVector(float x, float y) {
		Input4 keyPresses4 = Input4.as(this.keyPresses);
		final float anaImpulse = calculateImpulse(keyPresses4.ana(), keyPresses4.kata());
		return new HorizontalVec(x, y, anaImpulse);
	}
}
