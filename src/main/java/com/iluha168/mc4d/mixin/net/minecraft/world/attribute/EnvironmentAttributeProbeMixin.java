package com.iluha168.mc4d.mixin.net.minecraft.world.attribute;

import com.iluha168.mc4d.world.attribute.GaussianSampler4;
import com.iluha168.mc4d.world.level.biome.BiomeManager4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.GaussianSampler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnvironmentAttributeProbe.class)
class EnvironmentAttributeProbeMixin {
	@Shadow
	private @Nullable Level level;

	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/attribute/GaussianSampler;sample(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/attribute/GaussianSampler$Sampler;Lnet/minecraft/world/attribute/GaussianSampler$Accumulator;)V"
	))
	void sample(Vec3 position, GaussianSampler.Sampler<Holder<Biome>> sampler, GaussianSampler.Accumulator<Holder<Biome>> accumulator) {
		assert level != null;
		GaussianSampler4.sample(
			(Vec4) position,
			((BiomeManager4) level.getBiomeManager())::getNoiseBiomeAtQuart,
			accumulator
		);
	}
}
