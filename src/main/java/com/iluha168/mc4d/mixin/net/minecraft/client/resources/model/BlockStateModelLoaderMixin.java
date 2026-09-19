package com.iluha168.mc4d.mixin.net.minecraft.client.resources.model;

import com.iluha168.mc4d.api.net.minecraft.client.renderer.block.dispatch.Variant4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(BlockStateModelLoader.class)
class BlockStateModelLoaderMixin {
	@WrapOperation(method = "loadBlockStateDefinitionStack", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/block/dispatch/BlockStateModelDispatcher;instantiate(Lnet/minecraft/world/level/block/state/StateDefinition;Ljava/util/function/Supplier;)Ljava/util/Map;"
	))
	private static Map<BlockState, BlockStateModel.UnbakedRoot> loadBlockStateDefinitionStack(
		BlockStateModelDispatcher dispatcher, StateDefinition<Block, BlockState> stateDefinition, Supplier<String> source,
		Operation<Map<BlockState, BlockStateModel.UnbakedRoot>> original,
		@Local Iterator<?> definitions
	) {
		if (!definitions.hasNext()) { // Warn only top-level definition.
			dispatcher.simpleModels().ifPresent(selectors -> selectors.models().forEach(
				(selector, model) -> Variant4.warnWCoverage(model, selector, source)
			));
			dispatcher.multiPart().ifPresent(definition -> {
				final List<Selector> selectors = definition.selectors();
				for (int i = 0; i < selectors.size(); i++)
					Variant4.warnWCoverage(selectors.get(i).variant(), "multipart[" + i + "]", source);
			});
		}
		return original.call(dispatcher, stateDefinition, source);
	}
}
