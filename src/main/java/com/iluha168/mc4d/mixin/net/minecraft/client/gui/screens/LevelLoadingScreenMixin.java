package com.iluha168.mc4d.mixin.net.minecraft.client.gui.screens;

import com.iluha168.mc4d.server.MinecraftServer4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
class LevelLoadingScreenMixin {
	/** How many squares to render to each side? */
	@Unique	private static final int TRUNCATE_W_VIEW = 3;
	@Unique	private static final int MARGIN = 5;

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(0)")
	@Inject(method = "extractChunksForRendering", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void extractChunksForRendering_w(
		GuiGraphicsExtractor graphics, int xCenter, int yCenter, int size, int margin, ChunkLoadStatusView statusView, CallbackInfo ci,
		@Share("w") LocalIntRef w
	) {
		w.set(-TRUNCATE_W_VIEW);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "extractChunksForRendering", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int extractChunksForRendering_incrementW(int original, @Share("w") LocalIntRef w, @Local(name = "diameter") int diameter) {
		w.set(w.get() + 1);
		if (w.get() <= TRUNCATE_W_VIEW) return 0;
		w.set(-TRUNCATE_W_VIEW);
		return 1;
	}
	@Definition(id = "xCellStart", local = @Local(type = int.class, name = "xCellStart"))
	@Expression("xCellStart = @(?)")
	@ModifyExpressionValue(method = "extractChunksForRendering", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int extractChunksForRendering(
		int original,
		@Share("w") LocalIntRef w,
		@Local(name = "totalWidth") int totalWidth
	) {
		return original + (w.get()) * (MARGIN + totalWidth);
	}
	@Redirect(method = "extractChunksForRendering", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/progress/ChunkLoadStatusView;get(II)Lnet/minecraft/world/level/chunk/status/ChunkStatus;"
	))
	private static ChunkStatus extractChunksForRendering_getXZ(ChunkLoadStatusView statusView, int x, int z, @Share("w") LocalIntRef w) {
		return ((MinecraftServer4.ChunkLoadStatusView) statusView).get(x, z, w.get() + statusView.radius());
	}
}
