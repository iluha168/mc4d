package com.iluha168.mc4d.mixin.net.minecraft.world.level.storage.loot.predicates;

import com.iluha168.mc4d.advancements.criterion.LocationPredicate4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocationCheck.class)
class LocationCheckMixin {
	@Shadow
	@Final
	private BlockPos offset;

	@Definition(id = "OFFSET_CODEC", field = "Lnet/minecraft/world/level/storage/loot/predicates/LocationCheck;OFFSET_CODEC:Lcom/mojang/serialization/MapCodec;")
	@Expression("OFFSET_CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static MapCodec<BlockPos> OFFSET_CODEC(MapCodec<BlockPos> original) {
		return RecordCodecBuilder.mapCodec(
			i -> i.group(
					Codec.INT.optionalFieldOf("offsetX", 0).forGetter(Vec3i::getX),
					Codec.INT.optionalFieldOf("offsetY", 0).forGetter(Vec3i::getY),
					Codec.INT.optionalFieldOf("offsetZ", 0).forGetter(Vec3i::getZ),
					Codec.INT.optionalFieldOf("offsetW", 0).forGetter(Vec4i::getW)
				)
				.apply(i, BlockPos4::from)
		);
	}

	@Redirect(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/advancements/criterion/LocationPredicate;matches(Lnet/minecraft/server/level/ServerLevel;DDD)Z"
	))
	boolean test(LocationPredicate predicate, ServerLevel level, double x, double y, double z, @Local(name = "pos") Vec3 pos) {
		return LocationPredicate4.as(predicate).matches(level, x, y, z, ((Vec4) pos).w + Vec4i.getW(this.offset));
	}
}
