package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandBlockEntity.class)
class CommandBlockEntityMixin {
	@Mixin(targets = "net.minecraft.world.level.block.entity.CommandBlockEntity$1")
	static class BaseCommandBlockImplMixin {
		@Redirect(method = "createCommandSourceStack", at = @At(
			value = "NEW",
			target = "(FF)Lnet/minecraft/world/phys/Vec2;"
		))
		Vec2 createCommandSourceStack(float x, float y, @Local(name = "facing") Direction facing) {
			return new RotationVec(x, y, Direction4.as(facing).toWRot(), 0.0F);
		}
	}
}
