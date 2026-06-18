package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.status;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkStep.Builder.class)
class ChunkStepMixin {
	@WrapMethod(method = "addRequirement")
	ChunkStep.Builder addRequirement(ChunkStatus status, int radius, Operation<ChunkStep.Builder> original) {
		// Here we make chunk generation radius never bigger than 1, because 4D chunks are huge
		return original.call(status, Math.min(radius, 1));
	}
}