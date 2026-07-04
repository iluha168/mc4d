package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block.dispatch;

import com.iluha168.mc4d.client.renderer.block.dispatch.Variant4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ResolvableModel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Variant.class)
class VariantMixin implements Variant4 {
	@Unique
	private @Nullable List<WRangeModel> wRangeModels;

	@Override
	public @Nullable List<WRangeModel> wRangeModels() {
		return this.wRangeModels;
	}
	@Override
	public void setWRangeModels(@NonNull List<WRangeModel> wRangeModels) {
		this.wRangeModels = List.copyOf(wRangeModels);
	}
	@Override
	public void setWRangeModelsForce(@Nullable List<WRangeModel> wRangeModels) {
		this.wRangeModels = wRangeModels;
	}

	@Definition(id = "MAP_CODEC", field = "Lnet/minecraft/client/renderer/block/dispatch/Variant;MAP_CODEC:Lcom/mojang/serialization/MapCodec;")
	@Expression("MAP_CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static MapCodec<Variant> MAP_CODEC(MapCodec<Variant> original) {
		return RecordCodecBuilder.mapCodec(
			i -> i.group(
					Variant4.MODEL_CODEC.fieldOf("model").forGetter(variant -> Variant4.as(variant).wModel()),
					Variant.SimpleModelState.MAP_CODEC.forGetter(Variant::modelState)
				)
				.apply(i, Variant4::from)
		);
	}

	@ModifyReturnValue(method = "withState", at = @At("RETURN"))
	Variant withState(Variant result) {
		Variant4.as(result).setWRangeModelsForce(this.wRangeModels);
		return result;
	}

	@Inject(method = "resolveDependencies", at = @At("TAIL"))
	void resolveDependencies(ResolvableModel.Resolver resolver, CallbackInfo ci) {
		if (this.wRangeModels != null)
			for (final WRangeModel slice : this.wRangeModels)
				resolver.markDependency(slice.model());
	}
}
