package com.iluha168.mc4d.mixin.net.minecraft.world.ticks;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.ticks.SavedTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(SavedTick.class)
class SavedTickMixin {
	@Redirect(method = "codec", at = @At(
		value = "INVOKE",
		target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
	))
	private static MapCodec<BlockPos> codec(Function<RecordCodecBuilder.Instance<BlockPos>, ? extends App<RecordCodecBuilder.Mu<BlockPos>, BlockPos>> builder) {
		return RecordCodecBuilder.mapCodec(
			i -> i.group(
					Codec.INT.fieldOf("x").forGetter(Vec3i::getX),
					Codec.INT.fieldOf("y").forGetter(Vec3i::getY),
					Codec.INT.fieldOf("z").forGetter(Vec3i::getZ),
					Codec.INT.fieldOf("w").forGetter(Vec4i::getW)
				)
				.apply(i, BlockPos4::from)
		);
	}
}
