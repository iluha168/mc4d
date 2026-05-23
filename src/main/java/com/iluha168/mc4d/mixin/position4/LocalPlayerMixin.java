package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@Redirect(method = "resetPos", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;setPos(DDD)V"
	))
	void resetPos(LocalPlayer player, double x, double y, double z) {
		player.setPos(new Vec4(x, y, z, ((Entity4) this).getW()));
	}
}
