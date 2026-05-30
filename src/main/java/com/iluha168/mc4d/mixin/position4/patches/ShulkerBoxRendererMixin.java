package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.core.Direction4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxRenderer.class)
public class ShulkerBoxRendererMixin {
	@WrapMethod(method = "createModelTransform")
	private static Transformation createModelTransform(Direction direction, Operation<Transformation> original) {
		// TODO make the renderer 4D
		return direction.getAxis() == Direction4.Axis.W ? null : original.call(direction);
	}
}
