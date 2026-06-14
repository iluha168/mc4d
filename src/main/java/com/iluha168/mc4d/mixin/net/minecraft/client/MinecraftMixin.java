package com.iluha168.mc4d.mixin.net.minecraft.client;

import com.iluha168.mc4d.client.multiplayer.ClientLevel4;
import com.iluha168.mc4d.world.entity.Entity4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
class MinecraftMixin {
	// TODO the rest

	@Shadow
	@Nullable
	public LocalPlayer player;

	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;animateTick(III)V"
	))
	void tick_animateTick(ClientLevel instance, int xt, int yt, int zt) {
		assert this.player != null;
		((ClientLevel4) instance).animateTick(xt, yt, zt, ((Entity4) this.player).getBlockW());
	}

	// TODO the rest
}
