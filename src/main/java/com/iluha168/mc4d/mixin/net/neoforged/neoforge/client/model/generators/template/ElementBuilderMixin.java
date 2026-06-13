package com.iluha168.mc4d.mixin.net.neoforged.neoforge.client.model.generators.template;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO remove and make 4D rendering
@Mixin(ElementBuilder.class)
public class ElementBuilderMixin {
	@Redirect(method = "allFaces", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	Direction[] allFacesDirections3() {
		return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	}

	@Redirect(method = "allFacesExcept", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	Direction[] allFacesExceptDirections3() {
		return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	}
}
