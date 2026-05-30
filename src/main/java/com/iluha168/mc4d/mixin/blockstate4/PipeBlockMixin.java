package com.iluha168.mc4d.mixin.blockstate4;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PipeBlock.class)
class PipeBlockMixin {
	@Mutable
	@Shadow
	@Final
	public static Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void PROPERTY_BY_DIRECTION(CallbackInfo ci) {
		PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(
			Direction.NORTH, PipeBlock.NORTH,
			Direction.EAST, PipeBlock.EAST,
			Direction.SOUTH, PipeBlock.SOUTH,
			Direction.WEST, PipeBlock.WEST,
			Direction.UP, PipeBlock.UP,
			Direction.DOWN, PipeBlock.DOWN,
			Direction4.ANA, BlockStateProperties4.ANA,
			Direction4.KATA, BlockStateProperties4.KATA
		)));
	}
}
