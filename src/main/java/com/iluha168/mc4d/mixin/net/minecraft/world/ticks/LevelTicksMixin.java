package com.iluha168.mc4d.mixin.net.minecraft.world.ticks;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelTicks.class)
class LevelTicksMixin {
	@Definition(id = "zMax", local = @Local(type = int.class, name = "zMax"))
	@Expression("zMax = @(?)")
	@Inject(method = "forContainersInArea", at = @At("MIXINEXTRAS:EXPRESSION"))
	<T> void forContainersInArea_wMin_wMax(
		BoundingBox bb, LevelTicks.PosAndContainerConsumer<T> ouput, CallbackInfo ci,
		@Share("wMin") LocalIntRef wMin,
		@Share("wMax") LocalIntRef wMax
	) {
		BoundingBox4 bb4 = (BoundingBox4) bb;
		wMin.set(SectionPos.posToSectionCoord(bb4.minW()));
		wMax.set(SectionPos.posToSectionCoord(bb4.maxW()));
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "zMin", local = @Local(type = int.class, name = "zMin"))
	@Expression("z = @(zMin)")
	@Inject(method = "forContainersInArea", at = @At("MIXINEXTRAS:EXPRESSION"))
	<T> void forContainersInArea_w(
		BoundingBox bb, LevelTicks.PosAndContainerConsumer<T> ouput, CallbackInfo ci,
		@Share("wMin") LocalIntRef wMin,
		@Share("w") LocalIntRef w
	) {
		w.set(wMin.get());
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "forContainersInArea", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int forContainersInArea_incrementW(
		int one,
		@Share("wMin") LocalIntRef wMin,
		@Share("wMax") LocalIntRef wMax,
		@Share("w") LocalIntRef w
	) {
		w.set(w.get() + 1);
		if (w.get() <= wMax.get()) return 0;
		w.set(wMin.get());
		return 1;
	}
	@Redirect(method = "forContainersInArea", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long forContainersInArea_pack(int x, int z, @Share("w") LocalIntRef w) {
		return ChunkPos4.pack(x, z, w.get());
	}
}
