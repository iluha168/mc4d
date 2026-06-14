package com.iluha168.mc4d.mixin.net.minecraft.world.attribute;

import com.iluha168.mc4d.world.level.biome.BiomeManager4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnvironmentAttributeSystem.class)
class EnvironmentAttributeSystemMixin {
	@Redirect(method = "lambda$addBiomeLayerForAttribute$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/biome/BiomeManager;getNoiseBiomeAtPosition(DDD)Lnet/minecraft/core/Holder;"
	))
	private static Holder<Biome> addBiomeLayerForAttribute(
		BiomeManager instance, double x, double y, double z,
		@Local(argsOnly = true, name = "pos") Vec3 pos
	) {
		return ((BiomeManager4) instance).getNoiseBiomeAtPosition(x, y, z, ((Vec4) pos).w);
	}
}
