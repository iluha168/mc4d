package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.stateproviders;

import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.synth.NormalNoise4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseBasedStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NoiseBasedStateProvider.class)
class NoiseBasedStateProviderMixin {
	@Redirect(method = "getNoiseValue", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/synth/NormalNoise;getValue(DDD)D"
	))
	private double getNoiseValue(
		NormalNoise noise, double x, double y, double z,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "scale") double scale
	) {
		return ((NormalNoise4) noise).getValue(x, y, z, Vec4i.getW(pos) * scale);
	}
}
