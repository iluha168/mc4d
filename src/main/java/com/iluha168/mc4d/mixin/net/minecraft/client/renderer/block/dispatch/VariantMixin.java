package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block.dispatch;

import com.iluha168.mc4d.client.renderer.block.dispatch.Variant4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ResolvableModel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Variant.class)
abstract
class VariantMixin {
	@Shadow
	@Final
	private Variant.SimpleModelState modelState;

	@Inject(method = "resolveDependencies", at = @At("TAIL"))
	void resolveDependencies(ResolvableModel.Resolver resolver, CallbackInfo ci) {
		final List<Variant4.WRangeModel> slices = Variant4.SimpleModelState.as(this.modelState).wRangeModels();
		if (slices != null)
			for (final Variant4.WRangeModel slice : slices)
				resolver.markDependency(slice.model());
	}

	@Mixin(Variant.SimpleModelState.class)
	static class SimpleModelStateMixin implements Variant4.SimpleModelState {
		@Unique
		private @Nullable List<Variant4.WRangeModel> wRangeModels;

		@Override
		public @Nullable List<Variant4.WRangeModel> wRangeModels() {
			return this.wRangeModels;
		}
		@Override
		public void setWRangeModels(@Nullable List<Variant4.WRangeModel> wRangeModels) {
			this.wRangeModels = wRangeModels;
		}

		@Definition(id = "MAP_CODEC", field = "Lnet/minecraft/client/renderer/block/dispatch/Variant$SimpleModelState;MAP_CODEC:Lcom/mojang/serialization/MapCodec;")
		@Expression("MAP_CODEC = @(?)")
		@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static MapCodec<Variant.SimpleModelState> MAP_CODEC(MapCodec<Variant.SimpleModelState> original) {
			return RecordCodecBuilder.mapCodec(
				i -> i.group(
						Quadrant.CODEC.optionalFieldOf("x", Quadrant.R0).forGetter(Variant.SimpleModelState::x),
						Quadrant.CODEC.optionalFieldOf("y", Quadrant.R0).forGetter(Variant.SimpleModelState::y),
						Quadrant.CODEC.optionalFieldOf("z", Quadrant.R0).forGetter(Variant.SimpleModelState::z),
						Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(Variant.SimpleModelState::uvLock),
						Variant4.W_RANGES_CODEC.optionalFieldOf("w")
							.forGetter(state -> Optional.ofNullable(Variant4.SimpleModelState.as(state).wRangeModels()))
					)
					.apply(i, (x, y, z, uvLock, w) -> {
						final Variant.SimpleModelState state = new Variant.SimpleModelState(x, y, z, uvLock);
						w.ifPresent(Variant4.SimpleModelState.as(state)::setWRangeModels);
						return state;
					})
			);
		}

		@ModifyReturnValue(method = "withX", at = @At("RETURN"))
		Variant.SimpleModelState withX(Variant.SimpleModelState result) {
			Variant4.SimpleModelState.as(result).setWRangeModels(this.wRangeModels);
			return result;
		}

		@ModifyReturnValue(method = "withY", at = @At("RETURN"))
		Variant.SimpleModelState withY(Variant.SimpleModelState result) {
			Variant4.SimpleModelState.as(result).setWRangeModels(this.wRangeModels);
			return result;
		}

		@ModifyReturnValue(method = "withZ", at = @At("RETURN"))
		Variant.SimpleModelState withZ(Variant.SimpleModelState result) {
			Variant4.SimpleModelState.as(result).setWRangeModels(this.wRangeModels);
			return result;
		}

		@ModifyReturnValue(method = "withUvLock", at = @At("RETURN"))
		Variant.SimpleModelState withUvLock(Variant.SimpleModelState result) {
			Variant4.SimpleModelState.as(result).setWRangeModels(this.wRangeModels);
			return result;
		}
	}
}
