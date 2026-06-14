package com.iluha168.mc4d.mixin.net.minecraft.client;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
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

	@Definition(id = "position", field = "Lnet/minecraft/client/Camera;position:Lnet/minecraft/world/phys/Vec3;")
	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;")
	@Expression("this.position = @(ZERO)")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	Vec3 initialPosition(Vec3 original) {
		return Vec4.ZERO;
	}

	// TODO prepareCullFrustum

	@Redirect(method = "alignWithEntity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"
	))
	void alignWithEntity(
		Camera This, double x, double y, double z,
		@Local(argsOnly = true, name = "partialTicks") float partialTicks
	) {
		Entity4 entity4 = (Entity4) this.entity;
		assert entity4 != null;
		double w = Mth.lerp(partialTicks, entity4.getWO(), entity4.getW());
		this.setPosition(new Vec4(x, y, z, w));
	}

	@Redirect(method = "getMaxZoom", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getMaxZoom(Vec3 position, double x, double y, double z, @Local(name = "i") int i) {
		float offsetW = (i >> 3 & 1) * 2 - 1;
		return ((Vec4) position).add(x, y, z, offsetW * 0.1F);
	}

	// TODO move

	@Definition(id = "setPosition", method = "Lnet/minecraft/client/Camera;setPosition(Lnet/minecraft/world/phys/Vec3;)V")
	@Expression("this.setPosition(?)")
	@Redirect(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move(Camera This, Vec3 position) {
		this.setPosition(Vec4.of(position, ((Vec4) this.position).w));
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

	// TODO? NearPlane
}
