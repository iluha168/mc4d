package com.iluha168.mc4d.mixin.net.minecraft.client.resources.model.cuboid;

import com.iluha168.mc4d.core.Direction4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

// TODO remove and make 4D rendering
@Mixin(MissingCuboidModel.class)
public class MissingCuboidModelMixin {
	@ModifyExpressionValue(method = "missingModel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Util;makeEnumMap(Ljava/lang/Class;Ljava/util/function/Function;)Ljava/util/Map;"
	))
	private static Map<Direction, CuboidFace> makeEnumMap3(Map<Direction, CuboidFace> faces) {
		faces.remove(Direction4.KATA);
		faces.remove(Direction4.ANA);
		return faces;
	}
}
