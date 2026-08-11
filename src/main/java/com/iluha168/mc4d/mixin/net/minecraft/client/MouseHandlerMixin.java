package com.iluha168.mc4d.mixin.net.minecraft.client;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.player.LocalPlayer4;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
class MouseHandlerMixin {
	@Redirect(method = "turnPlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
	))
	void turnPlayer(LocalPlayer player, double xo, double yo) {
		// turn_absolute, because mathematically accurate turning modifies yaw and pitch weird at non-zero wRot and vRot
		// Otherwise, mouse movement does not align with the left-right and up-down on the monitor.
		if (MC4DClient.keyMouseAlternativeLook.isDown()) {
			((LocalPlayer4) player).turn_absolute(0.0, 0.0, yo, xo);
		} else {
			((LocalPlayer4) player).turn_absolute(xo, yo, 0.0, 0.0);
		}
	}
}
