package com.iluha168.mc4d.mixin.net.minecraft.world.entity.animal.happyghast;

import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HappyGhast.class)
class HappyGhastMixin {
	// TODO setServerStillTimeout
	// TODO addPassenger
	// TODO removePassenger

	@ModifyConstant(method = "canAddPassenger", constant = @Constant(intValue = 4))
	int canAddPassenger(int maxPassengers) {
		return 6;
	}

	// TODO getRiddenInput
	// TODO getRiddenRotation
	// TODO tickRidden
	// TODO getQuadLeashHolderOffsets
	// TODO scanPlayerAboveGhast
	// TODO getDismountLocationForPassenger

	// TODO HappyGhastBodyRotationControl
	// TODO HappyGhastLookControl
}
