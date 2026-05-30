package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {
	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB pickupArea(AABB instance, double xAdd, double yAdd, double zAdd) {
		assert xAdd == zAdd;
		return ((AABB4) instance).inflate(xAdd, yAdd, zAdd, xAdd);
	}
}
