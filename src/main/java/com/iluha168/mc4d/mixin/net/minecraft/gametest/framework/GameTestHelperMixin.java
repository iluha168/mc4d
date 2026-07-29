package com.iluha168.mc4d.mixin.net.minecraft.gametest.framework;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameTestHelper.class)
class GameTestHelperMixin {
	// TODO spawnItem
	// TODO spawnItem
	// TODO spawnItem
	// TODO despawnItem

	@Redirect(method = "spawn(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void spawn_snapTo(Entity entity, double x, double y, double z, float yRot, float xRot, @Local(name = "absoluteVec") Vec3 absoluteVec) {
		final Entity4 entity4 = (Entity4) entity;
		entity4.snapTo(x, y, z, ((Vec4) absoluteVec).w, yRot, xRot, entity4.getWRot(), entity4.getVRot());
	}
	@Redirect(method = "spawn(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setYBodyRot(F)V"
	))
	void spawn_setYBodyRot(Entity entity, float yBodyRot) {
		final Entity4 entity4 = (Entity4) entity;
		entity4.setYBodyRot(yBodyRot, entity4.getWRot());
	}
	@Redirect(method = "spawn(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setYHeadRot(F)V"
	))
	void spawn_setYHeadRot(Entity entity, float yHeadRot) {
		final Entity4 entity4 = (Entity4) entity;
		entity4.setYHeadRot(yHeadRot, entity4.getWRot());
	}

	// TODO spawn
	// TODO findOneEntity
	// TODO findClosestEntity
	// TODO findEntities
	// TODO findEntities
	// TODO spawn
	// TODO spawn
	// TODO spawnWithNoFreeWill
	// TODO spawnWithNoFreeWill
	// TODO moveTo
	// TODO moveTo
	// TODO pressButton
	// TODO pullLever
	// TODO placeBlock
	// TODO setBlock
	// TODO setBlock
	// TODO assertBlockPresent
	// TODO assertBlockPresent
	// TODO assertBlockPresent
	// TODO assertBlockNotPresent
	// TODO succeedWhenBlockPresent
	// TODO assertEntityPresent
	// TODO assertEntityPresent
	// TODO getEntities
	// TODO assertEntityInstancePresent
	// TODO assertEntityInstancePresent
	// TODO assertItemEntityCountIs
	// TODO assertItemEntityPresent
	// TODO assertItemEntityNotPresent
	// TODO assertEntityNotPresent
	// TODO assertEntityNotPresent
	// TODO assertEntityTouching
	// TODO assertEntityNotTouching
	// TODO assertEntityData
	// TODO assertEntityData
	// TODO assertEntityIsHolding
	// TODO assertEntityInventoryContains
	// TODO assertSameBlockStates
	// TODO succeedWhenEntityPresent
	// TODO succeedWhenEntityNotPresent
	// TODO tickPrecipitation
	// TODO getHeight
	// TODO getRelativeBounds
	// TODO forEveryBlockInStructure
	// TODO setBiome
}
