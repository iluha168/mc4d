package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.ItemPickupParticle4;
import com.iluha168.mc4d.client.particle.ItemPickupParticleGroup4;
import com.iluha168.mc4d.client.renderer.entity.EntityRenderDispatcher4;
import com.iluha168.mc4d.client.renderer.entity.state.EntityRenderState4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.particle.ItemPickupParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPickupParticleGroup.class)
class ItemPickupParticleGroupMixin implements ItemPickupParticleGroup4 {
	@Mixin(ItemPickupParticleGroup.ParticleInstance.class)
	static class ParticleInstanceMixin implements ItemPickupParticleGroup4.ParticleInstance {
		@Unique private double wOffset;

		@Override
		public double wOffset() {
			return this.wOffset;
		}
		@Override
		public void setWOffset(double wOffset) {
			this.wOffset = wOffset;
		}

		@ModifyExpressionValue(method = "fromParticle", at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDD)Lnet/minecraft/client/particle/ItemPickupParticleGroup$ParticleInstance;"
		))
		private static ItemPickupParticleGroup.ParticleInstance fromParticle(
			ItemPickupParticleGroup.ParticleInstance original,
			@Local(argsOnly = true, name = "particle") ItemPickupParticle particle,
			@Local(argsOnly = true, name = "partialTickTime") float partialTickTime,
			@Local(name = "time") float time,
			@Local(name = "pos") Vec3 pos
		) {
			ItemPickupParticle4 particle4 = (ItemPickupParticle4) particle;
			EntityRenderState4 renderState4 = (EntityRenderState4) particle.itemRenderState;
			final double wt = Mth.lerp(partialTickTime, particle4.targetWOld(), particle4.targetW());
			final double ww = Mth.lerp(time, renderState4.w(), wt);
			ItemPickupParticleGroup4.ParticleInstance.as(original).setWOffset(ww - ((Vec4) pos).w);
			return original;
		}
	}

	@Mixin(targets = "net.minecraft.client.particle.ItemPickupParticleGroup$State")
	static class StateMixin {
		@Redirect(method = "submit", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"
		))
		<S extends EntityRenderState> void submit(
			EntityRenderDispatcher entityRenderDispatcher, S renderState, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			@Local(name = "instance") ItemPickupParticleGroup.ParticleInstance instance
		) {
			((EntityRenderDispatcher4) entityRenderDispatcher).submit(renderState, camera, x, y, z, ItemPickupParticleGroup4.ParticleInstance.as(instance).wOffset(), poseStack, submitNodeCollector);
		}
	}
}
