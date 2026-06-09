package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SerializableChunkData.class)
class SerializableChunkDataMixin {
	@Shadow
	@Final
	private ChunkPos chunkPos;
	@Unique	private static final String W_POS_TAG = "wPos";

	@Definition(id = "chunkPos", local = @Local(type = ChunkPos.class, name = "chunkPos"))
	@Expression("chunkPos = @(?)")
	@ModifyExpressionValue(method = "parse", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ChunkPos parse_chunkPosW(ChunkPos chunkPos, @Local(argsOnly = true, name = "chunkData") CompoundTag chunkData) {
		ChunkPos4.as(chunkPos).setW(chunkData.getIntOr(W_POS_TAG, 0));
		return chunkPos;
	}

	@Definition(id = "tag", local = @Local(type = CompoundTag.class, name = "tag"))
	@Definition(id = "putInt", method = "Lnet/minecraft/nbt/CompoundTag;putInt(Ljava/lang/String;I)V")
	@Definition(id = "chunkPos", field = "Lnet/minecraft/world/level/chunk/storage/SerializableChunkData;chunkPos:Lnet/minecraft/world/level/ChunkPos;")
	@Definition(id = "z", method = "Lnet/minecraft/world/level/ChunkPos;z()I")
	@Expression("tag.putInt(?, this.chunkPos.z())")
	@Inject(method = "write", at = @At("MIXINEXTRAS:EXPRESSION"))
	void write_chunkPosW(CallbackInfoReturnable<CompoundTag> cir, @Local(name = "tag") CompoundTag tag) {
		tag.putInt(W_POS_TAG, ChunkPos4.as(this.chunkPos).w());
	}
}
