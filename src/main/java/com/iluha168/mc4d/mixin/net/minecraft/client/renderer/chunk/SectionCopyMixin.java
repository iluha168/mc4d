package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.chunk.PalettedContainer4;
import com.iluha168.mc4d.world.level.levelgen.DebugLevelSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionCopy.class)
class SectionCopyMixin {
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(?)")
	@Inject(method = "getBlockState", at = @At("MIXINEXTRAS:EXPRESSION"))
	void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir, @Share("w") LocalIntRef w) {
		w.set(Vec4i.getW(pos));
	}
	@Redirect(method = "getBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/DebugLevelSource;getBlockStateFor(II)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState getBlockState_DebugLevelSource(int worldX, int worldZ, @Share("w") LocalIntRef w) {
		return DebugLevelSource4.getBlockStateFor(worldX, worldZ, w.get());
	}
	@SuppressWarnings("unchecked")
	@Redirect(method = "getBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/PalettedContainer;get(III)Ljava/lang/Object;"
	))
	private <T> T getBlockState(PalettedContainer<T> section, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((PalettedContainer4<T>) section).get(x, y, z, SectionPos.sectionRelative(w.get()));
	}
	// TODO formatLocation
}
