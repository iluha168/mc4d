package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityDimensions.class)
public class EntityDimensionsMixin {
	@Shadow
	@Final
	private float width;

	@Shadow
	@Final
	private float height;

	@Redirect(method = "makeBoundingBox(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/EntityDimensions;makeBoundingBox(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB makeBoundingBox(EntityDimensions instance, double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		double w = ((Position4) pos).w();
		float width = this.width / 2.0F;
		float h = this.height;
		return new AABB4(
			x - width, y, z - width, w - width,
			x + width, y + h, z + width, w + width
		);
	}

	@Overwrite
	public AABB makeBoundingBox(double x, double y, double z) {
		throw Err4.arguments3("EntityDimensions#makeBoundingBox(Vec4)");
	}
}
