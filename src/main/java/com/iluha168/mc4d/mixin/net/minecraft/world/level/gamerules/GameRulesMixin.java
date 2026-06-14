package com.iluha168.mc4d.mixin.net.minecraft.world.level.gamerules;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRules.class)
class GameRulesMixin {
	@Definition(id = "RANDOM_TICK_SPEED", field = "Lnet/minecraft/world/level/gamerules/GameRules;RANDOM_TICK_SPEED:Lnet/minecraft/world/level/gamerules/GameRule;")
	@Definition(id = "registerInteger", method = "Lnet/minecraft/world/level/gamerules/GameRules;registerInteger(Ljava/lang/String;Lnet/minecraft/world/level/gamerules/GameRuleCategory;II)Lnet/minecraft/world/level/gamerules/GameRule;")
	@Expression("RANDOM_TICK_SPEED = @(registerInteger(?, ?, ?, ?))")
	@ModifyArg(method = "<clinit>", index = 2, at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int RANDOM_TICK_SPEED(int defaultValue) {
		// It is necessary to increase tick speed, because the chunks are 16 times larger.
		// This mixin actually retains vanilla crop speed.
		return defaultValue * LevelChunkSection.SECTION_WIDTH;
	}
}
