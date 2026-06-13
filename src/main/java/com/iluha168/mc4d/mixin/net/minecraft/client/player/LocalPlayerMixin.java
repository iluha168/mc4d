package com.iluha168.mc4d.mixin.net.minecraft.client.player;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {
	@Redirect(method = "resetPos", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;setPos(DDD)V"
	))
	void resetPos(LocalPlayer player, double x, double y, double z) {
		player.setPos(new Vec4(x, y, z, ((Entity4) this).getW()));
	}

	@Redirect(method = "resetPos", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
	))
	void resetDeltaMovement(LocalPlayer This, Vec3 vec3) {
		This.setDeltaMovement(Vec4.ZERO);
	}

	@Shadow
	protected abstract boolean suffocatesAt(BlockPos pos);

	@Redirect(method = "pick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB inflate(AABB instance, double xAdd, double yAdd, double zAdd) {
		return instance.inflate(xAdd);
	}

	@Redirect(method = "suffocatesAt", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB suffocatesAt(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		double minW = Vec4i.getW(pos);
		return new AABB4(
			minX, minY, minZ, minW,
			maxX, maxY, maxZ, minW + 1
		);
	}

	@Redirect(method = "pick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 pick(
		Vec3 from, double x, double y, double z,
		@Local(name = "direction") Vec3 direction,
		@Local(name = "maxDistance") double maxDistance
	) {
		double w = ((Position4) direction).w() * maxDistance;
		return ((Vec4) from).add(x, y, z, w);
	}

	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;moveTowardsClosestSpace(DD)V"
	))
	void aiStep_moveTowardsClosestSpace4(LocalPlayer This, double x, double z) {
		final double w = ((Entity4) this).getW();
		final double wPlus  = w + This.getBbWidth() * 0.35;
		final double wMinus = w - This.getBbWidth() * 0.35;
		this.moveTowardsClosestSpace(This, x, z, wPlus);
		this.moveTowardsClosestSpace(This, x, z, wMinus);
	}

	@Unique
	private void moveTowardsClosestSpace(LocalPlayer This, double x, double z, double w) {
		BlockPos pos = BlockPos4.containing(x, This.getY(), z, w);
		if (this.suffocatesAt(pos)) {
			double xd = x - pos.getX();
			double zd = z - pos.getZ();
			double wd = w - Vec4i.getW(pos);

			Direction dir = null;
			double closest = Double.MAX_VALUE;

			for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
				double axisDistance = Direction4.Axis.as(direction.getAxis()).choose(xd, 0.0, zd, wd);
				double distanceToEdge = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - axisDistance : axisDistance;
				if (distanceToEdge < closest && !this.suffocatesAt(pos.relative(direction))) {
					closest = distanceToEdge;
					dir = direction;
				}
			}

			if (dir != null) {
				Vec3 oldMovement = This.getDeltaMovement();
				double oldMovementW = ((Position4) oldMovement).w();
				if (dir.getAxis() == Direction.Axis.X) {
					This.setDeltaMovement(new Vec4(0.1 * dir.getStepX(), oldMovement.y, oldMovement.z, oldMovementW));
				} else if (dir.getAxis() == Direction.Axis.Z) {
					This.setDeltaMovement(new Vec4(oldMovement.x, oldMovement.y, 0.1 * dir.getStepZ(), oldMovementW));
				} else {
					This.setDeltaMovement(new Vec4(oldMovement.x, oldMovement.y, oldMovement.z, 0.1 * Direction4.as(dir).getStepW()));
				}
			}
		}
	}

	@Redirect(method = "playSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void playLocalSound(Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		// TODO obviously remove this in favor of 4D sound engine
	}
}
