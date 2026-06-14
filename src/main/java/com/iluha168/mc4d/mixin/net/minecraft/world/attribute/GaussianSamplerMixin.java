package com.iluha168.mc4d.mixin.net.minecraft.world.attribute;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.world.attribute.GaussianSampler;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GaussianSampler.class)
class GaussianSamplerMixin {
	@Overwrite
	@Deprecated
	public static <V> void sample(Vec3 position, GaussianSampler.Sampler<V> sampler, GaussianSampler.Accumulator<V> accumulator) {
		throw Err4.arguments3("GaussianSampler4#sample");
	}
}
