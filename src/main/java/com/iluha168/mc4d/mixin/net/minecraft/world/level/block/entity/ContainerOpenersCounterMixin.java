package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ContainerOpenersCounter.class)
class ContainerOpenersCounterMixin {
	@Redirect(method = "getEntitiesWithContainerOpen", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB getEntitiesWithContainerOpen(BlockPos pos) {
		return new AABB4(pos);
	}
}
