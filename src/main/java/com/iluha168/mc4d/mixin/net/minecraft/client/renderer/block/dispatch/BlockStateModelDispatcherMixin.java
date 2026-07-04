package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block.dispatch;

import com.iluha168.mc4d.client.renderer.block.dispatch.Variant4;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(BlockStateModelDispatcher.class)
abstract
class BlockStateModelDispatcherMixin {
	@Shadow
	public abstract Optional<BlockStateModelDispatcher.SimpleModelSelectors> simpleModels();

	@Shadow
	public abstract Optional<BlockStateModelDispatcher.MultiPartDefinition> multiPart();

	@Inject(method = "instantiateVanilla", at = @At("HEAD"))
	void instantiateVanilla(
		StateDefinition<Block, BlockState> stateDefinition, Supplier<String> source,
		CallbackInfoReturnable<Map<BlockState, BlockStateModel.UnbakedRoot>> cir
	) {
		// Logs for finding not patched models
		this.simpleModels().ifPresent(simpleModels -> simpleModels.models().forEach(
			(selector, model) -> Variant4.warnWCoverage(model, selector, source)
		));
		this.multiPart().ifPresent(definition -> {
			final List<Selector> selectors = definition.selectors();
			for (int i = 0; i < selectors.size(); i++)
				Variant4.warnWCoverage(selectors.get(i).variant(), "multipart[" + i + "]", source);
		});
	}
}
