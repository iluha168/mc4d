package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.core.SectionPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.SectionTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionTracker.class)
class SectionTrackerMixin {
	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = @(-1)")
	@Inject(method = "checkNeighborsAfterUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
	void checkNeighborsAfterUpdate_offsetW(long node, int level, boolean onlyDecrease, CallbackInfo ci, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(-1);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = offsetZ + @(1)")
	@ModifyExpressionValue(method = "checkNeighborsAfterUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int checkNeighborsAfterUpdate_incrementOffsetW(int one, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(offsetW.get() + 1);
		if (offsetW.get() <= 1) return 0;
		offsetW.set(-1);
		return 1;
	}
	@Redirect(method = "checkNeighborsAfterUpdate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;offset(JIII)J"
	))
	long checkNeighborsAfterUpdate_offset(long sectionNode, int stepX, int stepY, int stepZ, @Share("offsetW") LocalIntRef offsetW) {
		return SectionPos4.offset(sectionNode, stepX, stepY, stepZ, offsetW.get());
	}

	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = @(-1)")
	@Inject(method = "getComputedLevel", at = @At("MIXINEXTRAS:EXPRESSION"))
	void getComputedLevel_offsetW(long node, long knownParent, int knownLevelFromParent, CallbackInfoReturnable<Integer> cir, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(-1);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = offsetZ + @(1)")
	@ModifyExpressionValue(method = "getComputedLevel", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int getComputedLevel_incrementOffsetW(int one, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(offsetW.get() + 1);
		if (offsetW.get() <= 1) return 0;
		offsetW.set(-1);
		return 1;
	}
	@Redirect(method = "getComputedLevel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;offset(JIII)J"
	))
	long getComputedLevel_offset(long sectionNode, int stepX, int stepY, int stepZ, @Share("offsetW") LocalIntRef offsetW) {
		return SectionPos4.offset(sectionNode, stepX, stepY, stepZ, offsetW.get());
	}
}
