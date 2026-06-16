package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.chunk.RenderSectionRegion4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.net.neoforged.neoforge.model.data.ModelDataManager4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.Level4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelDataManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderRegionCache.class)
class RenderRegionCacheMixin {
	@Final
	@Shadow
	private Long2ObjectMap<SectionCopy> sectionCopyCache;

	@ModifyConstant(method = "createRegion", constant = @Constant(intValue = 27))
	private int createRegion_sections(int dummy) {
		return 3*3*3*3;
	}
	@Definition(id = "regionSectionZ", local = @Local(type = int.class, name = "regionSectionZ"))
	@Definition(id = "minSectionZ", local = @Local(type = int.class, name = "minSectionZ"))
	@Expression("regionSectionZ = @(minSectionZ)")
	@Inject(method = "createRegion", at = @At("MIXINEXTRAS:EXPRESSION"))
	void createRegion_w(
		ClientLevel level, long sectionNode, CallbackInfoReturnable<RenderSectionRegion> cir,
		@Share("regionSectionW") LocalIntRef regionSectionW,
		@Share("minSectionW") LocalIntRef minSectionW
	) {
		final int sectionW = SectionPos4.w(sectionNode);
		minSectionW.set(sectionW - 1);
		regionSectionW.set(sectionW - 1);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "regionSectionZ", local = @Local(type = int.class, name = "regionSectionZ"))
	@Expression("regionSectionZ = regionSectionZ + @(1)")
	@ModifyExpressionValue(method = "createRegion", at = @At("MIXINEXTRAS:EXPRESSION"))
	int createRegion_incrementW(
		int one,
		@Share("regionSectionW") LocalIntRef regionSectionW,
		@Share("minSectionW") LocalIntRef minSectionW
	) {
		regionSectionW.set(regionSectionW.get() + 1);
		final int maxSectionW = minSectionW.get() + 2;
		if (regionSectionW.get() <= maxSectionW) return 0;
		regionSectionW.set(minSectionW.get());
		return 1;
	}
	@Redirect(method = "createRegion", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;index(IIIIII)I"
	))
	private int createRegion_index(
		int minSectionX, int minSectionY, int minSectionZ, int sectionX, int sectionY, int sectionZ,
		@Share("regionSectionW") LocalIntRef regionSectionW,
		@Share("minSectionW") LocalIntRef minSectionW
	) {
		return RenderSectionRegion4.index(minSectionX, minSectionY, minSectionZ, minSectionW.get(), sectionX, sectionY, sectionZ, regionSectionW.get());
	}
	@Redirect(method = "createRegion", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/RenderRegionCache;getSectionDataCopy(Lnet/minecraft/world/level/Level;III)Lnet/minecraft/client/renderer/chunk/SectionCopy;"
	))
	private SectionCopy createRegion_getSectionDataCopy(
		RenderRegionCache self, Level level, int sectionX, int sectionY, int sectionZ,
		@Share("regionSectionW") LocalIntRef regionSectionW
	) {
		return this.getSectionDataCopy(level, sectionX, sectionY, sectionZ, regionSectionW.get());
	}
	@Redirect(method = "createRegion", at = @At(
		value = "INVOKE",
		target = "Lnet/neoforged/neoforge/model/data/ModelDataManager;snapshotSectionRegion(IIIIII)Lit/unimi/dsi/fastutil/longs/Long2ObjectFunction;"
	))
	private Long2ObjectFunction<ModelData> createRegion_modelData(
		ModelDataManager manager,
		int minSectionX, int minSectionY, int minSectionZ, int maxSectionX, int maxSectionY, int maxSectionZ,
		@Share("minSectionW") LocalIntRef minSectionW
	) {
		return ((ModelDataManager4) manager).snapshotSectionRegion(minSectionX, minSectionY, minSectionZ, minSectionW.get(), maxSectionX, maxSectionY, maxSectionZ, minSectionW.get() + 2);
	}
	@ModifyExpressionValue(method = "createRegion", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/client/multiplayer/ClientLevel;III[Lnet/minecraft/client/renderer/chunk/SectionCopy;Lit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;"
	))
	private RenderSectionRegion createRegion_new(RenderSectionRegion original, @Share("minSectionW") LocalIntRef minSectionW) {
		((RenderSectionRegion4) original).setMinSectionW(minSectionW.get());
		return original;
	}

	@Overwrite
	@Deprecated
	private SectionCopy getSectionDataCopy(Level level, int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private SectionCopy getSectionDataCopy(Level level, int sectionX, int sectionY, int sectionZ, int sectionW) {
		return this.sectionCopyCache.computeIfAbsent(SectionPos4.asLong(sectionX, sectionY, sectionZ, sectionW), _ -> {
			LevelChunk chunk = ((Level4) level).getChunk(sectionX, sectionZ, sectionW);
			return new SectionCopy(chunk, chunk.getSectionIndexFromSectionY(sectionY));
		});
	}
}