package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
class BlockEntityMixin {
	@Shadow
	@Final
	protected BlockPos worldPosition;

	@Definition(id = "blockToSectionCoord", method = "Lnet/minecraft/core/SectionPos;blockToSectionCoord(I)I")
	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("blockToSectionCoord(x)")
	@Inject(method = "getPosFromTag", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void getPosFromTag_initW(ChunkPos base, CompoundTag entityTag, CallbackInfoReturnable<BlockPos> cir, @Share("w") LocalIntRef w) {
		w.set(entityTag.getIntOr("w", 0));
	}
	@Definition(id = "sectionZ", local = @Local(type = int.class, name = "sectionZ"))
	@Expression("sectionZ != ?")
	@ModifyExpressionValue(method = "getPosFromTag", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean getPosFromTag_wrongChunk(boolean wrongChunk, @Local(argsOnly = true, name = "base") ChunkPos base, @Share("w") LocalIntRef w) {
		return wrongChunk || SectionPos.blockToSectionCoord(w.get()) != ChunkPos4.as(base).w();
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "base", local = @Local(type = ChunkPos.class, argsOnly = true))
	@Definition(id = "getBlockZ", method = "Lnet/minecraft/world/level/ChunkPos;getBlockZ(I)I")
	@Expression("z = base.getBlockZ(?)")
	@Inject(method = "getPosFromTag", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void getPosFromTag_correctW(ChunkPos base, CompoundTag entityTag, CallbackInfoReturnable<BlockPos> cir, @Share("w") LocalIntRef w) {
		w.set(ChunkPos4.as(base).getBlockW(SectionPos.sectionRelative(w.get())));
	}
	@ModifyExpressionValue(method = "getPosFromTag", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos getPosFromTag_setW(BlockPos pos, @Share("w") LocalIntRef w) {
		Vec4i.setW(pos, w.get());
		return pos;
	}

	@Inject(method = "saveMetadata", at = @At("TAIL"))
	private void saveMetadata(ValueOutput output, CallbackInfo ci) {
		output.putInt("w", Vec4i.getW(this.worldPosition));
	}
}
