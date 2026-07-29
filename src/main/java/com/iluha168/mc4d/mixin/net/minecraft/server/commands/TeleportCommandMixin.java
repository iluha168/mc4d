package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.commands.arguments.coordinates.Coordinates4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.Relative4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.LookAt;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(TeleportCommand.class)
class TeleportCommandMixin {
	@Redirect(method = "teleportToEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/commands/TeleportCommand;performTeleport(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFLnet/minecraft/server/commands/LookAt;)V"
	))
	private static void teleportToEntity(
		CommandSourceStack source, Entity victim, ServerLevel level, double x, double y, double z, Set<Relative> relatives, float yRot, float xRot, @Nullable LookAt lookAt,
		@Local(argsOnly = true, name = "destination") Entity destination
	) throws CommandSyntaxException {
		final Entity4 destination4 = (Entity4) destination;
		performTeleport(
			source, victim, level,
			x, y, z, destination4.getW(),
			relatives, yRot, xRot, destination4.getWRot(), destination4.getVRot(), lookAt
		);
	}

	@Definition(id = "performTeleport", method = "Lnet/minecraft/server/commands/TeleportCommand;performTeleport(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFLnet/minecraft/server/commands/LookAt;)V")
	@Definition(id = "getXRot", method = "Lnet/minecraft/world/entity/Entity;getXRot()F")
	@Expression("performTeleport(?, ?, ?, ?, ?, ?, ?, ?, ?.getXRot(), ?)")
	@Redirect(method = "teleportToPos", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void teleportToPos_withoutRot(
		CommandSourceStack source, Entity victim, ServerLevel level, double x, double y, double z, Set<Relative> relatives, float yRot, float xRot, @Nullable LookAt lookAt,
		@Local(name = "pos") Vec3 pos
	) throws CommandSyntaxException {
		Entity4 victim4 = (Entity4) victim;
		performTeleport(
			source, victim, level,
			x, y, z, ((Vec4) pos).w,
			relatives,
			yRot, xRot, victim4.getWRot(), victim4.getVRot(),
			lookAt
		);
	}
	@Definition(id = "performTeleport", method = "Lnet/minecraft/server/commands/TeleportCommand;performTeleport(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFLnet/minecraft/server/commands/LookAt;)V")
	@Definition(id = "x", field = "Lnet/minecraft/world/phys/Vec2;x:F")
	@Expression("performTeleport(?, ?, ?, ?, ?, ?, ?, ?, ?.x, ?)")
	@Redirect(method = "teleportToPos", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void teleportToPos(
		CommandSourceStack source, Entity victim, ServerLevel level, double x, double y, double z, Set<Relative> relatives, float yRot, float xRot, @Nullable LookAt lookAt,
		@Local(name = "pos") Vec3 pos,
		@Local(name = "rot") Vec2 rot
	) throws CommandSyntaxException {
		RotationVec rot4 = (RotationVec) rot;
		performTeleport(
			source, victim, level,
			x, y, z, ((Vec4) pos).w,
			relatives,
			yRot, xRot, rot4.w, rot4.v,
			lookAt
		);
	}
	// TODO teleportToPos sendSuccess translations

	@Shadow
	@Final
	private static SimpleCommandExceptionType INVALID_POSITION;

	@Redirect(method = "getRelatives", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Relative;direction(ZZZ)Ljava/util/Set;"
	))
	private static Set<Relative> getRelatives_direction(
		boolean relativeX, boolean relativeY, boolean relativeZ,
		@Local(argsOnly = true, name = "destination") Coordinates destination
	) {
		final boolean relativeW = ((Coordinates4) destination).isWRelative();
		return Relative4.direction(relativeX, relativeY, relativeZ, relativeW);
	}
	@Redirect(method = "getRelatives", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Relative;position(ZZZ)Ljava/util/Set;"
	))
	private static Set<Relative> getRelatives_position(
		boolean relativeX, boolean relativeY, boolean relativeZ,
		@Local(argsOnly = true, name = "destination") Coordinates destination
	) {
		final boolean relativeW = ((Coordinates4) destination).isWRelative();
		return Relative4.position(relativeX, relativeY, relativeZ, relativeW);
	}
	@Redirect(method = "getRelatives", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Relative;rotation(ZZ)Ljava/util/Set;"
	))
	private static Set<Relative> getRelatives_rotation(
		boolean relativeYRot, boolean relativeXRot,
		@Local(argsOnly = true, name = "rotation") Coordinates rotation
	) {
		return Relative4.rotation(relativeYRot, relativeXRot, rotation.isZRelative(), ((Coordinates4) rotation).isWRelative());
	}

	@Overwrite
	@Deprecated
	private static void performTeleport(
		CommandSourceStack source,
		Entity victim,
		ServerLevel level,
		double x,
		double y,
		double z,
		Set<Relative> relatives,
		float yRot,
		float xRot,
		@Nullable LookAt lookAt
	) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static void performTeleport(
		CommandSourceStack source,
		Entity victim,
		ServerLevel level,
		double x,
		double y,
		double z,
		double w,
		Set<Relative> relatives,
		float yRot,
		float xRot,
		float wRot,
		float vRot,
		@Nullable LookAt lookAt
	) throws CommandSyntaxException {
		// TODO neo forge event support?
		BlockPos blockPos = BlockPos4.containing(x, y, z, w);
		if (!Level.isInSpawnableBounds(blockPos)) {
			throw INVALID_POSITION.create();
		}

		Entity4 victim4 = (Entity4) victim;
		double relativeOrAbsoluteX = relatives.contains(Relative.X) ? x - victim.getX() : x;
		double relativeOrAbsoluteY = relatives.contains(Relative.Y) ? y - victim.getY() : y;
		double relativeOrAbsoluteZ = relatives.contains(Relative.Z) ? z - victim.getZ() : z;
		double relativeOrAbsoluteW = relatives.contains(Relative4.W) ? w - victim4.getW() : w;
		float relativeOrAbsoluteYRot = relatives.contains(Relative.Y_ROT) ? yRot - victim.getYRot() : yRot;
		float relativeOrAbsoluteXRot = relatives.contains(Relative.X_ROT) ? xRot - victim.getXRot() : xRot;
		float relativeOrAbsoluteWRot = relatives.contains(Relative4.W_ROT) ? wRot - victim4.getWRot() : wRot;
		float relativeOrAbsoluteVRot = relatives.contains(Relative4.V_ROT) ? vRot - victim4.getVRot() : vRot;
		float newYRot = Mth.wrapDegrees(relativeOrAbsoluteYRot);
		float newXRot = Mth.wrapDegrees(relativeOrAbsoluteXRot);
		float newWRot = Mth.wrapDegrees(relativeOrAbsoluteWRot);
		float newVRot = Mth.wrapDegrees(relativeOrAbsoluteVRot);
		if (victim4.teleportTo(level, relativeOrAbsoluteX, relativeOrAbsoluteY, relativeOrAbsoluteZ, relativeOrAbsoluteW, relatives, newYRot, newXRot, newWRot, newVRot, true)) {
			if (lookAt != null) {
				lookAt.perform(source, victim);
			}

			if (!(victim instanceof LivingEntity living && living.isFallFlying())) {
				victim.setDeltaMovement(((Vec4) victim.getDeltaMovement()).multiply(1, 0, 1, 1));
				victim.setOnGround(true);
			}

			if (victim instanceof PathfinderMob mob) {
				mob.getNavigation().stop();
			}
		}
	}
}
