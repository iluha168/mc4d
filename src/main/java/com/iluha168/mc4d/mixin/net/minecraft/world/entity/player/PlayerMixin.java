package com.iluha168.mc4d.mixin.net.minecraft.world.entity.player;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.mixin.net.minecraft.world.entity.LivingEntityMixin;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin {
	@Shadow
	public abstract float getSpeed();

	@ModifyConstant(method = "createAttributes", constant = @Constant(doubleValue = 2 * Level.MAX_LEVEL_SIZE))
	private static double createAttributes(double constant) {
		return 2 * Level4.MAX_LEVEL_SIZE;
	}

	@ModifyConstant(method = "tick", constant = @Constant(intValue = Level.MAX_LEVEL_SIZE - 1))
	int tick_levelSize(int constant) {
		return Level4.MAX_LEVEL_SIZE - 1;
	}
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = Level.MAX_LEVEL_SIZE - 1))
	double tick_levelSizePos(double constant) {
		return Level4.MAX_LEVEL_SIZE - 1;
	}
	@ModifyConstant(method = "tick", constant = @Constant(doubleValue = -(Level.MAX_LEVEL_SIZE - 1)))
	double tick_levelSizeNeg(double constant) {
		return -(Level4.MAX_LEVEL_SIZE - 1);
	}
	@Definition(id = "nz", local = @Local(type = double.class, name = "nz"))
	@Expression("nz != ?")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean tick(boolean original, @Share("nw") LocalDoubleRef nw) {
		nw.set(Mth.clamp(this.getW(), -(Level4.MAX_LEVEL_SIZE - 1), Level4.MAX_LEVEL_SIZE - 1));
		return original || nw.get() != this.getW();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;setPos(DDD)V"
	))
	void tick(Player instance, double x, double y, double z, @Share("nw") LocalDoubleRef nw) {
		instance.setPos(new Vec4(x, y, z, nw.get()));
	}

	@Redirect(method = "playSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void playSound(Level level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		((Level4) level).playSound(except, x, y, z, this.getW(), sound, source, volume, pitch);
	}

	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB pickupArea(AABB instance, double xAdd, double yAdd, double zAdd) {
		assert xAdd == zAdd;
		return ((AABB4) instance).inflate(xAdd, yAdd, zAdd, xAdd);
	}

	@Redirect(method = "die", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;setDeltaMovement(DDD)V"
	))
	void die(Player instance, double x, double y, double z) {
		instance.setDeltaMovement(new Vec4(x, y, z, 0.0));
	}

	@Redirect(method = "maybeBackOffFromEdge", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDD)Z"
	))
	boolean maybeBackOffFromEdge_canFallAtLeast4(Player instance, double deltaX, double deltaZ, double minHeight) {
		assert (Object) this == instance;
		return this.canFallAtLeast(deltaX, deltaZ, 0, minHeight);
	}
	@Redirect(method = "maybeBackOffFromEdge", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 maybeBackOffFromEdge(
		double x, double y, double z,
		@Local(name = "delta", argsOnly = true) Vec3 delta,
		@Local(name = "step") double step,
		@Local(name = "maxDownStep") float maxDownStep,
		@Local(name = "stepX") double stepX,
		@Local(name = "stepZ") double stepZ
	) {
		double deltaW = ((Vec4) delta).w;
		double stepW = Math.signum(deltaW) * step;

		while (deltaW != 0 && this.canFallAtLeast(0, z, deltaW, maxDownStep)) {
			if (Math.abs(deltaW) <= step) {
				deltaW = 0;
				break;
			}
			deltaW -= stepW;
		}

		while (x != 0 && z != 0 && deltaW != 0 && this.canFallAtLeast(x, z, deltaW, maxDownStep)) {
			if (Math.abs(x) <= step) {
				x = 0;
			} else {
				x -= stepX;
			}

			if (Math.abs(z) <= step) {
				z = 0;
			} else {
				z -= stepZ;
			}

			if (Math.abs(deltaW) <= step) {
				deltaW = 0;
			} else {
				deltaW -= stepW;
			}
		}

		return new Vec4(x, y, z, deltaW);
	}

	@Redirect(method = "isAboveGround", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDD)Z"
	))
	boolean isAboveGround(Player instance, double deltaX, double deltaZ, double minHeight) {
		assert (Object) this == instance;
		return this.canFallAtLeast(deltaX, deltaZ, 0, minHeight);
	}

	@Overwrite
	@Deprecated
	private boolean canFallAtLeast(double deltaX, double deltaZ, double minHeight) {
		throw Err4.arguments2(null);
	}
	@Unique
	private boolean canFallAtLeast(double deltaX, double deltaZ, double deltaW, double minHeight) {
		Entity player = (Entity) (Object) this;
		AABB4 boundingBox = (AABB4) player.getBoundingBox();
		return player.level().noCollision(
			player,
			new AABB4(
				boundingBox.minX + AABB4.EPSILON + deltaX,
				boundingBox.minY - minHeight - AABB4.EPSILON,
				boundingBox.minZ + AABB4.EPSILON + deltaZ,
				boundingBox.minW + AABB4.EPSILON + deltaW,
				boundingBox.maxX - AABB4.EPSILON + deltaX,
				boundingBox.minY,
				boundingBox.maxZ - AABB4.EPSILON + deltaZ,
				boundingBox.maxW - AABB4.EPSILON + deltaW
			)
		);
	}

	@Redirect(method = "playServerSideSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void playServerSideSound(Level level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		((Level4) level).playSound(except, x, y, z, this.getW(), sound, source, volume, pitch);
	}

	@Redirect(method = "deflectProjectile", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;)V"
	))
	void deflectProjectile(Level level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source) {
		((Level4) level).playSound(except, x, y, z, this.getW(), sound, source);
	}

	// TODO damageStatsAndHearts
	// TODO causeExtraKnockback
	// TODO doSweepAttack
	// TODO doSweepAttack

	@Redirect(method = "travel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos travel_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.getW());
	}
	@Redirect(method = "travel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 travel_add(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).add(x, y, z, z);
	}

	@Redirect(method = "giveExperienceLevels", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void giveExperienceLevels(Level level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		((Level4) level).playSound(except, x, y, z, this.getW(), sound, source, volume, pitch);
	}

	// TODO getRopeHoldPosition

	@Redirect(method = "isWithinBlockInteractionRange", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB isWithinBlockInteractionRange(BlockPos pos) {
		return new AABB4(pos);
	}
}
