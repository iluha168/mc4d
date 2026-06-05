package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.core.Direction4;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

// TODO remove and make 4D rendering
@Mixin(CubeDefinition.class)
class CubeDefinitionMixin {
	@Inject(method = "<init>", at = @At("HEAD"))
	private static void no4D(
		String comment,
		float xTexOffs, float yTexOffs,
		float minX, float minY, float minZ,
		float width, float height, float depth,
		CubeDeformation grow, boolean mirror, float xTexScale, float yTexScale,
		Set<Direction> visibleFaces, CallbackInfo ci
	) {
		visibleFaces.remove(Direction4.KATA);
		visibleFaces.remove(Direction4.ANA);
	}
}
