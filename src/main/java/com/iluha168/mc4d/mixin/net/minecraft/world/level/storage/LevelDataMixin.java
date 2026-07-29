package com.iluha168.mc4d.mixin.net.minecraft.world.level.storage;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(LevelData.class)
interface LevelDataMixin extends LevelData4 {
	@Mixin(LevelData.RespawnData.class)
	abstract class RespawnDataMixin implements LevelData4.RespawnData {
		@Unique
		private float wRot = 0.0F, vRot = 0.0F;

		@Override
		public float wRot() {
			return this.wRot;
		}
		@Override
		public void setWRot(float wRot) {
			this.wRot = wRot;
		}
		@Override
		public float vRot() {
			return this.vRot;
		}
		@Override
		public void setVRot(float vRot) {
			this.vRot = vRot;
		}

		@Definition(id = "MAP_CODEC", field = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;MAP_CODEC:Lcom/mojang/serialization/MapCodec;")
		@Expression("MAP_CODEC = @(?)")
		@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static MapCodec<LevelData.RespawnData> MAP_CODEC(MapCodec<LevelData.RespawnData> original) {
			return RecordCodecBuilder.mapCodec(
				i -> i.group(
						GlobalPos.MAP_CODEC.forGetter(LevelData.RespawnData::globalPos),
						Codec.floatRange(-180.0F, 180.0F).fieldOf("yaw").forGetter(LevelData.RespawnData::yaw),
						Codec.floatRange(-90.0F, 90.0F).fieldOf("pitch").forGetter(LevelData.RespawnData::pitch),
						Codec.floatRange(-90.0F, 90.0F).fieldOf("wRot").forGetter(LevelData4.RespawnData::wRot),
						Codec.floatRange(-180.0F, 180.0F).fieldOf("vRot").forGetter(LevelData4.RespawnData::vRot)
					)
					.apply(i, LevelData4.RespawnData::from)
			);
		}
		@Redirect(method = "<clinit>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function3;)Lnet/minecraft/network/codec/StreamCodec;"
		))
		private static StreamCodec<ByteBuf, LevelData.RespawnData> STREAM_CODEC(
			StreamCodec<ByteBuf, GlobalPos> codec1, Function<LevelData.RespawnData, GlobalPos> getter1,
			StreamCodec<ByteBuf, Float> codec2, Function<LevelData.RespawnData, Float> getter2,
			StreamCodec<ByteBuf, Float> codec3, Function<LevelData.RespawnData, Float> getter3,
			Function3<GlobalPos, Float, Float, LevelData.RespawnData> constructor
		) {
			return StreamCodec.composite(
				codec1, getter1,
				codec2, getter2,
				codec3, getter3,
				codec3, LevelData4.RespawnData::wRot,
				codec2, LevelData4.RespawnData::vRot,
				LevelData4.RespawnData::from
			);
		}

		@Overwrite
		@Deprecated
		public static LevelData.RespawnData of(ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch) {
			throw Err4.rotation("LevelData4.RespawnData#of");
		}

		@WrapMethod(method = "equals")
		public boolean equals(Object o, Operation<Boolean> original) {
			if (!original.call(o)) {
				return false;
			}
			final LevelData4.RespawnData o4 = (LevelData4.RespawnData) o;
			return this.wRot == o4.wRot() && this.vRot == o4.vRot();
		}

		@WrapMethod(method = "hashCode")
		public int hashCode(Operation<Integer> original) {
			return (original.call() * 31 + Float.hashCode(this.wRot)) * 31 + Float.hashCode(this.vRot);
		}
	}
}
