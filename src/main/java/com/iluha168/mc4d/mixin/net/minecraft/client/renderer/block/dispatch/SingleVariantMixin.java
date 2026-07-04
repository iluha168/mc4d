package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block.dispatch;

import com.iluha168.mc4d.client.renderer.block.dispatch.Variant4;
import com.iluha168.mc4d.client.renderer.block.dispatch.WRangeVariant;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SingleVariant.class)
class SingleVariantMixin {
	@Mixin(SingleVariant.Unbaked.class)
	static abstract class UnbakedMixin {
		@Shadow
		public abstract Variant variant();

		@WrapMethod(method = "bake")
		BlockStateModel bake(ModelBaker modelBakery, Operation<BlockStateModel> original) {
			final List<Variant4.WRangeModel> slices = Variant4.as(this.variant()).wRangeModels();
			return slices == null
				? original.call(modelBakery)
				: WRangeVariant.bake(modelBakery, slices, this.variant().modelState().asModelState());
		}
	}
}
