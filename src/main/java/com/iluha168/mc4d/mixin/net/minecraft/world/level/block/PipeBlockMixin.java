package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.level.block.state.properties.BlockStateProperties4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(PipeBlock.class)
class PipeBlockMixin {
	@Definition(id = "PROPERTY_BY_DIRECTION", field = "Lnet/minecraft/world/level/block/PipeBlock;PROPERTY_BY_DIRECTION:Ljava/util/Map;")
	@Expression("PROPERTY_BY_DIRECTION = @(?)")
	@ModifyArg(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lcom/google/common/collect/Maps;newEnumMap(Ljava/util/Map;)Ljava/util/EnumMap;"
	))
	private static Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION(Map<Direction, BooleanProperty> map) {
		return new ImmutableMap.Builder<Direction, BooleanProperty>()
			.putAll(map)
			.put(Direction4.ANA, BlockStateProperties4.ANA)
			.put(Direction4.KATA, BlockStateProperties4.KATA)
			.buildOrThrow();
	}
}
