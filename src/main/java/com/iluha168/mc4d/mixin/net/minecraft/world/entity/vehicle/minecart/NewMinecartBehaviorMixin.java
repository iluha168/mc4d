package com.iluha168.mc4d.mixin.net.minecraft.world.entity.vehicle.minecart;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.vehicle.minecart.NewMinecartBehavior4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Function5;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(NewMinecartBehavior.class)
class NewMinecartBehaviorMixin implements NewMinecartBehavior4 {
	// TODO everything else

	@Mixin(NewMinecartBehavior.MinecartStep.class)
	static class MinecartStepMixin implements NewMinecartBehavior4.MinecartStep {
		@Unique private float wRot = Float.NaN, vRot = Float.NaN;

		@Override
		public float wRot() {
			if (Float.isNaN(this.wRot)) throw Err4.field4missing("wRot");
			return this.wRot;
		}
		@Override
		public float vRot() {
			if (Float.isNaN(this.vRot)) throw Err4.field4missing("vRot");
			return this.vRot;
		}

		@Override
		public void setWRot(float wRot) {
			this.wRot = wRot;
		}
		@Override
		public void setVRot(float vRot) {
			this.vRot = vRot;
		}

		@Redirect(method = "<clinit>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function5;)Lnet/minecraft/network/codec/StreamCodec;"
		))
		private static StreamCodec<ByteBuf, NewMinecartBehavior.MinecartStep> STREAM_CODEC(
			StreamCodec<ByteBuf, Vec3> codec1, Function<NewMinecartBehavior.MinecartStep, Vec3> getter1,
			StreamCodec<ByteBuf, Vec3> codec2, Function<NewMinecartBehavior.MinecartStep, Vec3> getter2,
			StreamCodec<ByteBuf, Float> codec3, Function<NewMinecartBehavior.MinecartStep, Float> getter3,
			StreamCodec<ByteBuf, Float> codec4, Function<NewMinecartBehavior.MinecartStep, Float> getter4,
			StreamCodec<ByteBuf, Float> codec5, Function<NewMinecartBehavior.MinecartStep, Float> getter5,
			Function5<Vec3, Vec3, Float, Float, Float, NewMinecartBehavior.MinecartStep> constructor
		) {
			return StreamCodec.composite(
				Vec4.STREAM_CODEC, getter1.andThen(Vec4.class::cast),
				Vec4.STREAM_CODEC, getter2.andThen(Vec4.class::cast),
				codec3, getter3,
				codec4, getter4,
				codec4, NewMinecartBehavior4.MinecartStep::wRot,
				codec3, NewMinecartBehavior4.MinecartStep::vRot,
				codec5, getter5,
				NewMinecartBehavior4.MinecartStep::from
			);
		}

		@ModifyExpressionValue(method = "<clinit>", at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFF)Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior$MinecartStep;"
		))
		private static NewMinecartBehavior.MinecartStep ZERO_step(NewMinecartBehavior.MinecartStep step) {
			final NewMinecartBehavior4.MinecartStep step4 = NewMinecartBehavior4.MinecartStep.as(step);
			step4.setWRot(0);
			step4.setVRot(0);
			return step;
		}
	}

	// TODO everything else
}
