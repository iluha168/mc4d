package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.commands.arguments.coordinates.Coordinates4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.server.commands.RotateCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RotateCommand.class)
class RotateCommandMixin {
	@Redirect(method = "rotate(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/commands/arguments/coordinates/Coordinates;)I", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;forceSetRotation(FZFZ)V"
	))
	private static void rotate(
		Entity entity, float yRot, boolean relativeY, float xRot, boolean relativeX,
		@Local(argsOnly = true, name = "rotation") Coordinates rotation,
		@Local(name = "rot") Vec2 rot
	) {
		final Entity4 entity4 = (Entity4) entity;
		final RotationVec rot4 = (RotationVec) rot;

		final boolean relativeW = rotation.isZRelative();
		final float relativeOrAbsoluteWRot = relativeW ? rot4.w - entity4.getWRot() : rot4.w;

		final boolean relativeV = ((Coordinates4) rotation).isWRelative();
		float relativeOrAbsoluteVRot = relativeV ? rot4.v - entity4.getVRot() : rot4.v;

		entity4.forceSetRotation(yRot, relativeY, xRot, relativeX, relativeOrAbsoluteWRot, relativeW, relativeOrAbsoluteVRot, relativeV);
	}
}
