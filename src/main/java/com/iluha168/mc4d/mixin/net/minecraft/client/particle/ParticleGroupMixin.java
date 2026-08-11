package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ParticleGroup.class)
class ParticleGroupMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 16384))
	private int init(int maxParticles) {
		// Increase the cap, because we usually create more particles than vanilla and only a fraction of them is actually rendered.
		return maxParticles * LevelChunkSection.SECTION_WIDTH;
	}
}
