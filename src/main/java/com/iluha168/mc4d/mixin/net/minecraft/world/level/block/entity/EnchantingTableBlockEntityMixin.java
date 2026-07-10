package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.EntityGetter4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantingTableBlockEntity.class)
class EnchantingTableBlockEntityMixin {
	@Redirect(method = "bookAnimationTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;getNearestPlayer(DDDDZ)Lnet/minecraft/world/entity/player/Player;"
	))
	private static Player bookAnimationTick(
		Level instance, double x, double y, double z, double maxDist, boolean filterOutCreative,
		@Local(argsOnly = true, name = "worldPosition") BlockPos worldPosition
	) {
		return ((EntityGetter4) instance).getNearestPlayer(x, y, z, Vec4i.getW(worldPosition) + 0.5, maxDist, filterOutCreative);
	}
	// TODO bookAnimationTick when 4D renderer
}
