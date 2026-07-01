package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.EntityGetter4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(EntityGetter.class)
interface EntityGetterMixin extends EntityGetter4 {
	@Overwrite
	@Deprecated
	default @Nullable Player getNearestPlayer(double x, double y, double z, double range, @Nullable Predicate<Entity> predicate) {
		throw Err4.arguments3("EntityGetter4#getNearestPlayer");
	}

	@Redirect(method = "getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/EntityGetter;getNearestPlayer(DDDDZ)Lnet/minecraft/world/entity/player/Player;"
	))
	default @Nullable Player getNearestPlayer_source(EntityGetter This, double x, double y, double z, double maxDist, boolean filterOutCreative, @Local(argsOnly = true, name = "source") Entity source) {
		return ((EntityGetter4) This).getNearestPlayer(x, y, z, ((Entity4) source).getW(), maxDist, filterOutCreative);
	}

	@Overwrite
	@Deprecated
	default @Nullable Player getNearestPlayer(double x, double y, double z, double maxDist, boolean filterOutCreative) {
		throw Err4.arguments3("EntityGetter4#getNearestPlayer");
	}

	@Overwrite
	@Deprecated
	default boolean hasNearbyAlivePlayer(double x, double y, double z, double range) {
		throw Err4.arguments3("EntityGetter4#hasNearbyAlivePlayer");
	}
}
