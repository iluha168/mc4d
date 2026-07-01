package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.network.protocol.game.ClientboundAddEntityPacket4;
import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.LivingEntity4;
import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin implements LivingEntity4 {

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@Shadow
	public abstract @Nullable MobEffectInstance getEffect(Holder<MobEffect> effect);

	@Unique protected float wwa;

	@Override
	public float wwa() {
		return this.wwa;
	}
	@Override
	public void setWwa(float wwa) {
		this.wwa = wwa;
	}

	@Definition(id = "z", local = @Local(type = double.class, name = "z"))
	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/LivingEntity;getZ()D")
	@Expression("z = this.getZ()")
	@Inject(method = "checkFallDamage", at = @At("MIXINEXTRAS:EXPRESSION"))
	void checkFallDamage_w(double ya, boolean onGround, BlockState onState, BlockPos pos, CallbackInfo ci, @Share("w") LocalDoubleRef w) {
		w.set(this.getW());
	}
	@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
	@Expression("?.getZ() != ?.getZ()")
	@ModifyExpressionValue(method = "checkFallDamage", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean checkFallDamage_condition(boolean original, @Local(argsOnly = true, name = "pos") BlockPos pos, @Local(name = "entityPos") BlockPos entityPos) {
		return original || Vec4i.getW(pos) != Vec4i.getW(entityPos);
	}

	@Definition(id = "maxDiff", local = @Local(type = double.class, name = "maxDiff"))
	@Expression("maxDiff = @(?)")
	@ModifyExpressionValue(method = "checkFallDamage", at = @At("MIXINEXTRAS:EXPRESSION"))
	double checkFallDamage_wDiff_maxDiff(
		double maxDiff3,
		@Share("w") LocalDoubleRef w,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		final double posW = Vec4i.getW(pos);
		final double wDiff = w.get() - posW - 0.5;
		final double maxDiff = Math.max(maxDiff3, Math.abs(wDiff));
		w.set(posW + 0.5 + wDiff / maxDiff * 0.5);
		return maxDiff;
	}
	@Redirect(method = "checkFallDamage", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
	))
	<T extends ParticleOptions> int checkFallDamage(
		ServerLevel level, T particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed,
		@Share("w") LocalDoubleRef w
	) {
		return ((ServerLevel4) level).sendParticles(particle, x, y, z, w.get(), count, xDist, yDist, zDist, zDist, speed);
	}

	@Redirect(method = "baseTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos baseTick(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.getW());
	}

	@Redirect(method = "onEquipItem", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
	))
	void onEquipItem(Level level, Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
		((Level4) level).playSeededSound(except, x, y, z, this.getW(), sound, source, volume, pitch, seed);
	}

	// TODO addAdditionalSaveData
	// TODO readAdditionalSaveData
	// TODO tickEffects
	// TODO hurtServer
	// TODO applyItemBlocking

	@Redirect(method = "playSecondaryHurtSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;)V"
	))
	void playSecondaryHurtSound(Level level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source) {
		((Level4) level).playSound(except, x, y, z, this.getW(), sound, source);
	}

	// TODO blockedByItem

	@Redirect(method = "breakItem", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void breakItem(Level level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		((Level4) level).playLocalSound(x, y, z, this.getW(), sound, source, volume, pitch, distanceDelay);
	}

	@Redirect(method = "createWitherRose", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
	))
	ItemEntity createWitherRose(Level level, double x, double y, double z, ItemStack itemStack) {
		return ItemEntity4.from(level, x, y, z, this.getW(), itemStack);
	}

	// TODO knockback
	// TODO indicateDamage

	@Redirect(method = "isLookingAtMe", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 isLookingAtMe(double x, double y, double z, @Local(argsOnly = true, name = "target") LivingEntity target) {
		return new Vec4(x, y, z, this.getW() - ((Entity4) target).getW());
	}

	@Redirect(method = "playBlockFallSound", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos playBlockFallSound(int x, int y, int z) {
		return BlockPos4.from(x, y, z, Mth.floor(this.getW()));
	}

	@Redirect(method = "handleEntityEvent", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void handleEntityEvent(Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, @Local(name = "d") double d) {
		final float wa = (this.random.nextFloat() - 0.5F) * 0.2F;
		final double w = Mth.lerp(d, this.wo, this.getW()) + (this.random.nextDouble() - 0.5) * this.getBbWidth() * 2.0;
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wa);
	}

	// TODO makePoofParticles

	@Redirect(method = "makeDrownParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void makeDrownParticles(Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, @Local(name = "movement") Vec3 movement) {
		((LevelAccessor4) level).addParticle(particle, x, y, z, this.getW() + this.random.triangle(0.0, 1.0), xd, yd, zd, ((Vec4) movement).w);
	}

	// TODO dismountVehicle

	@Redirect(method = "jumpFromGround", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"
	))
	void jumpFromGround(LivingEntity This, double x, double y, double z, @Local(name = "movement") Vec3 movement) {
		This.setDeltaMovement(new Vec4(x, y, z, ((Vec4) movement).w));
	}
	@Redirect(method = "jumpFromGround", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 jumpFromGround(double x, double y, double z) {
		return new Vec4(x, y, z, 0);
	}

	// TODO jumpInLiquid

	@Redirect(method = "travelInAir", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V",
		ordinal = 0
	))
	void travelInAir_noFriction(LivingEntity instance, double dx, double dy, double dz, @Local(name = "movement") Vec3 movement) {
		instance.setDeltaMovement(new Vec4(dx, dy, dz, ((Vec4) movement).w));
	}
	@Redirect(method = "travelInAir", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V",
		ordinal = 1
	))
	void travelInAir_friction(
		LivingEntity instance, double dx, double dy, double dz,
		@Local(name = "movement") Vec3 movement,
		@Local(name = "friction") float friction
	) {
		instance.setDeltaMovement(new Vec4(dx, dy, dz, ((Vec4) movement).w * friction));
	}

	// TODO travelInWater

	@Redirect(method = "travelInLava", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 travelInLava_multiply(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}
	@Redirect(method = "travelInLava", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 travelInLava_add(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).add(x, y, z, z);
	}

	// TODO jumpOutOfFluid
	// TODO floatInWaterWhileRidden
	// TODO updateFallFlyingMovement

	@ModifyExpressionValue(method = "travelRidden", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	Vec3 travelRidden(Vec3 original) {
		return Vec4.ZERO;
	}

	@Redirect(method = "calculateEntityAnimation", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;length(DDD)D"
	))
	double calculateEntityAnimation(double x, double y, double z) {
		return MathHelpers.length(x, y, z, this.getW() - this.wo);
	}

	@Redirect(method = "handleRelativeFrictionAndCalculateMovement", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 handleRelativeFrictionAndCalculateMovement(double x, double y, double z, @Local(name = "movement") Vec3 movement) {
		return new Vec4(x, y, z, ((Vec4) movement).w);
	}

	@Redirect(method = "getFluidFallingAdjustedMovement", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getFluidFallingAdjustedMovement(double x, double y, double z, @Local(argsOnly = true, name = "movement") Vec3 movement) {
		return new Vec4(x, y, z, ((Vec4) movement).w);
	}

	@Redirect(method = "handleOnClimbable", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 handleOnClimbable(double x, double y, double z, @Local(argsOnly = true, name = "delta") Vec3 delta) {
		return new Vec4(x, y, z, Mth.clamp(((Vec4) delta).w, -0.15F, 0.15F));
	}

	@Redirect(method = "causeExtraKnockback", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 causeExtraKnockback(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}

	@Definition(id = "dz", local = @Local(type = double.class, name = "dz"))
	@Definition(id = "movement", local = @Local(type = Vec3.class, name = "movement"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Expression("dz = movement.z")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void aiStep_createDW(
		CallbackInfo ci,
		@Local(name = "movement") Vec3 movement,
		@Share("dw") LocalDoubleRef dw
	) {
		dw.set(((Vec4) movement).w);
	}
	@Definition(id = "dz", local = @Local(type = double.class, name = "dz"))
	@Expression("dz = 0.0")
	@Inject(method = "aiStep", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	void aiStep_horizontalEpsilon(CallbackInfo ci, @Share("dw") LocalDoubleRef dw) {
		dw.set(0);
	}
	@Definition(id = "abs", method = "Ljava/lang/Math;abs(D)D")
	@Definition(id = "movement", local = @Local(type = Vec3.class, name = "movement"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Expression("abs(movement.z)")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void aiStep_wEpsilon(
		CallbackInfo ci,
		@Local(name = "movement") Vec3 movement,
		@Share("dw") LocalDoubleRef dw
	) {
		if (Math.abs(((Vec4) movement).w) < 0.003) {
			dw.set(0);
		}
	}
	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"
	))
	void aiStep_setDeltaMovement(LivingEntity This, double dx, double dy, double dz, @Share("dw") LocalDoubleRef dw) {
		This.setDeltaMovement(new Vec4(dx, dy, dz, dw.get()));
	}
	@Definition(id = "zza", field = "Lnet/minecraft/world/entity/LivingEntity;zza:F")
	@Expression("this.zza = 0.0")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void aiStep_immobile(CallbackInfo ci) {
		this.wwa = 0.0F;
	}
	@Redirect(method = "aiStep", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 aiStep_input(double x, double y, double z) {
		return new Vec4(x, y, z, this.wwa);
	}

	@Inject(method = "applyInput", at = @At("TAIL"))
	void applyInput(CallbackInfo ci) {
		this.wwa *= 0.98F;
	}

	// TODO hasLineOfSight
	// TODO resetForwardDirectionOfRelativePortalPosition

	@Redirect(method = "createItemStackToDrop", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
	))
	ItemEntity createItemStackToDrop_itemEntity(Level level, double x, double y, double z, ItemStack itemStack) {
		return ItemEntity4.from(level, x, y, z, this.getW(), itemStack);
	}
	@Redirect(method = "createItemStackToDrop", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V",
		ordinal = 0
	))
	void createItemStackToDrop_randomly(ItemEntity instance, double x, double y, double z) {
		final float pow2 = this.random.nextFloat() * 0.5F;
		final float dir2 = this.random.nextFloat() * (float) (Math.PI * 2);
		instance.setDeltaMovement(new Vec4(x, y, z, Mth.cos(dir2) * pow2));
	}
	@Redirect(method = "createItemStackToDrop", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V",
		ordinal = 1
	))
	void createItemStackToDrop_setDeltaMovement(ItemEntity instance, double x, double y, double z) {
		final float pow2 = 0.02F * this.random.nextFloat();
		final float dir2 = this.random.nextFloat() * (float) (Math.PI * 2);
		instance.setDeltaMovement(new Vec4(x, y, z, Mth.sin(dir2) * pow2));
	}

	// TODO spawnItemParticles
	// TODO randomTeleport
	// TODO randomTeleport

	@Redirect(method = "getLocalBoundsForPose", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB getLocalBoundsForPose(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new AABB4(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	@Redirect(method = "setPosToBed", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setPos(DDD)V"
	))
	void setPosToBed(LivingEntity instance, double x, double y, double z, @Local(argsOnly = true, name = "bedPosition") BlockPos bedPosition) {
		instance.setPos(new Vec4(x, y, z, Vec4i.getW(bedPosition) + 0.5));
	}

	// TODO stopSleeping

	@Redirect(method = "recreateFromPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;syncPacketPositionCodec(DDD)V"
	))
	void recreateFromPacket_syncPacketPositionCodec(LivingEntity instance, double x, double y, double z, @Local(argsOnly = true, name = "packet") ClientboundAddEntityPacket packet) {
		((Entity4) instance).syncPacketPositionCodec(x, y, z, ((ClientboundAddEntityPacket4) packet).getW());
	}
	@Redirect(method = "recreateFromPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;absSnapTo(DDDFF)V"
	))
	void recreateFromPacket_absSnapTo(LivingEntity instance, double x, double y, double z, float yaw, float pitch, @Local(argsOnly = true, name = "packet") ClientboundAddEntityPacket packet) {
		((Entity4) instance).absSnapTo(x, y, z, ((ClientboundAddEntityPacket4) packet).getW(), yaw, pitch);
	}
}
