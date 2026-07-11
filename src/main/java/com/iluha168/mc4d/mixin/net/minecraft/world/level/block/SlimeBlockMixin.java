package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlimeBlock.class)
class SlimeBlockMixin extends BlockMixin {
	@Redirect(method = "bounceUp", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	void bounceUp(Entity entity, double xd, double yd, double zd, @Local(name = "movement") Vec3 movement) {
		entity.setDeltaMovement(new Vec4(xd, yd, zd, ((Vec4) movement).w));
	}

	@Redirect(method = "stepOn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 stepOn(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}
}
