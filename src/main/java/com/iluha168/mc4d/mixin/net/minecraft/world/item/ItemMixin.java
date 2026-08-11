package com.iluha168.mc4d.mixin.net.minecraft.world.item;

import com.iluha168.mc4d.MC4D;
import com.iluha168.mc4d.world.entity.Entity4;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
class ItemMixin {
	@Redirect(method = "getPlayerPOVHitResult", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 getPlayerPOVHitResult(Player player, float xRot, float yRot) {
		// The server must predict the client's POV the same way as the client.
		return ((Entity4) player).calculateViewVector(xRot, yRot, MC4D.getCameraSliceWRot());
	}
}
