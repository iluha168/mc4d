package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.entity;

import com.iluha168.mc4d.client.renderer.entity.EntityRenderer4;
import com.iluha168.mc4d.client.renderer.entity.state.EntityRenderState4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
abstract
class EntityRendererMixin<T extends Entity, S extends EntityRenderState> implements EntityRenderer4<T, S> {
	// TODO everything else

	@Shadow
	protected abstract boolean affectedByCulling(T entity);

	@Shadow
	protected abstract AABB getBoundingBoxForCulling(T entity);

	@Shadow
	@Final
	protected EntityRenderDispatcher entityRenderDispatcher;

	@Redirect(method = "extractShadow", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos extractShadow(BlockPos.MutableBlockPos instance, int x, int y, int z) {
		// TODO use actual W value for 4D renderer
		return ((BlockPos4.MutableBlockPos) instance).set(x, y, z, 0);
	}

	// TODO everything else

	@Overwrite
	@Deprecated
	public boolean shouldRender(T entity, Frustum culler, double camX, double camY, double camZ) {
		throw Err4.arguments3("EntityRenderer4#shouldRender");
	}
	@Override
	public boolean shouldRender(T entity, Frustum culler, double camX, double camY, double camZ, double camW) {
		Entity4 entity4 = (Entity4) entity;

		if (!entity4.shouldRender(camX, camY, camZ, camW)) {
			return false;
		}
		if (!this.affectedByCulling(entity)) {
			return true;
		}
		// Inflating W would render the model 0.5 wider than its actual hitbox.
		// TODO: return inflate(0.5) when 4D renderer
		AABB boundingBox = ((AABB4) this.getBoundingBoxForCulling(entity)).inflate(0.5, 0.5, 0.5, 0);
		if (boundingBox.hasNaN() || boundingBox.getSize() == 0.0) {
			boundingBox = new AABB4(
				entity.getX() - 2.0,
				entity.getY() - 2.0,
				entity.getZ() - 2.0,
				entity4.getW() - 2.0,
				entity.getX() + 2.0,
				entity.getY() + 2.0,
				entity.getZ() + 2.0,
				entity4.getW() + 2.0
			);
		}

		if (culler.isVisible(boundingBox)) {
			return true;
		}
		if (entity instanceof Leashable leashable) {
			Entity leashHolder = leashable.getLeashHolder();
			if (leashHolder != null) {
				@SuppressWarnings("unchecked") AABB leasherBox = ((EntityRenderer4<Entity, ?>) this.entityRenderDispatcher.getRenderer(leashHolder)).getBoundingBoxForCulling4(leashHolder);
				return culler.isVisible(leasherBox) || culler.isVisible(boundingBox.minmax(leasherBox));
			}
		}

		return false;
	}

	@Override
	public AABB getBoundingBoxForCulling4(T entity) {
		return this.getBoundingBoxForCulling(entity);
	}

	// TODO everything else

	@Definition(id = "z", field = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;z:D")
	@Expression("?.z = @(?)")
	@Inject(method = "extractRenderState", at = @At("MIXINEXTRAS:EXPRESSION"))
	void extractRenderState_w(T entity, S state, float partialTicks, CallbackInfo ci) {
		Entity4 entity4 = (Entity4) entity;
		((EntityRenderState4) state).setW(Mth.lerp(partialTicks, entity4.wOld(), entity4.getW()));
	}
	@Redirect(method = "extractRenderState", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 extractRenderState_cart(
		double x, double y, double z,
		@Local(argsOnly = true, name = "partialTicks") float partialTicks,
		@Local(name = "minecart") AbstractMinecart minecart
	) {
		Entity4 minecart4 = (Entity4) minecart;
		final double cartLerpW = Mth.lerp(partialTicks, minecart4.wOld(), minecart4.getW());
		return new Vec4(x, y, z, cartLerpW);
	}

	// TODO everything else
}
