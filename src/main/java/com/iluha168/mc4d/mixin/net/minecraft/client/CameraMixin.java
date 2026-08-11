package com.iluha168.mc4d.mixin.net.minecraft.client;

import com.iluha168.mc4d.client.renderer.culling.Frustum4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
abstract
class CameraMixin {
	@Shadow
	protected abstract void setPosition(Vec3 position);

	@Shadow
	private @Nullable Entity entity;

	@Shadow
	private Vec3 position;

	@Redirect(method = "prepareCullFrustum", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/culling/Frustum;prepare(DDD)V"
	))
	void prepareCullFrustum(Frustum frustum, double camX, double camY, double camZ, @Local(argsOnly = true, name = "cameraPos") Vec3 cameraPos) {
		((Frustum4) frustum).prepare(camX, camY, camZ, ((Vec4) cameraPos).w);
	}

	@Redirect(method = "alignWithEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"
	))
	void alignWithEntity_position(
		Camera This, double x, double y, double z,
		@Local(argsOnly = true, name = "partialTicks") float partialTicks
	) {
		Entity4 entity4 = (Entity4) this.entity;
		assert entity4 != null;
		double w = Mth.lerp(partialTicks, entity4.getWO(), entity4.getW());
		this.setPosition(new Vec4(x, y, z, w));
	}

	@ModifyConstant(method = "getMaxZoom", constant = @Constant(intValue = 8))
	int getMaxZoom_iMax(int constant) {
		return 16;
	}
	@Redirect(method = "getMaxZoom", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getMaxZoom_from(Vec3 position, double x, double y, double z, @Local(name = "i") int i) {
		float offsetW = (i >> 3 & 1) * 2 - 1;
		return ((Vec4) position).add(x, y, z, offsetW * 0.1F);
	}
	@ModifyExpressionValue(method = "getMaxZoom", at = @At(
		value = "NEW",
		target = "(Lorg/joml/Vector3fc;)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getMaxZoom_forwards(Vec3 f) {
		return new Vec4(f.x, f.y, f.z, 0);
	}

	@Definition(id = "setPosition", method = "Lnet/minecraft/client/Camera;setPosition(Lnet/minecraft/world/phys/Vec3;)V")
	@Expression("this.setPosition(?)")
	@Redirect(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move(Camera This, Vec3 position) {
		// TODO rework when 4D renderer
		this.setPosition(new Vec4(position.x, position.y, position.z, ((Vec4) this.position).w));
	}

	@Redirect(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(DDD)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos setPosition(BlockPos.MutableBlockPos blockPosition, double x, double y, double z, @Local(argsOnly = true, name = "position") Vec3 position) {
		if (!(position instanceof Vec4 pos4)) {
			throw Err4.container3();
		}
		return ((BlockPos4.MutableBlockPos) blockPosition).set(x, y, z, pos4.w);
	}

	@Redirect(method = "getNearPlane", at = @At(
		value = "NEW",
		target = "(Lorg/joml/Vector3fc;)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getNearPlane(Vector3fc vec) {
		// TODO rework when 4D renderer
		return new Vec4(vec.x(), vec.y(), vec.z(), 0.0);
	}
}
