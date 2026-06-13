package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.ChunkTracker;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkTracker.class)
class ChunkTrackerMixin {
	@Inject(method = "checkNeighborsAfterUpdate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;z()I"
	))
	void checkNeighborsAfterUpdate_setW(
		long node, int level, boolean onlyDecrease, CallbackInfo ci,
		@Share("w") LocalIntRef w, @Local(name = "pos") ChunkPos pos
	) {
		w.set(ChunkPos4.as(pos).w());
	}

	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = @(-1)")
	@ModifyExpressionValue(method = "checkNeighborsAfterUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
	int checkNeighborsAfterUpdate_setOffsetW(int negOne, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(negOne);
		return negOne;
	}

	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("? = offsetZ + @(1)")
	@ModifyExpressionValue(method = "checkNeighborsAfterUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
	int checkNeighborsAfterUpdate_incrementOffsetW(int one, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(offsetW.get() + 1);
		if (offsetW.get() <= 1) return 0;
		offsetW.set(-1);
		return 1;
	}

	@Redirect(method = "checkNeighborsAfterUpdate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long checkNeighborsAfterUpdate_pack(
		int x, int z,
		@Share("w") LocalIntRef w,
		@Share("offsetW") LocalIntRef offsetW
	) {
		return ChunkPos4.pack(x, z, w.get() + offsetW.get());
	}

	@Inject(method = "getComputedLevel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;z()I"
	))
	void getComputedLevel_setW(
		long node, long knownParent, int knownLevelFromParent, CallbackInfoReturnable<Integer> cir,
		@Share("w") LocalIntRef w,
		@Local(name = "pos") ChunkPos pos
	) {
		w.set(ChunkPos4.as(pos).w());
	}

	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = @(-1)")
	@ModifyExpressionValue(method = "getComputedLevel", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getComputedLevel_setOffsetW(int negOne, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(negOne);
		return negOne;
	}

	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("? = offsetZ + @(1)")
	@ModifyExpressionValue(method = "getComputedLevel", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getComputedLevel_incrementOffsetW(int one, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(offsetW.get() + 1);
		if (offsetW.get() <= 1) return 0;
		offsetW.set(-1);
		return 1;
	}

	@Redirect(method = "getComputedLevel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long getComputedLevel_pack(
		int x, int z,
		@Share("w") LocalIntRef w,
		@Share("offsetW") LocalIntRef offsetW
	) {
		return ChunkPos4.pack(x, z, w.get() + offsetW.get());
	}
}
