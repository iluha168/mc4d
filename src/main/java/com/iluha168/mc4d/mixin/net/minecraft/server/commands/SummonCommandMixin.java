package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SummonCommand.class)
class SummonCommandMixin {
	@Redirect(method = "lambda$createEntity$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	private static void createEntity(Entity entity, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		((Entity4) entity).snapTo(x, y, z, ((Vec4) pos).w, yRot, xRot);
	}
}
