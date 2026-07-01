package com.iluha168.mc4d.mixin.net.minecraft.world.entity.item;

import com.iluha168.mc4d.mixin.net.minecraft.world.entity.EntityMixin;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin extends EntityMixin implements ItemEntity4 {
	@Unique	protected byte initIncomplete;

	@Redirect(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setPos(DDD)V"
	))
	void initWithoutMovement_setPos(ItemEntity instance, double x, double y, double z) {
		this.initIncomplete = 1;
	}
	@Redirect(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
	))
	void initWithoutMovement_setDeltaMovement(ItemEntity instance, double x, double y, double z) {}
	@Override
	public void init_finish(double x, double y, double z, double w) {
		if (this.initIncomplete == 0) {
			throw new IllegalStateException("Programmer error: ItemEntity4#init_finish called more than once.");
		}
		if (this.initIncomplete != 1) {
			throw new IllegalStateException("Programmer error: wrong ItemEntity4#init_finish called. This ItemEntity has been created with initial velocity.");
		}
		this.initIncomplete = 0;
		try {
			this.setPos(new Vec4(x, y, z, w));
			this.setDeltaMovement(new Vec4(
				this.random.nextDouble() * 0.2 - 0.1,
				0.2,
				this.random.nextDouble() * 0.2 - 0.1,
				this.random.nextDouble() * 0.2 - 0.1
			));
		} catch (Throwable e) {
			this.initIncomplete = 1;
			throw e;
		}
	}

	@Redirect(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setPos(DDD)V"
	))
	void initWithMovement_setPos(ItemEntity instance, double x, double y, double z) {
		this.initIncomplete = 2;
	}
	@Redirect(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
	))
	void initWithMovement_setDeltaMovement(ItemEntity instance, double x, double y, double z) {}
	@Override
	public void init_finish(double x, double y, double z, double w, double deltaX, double deltaY, double deltaZ, double deltaW) {
		if (this.initIncomplete == 0) {
			throw new IllegalStateException("Programmer error: ItemEntity4#init_finish called more than once.");
		}
		if (this.initIncomplete != 2) {
			throw new IllegalStateException("Programmer error: wrong ItemEntity4#init_finish called. This ItemEntity has been created without initial velocity.");
		}
		this.initIncomplete = 0;
		try {
			this.setPos(new Vec4(x, y, z, w));
			this.setDeltaMovement(new Vec4(deltaX, deltaY, deltaZ, deltaW));
		} catch (Throwable e) {
			this.initIncomplete = 2;
			throw e;
		}
	}

	@Definition(id = "zo", field = "Lnet/minecraft/world/entity/item/ItemEntity;zo:D")
	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/item/ItemEntity;getZ()D")
	@Expression("this.zo = this.getZ()")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_setWO(CallbackInfo ci) {
		if (this.initIncomplete != 0) throw Err4.field4missing("w");
		this.setWO(this.getW());
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;moveTowardsClosestSpace(DDD)V"
	))
	void tick_moveTowardsClosestSpace(ItemEntity instance, double x, double y, double z) {
		((Entity4) instance).moveTowardsClosestSpace(x, y, z, this.getW());
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 tick_multiply(Vec3 instance, double xScale, double yScale, double zScale) {
		assert xScale == zScale;
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}
	@Definition(id = "floor", method = "Lnet/minecraft/util/Mth;floor(D)I")
	@Definition(id = "zo", field = "Lnet/minecraft/world/entity/item/ItemEntity;zo:D")
	@Expression("floor(this.zo) != floor(?)")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean tick_moved(boolean original) {
		return original || Mth.floor(this.wo) != Mth.floor(this.getW());
	}

	@Redirect(method = "setFluidMovement", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
	))
	void setFluidMovement(ItemEntity instance, double x, double y, double z, @Local(name = "movement") Vec3 movement, @Local(argsOnly = true, name = "multiplier") double multiplier) {
		instance.setDeltaMovement(new Vec4(x, y, z, ((Vec4) movement).w * multiplier));
	}
	@Redirect(method = "mergeWithNeighbours", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB mergeWithNeighbours(AABB instance, double xAdd, double yAdd, double zAdd) {
		return ((AABB4) instance).inflate(xAdd, yAdd, zAdd, zAdd);
	}
}
