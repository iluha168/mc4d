package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.EntityType4;
import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityType.class)
abstract
class EntityTypeMixin<T extends Entity> implements EntityType4<T> {
	@Shadow
	@Final
	private float spawnDimensionsScale;

	@Shadow
	public abstract float getWidth();

	@Shadow
	public abstract float getHeight();

	// TODO create
	// TODO getYOffset

	@Overwrite
	@Deprecated
	public AABB getSpawnAABB(double x, double y, double z) {
		throw Err4.arguments3("EntityType4#getSpawnAABB");
	}
	@Override
	public AABB4 getSpawnAABB(double x, double y, double z, double w) {
		final float halfWidth = this.spawnDimensionsScale * this.getWidth() / 2.0F;
		final float height = this.spawnDimensionsScale * this.getHeight();
		return new AABB4(
			x - halfWidth, y, z - halfWidth, w - halfWidth,
			x + halfWidth, y + height, z + halfWidth, w + halfWidth
		);
	}

	// TODO Builder#passengerAttachments
	// TODO Builder#ridingOffset
	// TODO Builder#nameTagOffset
	// TODO Builder#attach
}
