package com.iluha168.mc4d.mixin.net.minecraft.commands;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSourceStack.class)
abstract
class CommandSourceStackMixin {
	@Shadow
	@Final
	private Vec3 worldPosition;

	@Shadow
	@Final
	private Vec2 rotation;

	@ModifyVariable(method = "<init>(Lnet/minecraft/commands/CommandSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/permissions/PermissionSet;Ljava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/commands/CommandResultCallback;Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;Lnet/minecraft/commands/CommandSigningContext;Lnet/minecraft/util/TaskChainer;)V", at = @At("HEAD"), argsOnly = true, name = "rotation")
	private static Vec2 init(Vec2 rotation) {
		if (!(rotation instanceof RotationVec)) {
			throw Err4.container3();
		}
		return rotation;
	}

	@Redirect(method = "withLevel", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 withLevel(double x, double y, double z, @Local(name = "scale") double scale) {
		return new Vec4(x, y, z, ((Vec4) this.worldPosition).w * scale);
	}

	@Redirect(method = "facing(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/commands/CommandSourceStack;", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	Vec2 facing(
		float x, float y,
		@Local(argsOnly = true, name = "pos") Vec3 pos,
		@Local(name = "from") Vec3 from,
		@Local(name = "xd") double xd,
		@Local(name = "yd") double yd,
		@Local(name = "zd") double zd
	) {
		if (!(
			pos instanceof Vec4 pos4 &&
			from instanceof Vec4 from4
		)) throw Err4.container3();
		RotationVec rot = new Vec4(xd, yd, zd, pos4.w - from4.w).rotation();
		return new RotationVec(
			Mth.wrapDegrees(rot.x),
			Mth.wrapDegrees(rot.y),
			Mth.wrapDegrees(rot.w),
			((RotationVec) this.rotation).v // vRot does not affect look direction
		);
	}
}
