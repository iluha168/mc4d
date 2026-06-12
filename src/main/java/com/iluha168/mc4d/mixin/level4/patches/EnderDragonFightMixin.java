package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragonFight.class)
class EnderDragonFightMixin {
	// TODO the rest

	@Definition(id = "ChunkPos", type = ChunkPos.class)
	@Expression("new ChunkPos(0, 0)")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	ChunkPos tick_chunkPos_zero(ChunkPos original) {
		ChunkPos4.as(original).setW(0);
		return original;
	}

	// TODO the rest
}
