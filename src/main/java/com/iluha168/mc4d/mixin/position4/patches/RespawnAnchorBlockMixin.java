package com.iluha168.mc4d.mixin.position4.patches;

import com.google.common.collect.ImmutableList;
import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RespawnAnchorBlock.class)
class RespawnAnchorBlockMixin {
	@Definition(id = "RESPAWN_HORIZONTAL_OFFSETS", field = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;RESPAWN_HORIZONTAL_OFFSETS:Lcom/google/common/collect/ImmutableList;")
	@Expression("RESPAWN_HORIZONTAL_OFFSETS = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS(ImmutableList<Vec3i> offsets3d) {
		return new ImmutableList.Builder<Vec3i>()
			.addAll(offsets3d.stream().map(v -> Vec4i.from(v.getX(), v.getY(), v.getZ(), +0)).iterator())
			.addAll(offsets3d.stream().map(v -> Vec4i.from(v.getX(), v.getY(), v.getZ(), -1)).iterator())
			.addAll(offsets3d.stream().map(v -> Vec4i.from(v.getX(), v.getY(), v.getZ(), +1)).iterator())
			.build();
	}
}
