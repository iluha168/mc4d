package com.iluha168.mc4d.mixin.net.minecraft.world.level.entity;

import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySectionStorage.class)
class EntitySectionStorageMixin {
	@Shadow
	@Final
	private LongSortedSet sectionIds;

	@Definition(id = "zMax", local = @Local(type = int.class, name = "zMax"))
	@Expression("zMax = @(?)")
	@Inject(method = "forEachAccessibleNonEmptySection", at = @At("MIXINEXTRAS:EXPRESSION"))
	void forEachAccessibleNonEmptySection_wMinMax(
		AABB bb, AbortableIterationConsumer<EntitySection<?>> output, CallbackInfo ci,
		@Share("wMin") LocalIntRef wMin,
		@Share("wMax") LocalIntRef wMax
	) {
		if (!(bb instanceof AABB4 bb4)) throw Err4.container3();
		wMin.set(SectionPos.posToSectionCoord(bb4.minW - 2.0));
		wMax.set(SectionPos.posToSectionCoord(bb4.maxW + 2.0));
	}
	@Redirect(method = "forEachAccessibleNonEmptySection", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long forEachAccessibleNonEmptySection_asLong(int x, int y, int z) {
		return 0; // The returned value is not used
	}
	@WrapOperation(method = "forEachAccessibleNonEmptySection", at = @At(
		value = "INVOKE",
		target = "Lit/unimi/dsi/fastutil/longs/LongSortedSet;subSet(JJ)Lit/unimi/dsi/fastutil/longs/LongSortedSet;"
	))
	LongSortedSet forEachAccessibleNonEmptySection_subSet(
		LongSortedSet sectionIds, long from, long to, Operation<LongSortedSet> original,
		@Local(name = "x") int x,
		@Share("wMin") LocalIntRef wMin,
		@Share("wMax") LocalIntRef wMax
	) {
		// Vanilla relies on X being the most significant field in SectionPos packing, so whatever Y and Z are, they are in a 1 dimensional packed range.
		// But W is most significant now, so YZ spans a 2D block of non-continuous SectionPos longs.
		// We split that range into a bunch of 1D ranges to match vanilla, ezpz:
		LongSortedSet sections = new LongAVLTreeSet();
		for (int w = wMin.get(); w <= wMax.get(); w++)
			sections.addAll(original.call(
				sectionIds,
				SectionPos4.asLong(x, 0, 0, w),
				SectionPos4.asLong(x, -1, -1, w) + 1L
			));
		return sections;
	}

	@Definition(id = "getChunkSections", method = "Lnet/minecraft/world/level/entity/EntitySectionStorage;getChunkSections(II)Lit/unimi/dsi/fastutil/longs/LongSortedSet;")
	@Expression("this.getChunkSections(?, ?)")
	@Redirect(method = "getExistingSectionPositionsInChunk", at = @At("MIXINEXTRAS:EXPRESSION"))
	LongSortedSet getExistingSectionPositionsInChunk(EntitySectionStorage<?> This, int x, int z, @Local(argsOnly = true, name = "chunkKey") long chunkKey) {
		return this.getChunkSections(x, z, ChunkPos4.getW(chunkKey));
	}

	@Overwrite
	@Deprecated
	private LongSortedSet getChunkSections(int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private LongSortedSet getChunkSections(int x, int z, int w) {
		long lowestAbsoluteSectionKey = SectionPos4.asLong(x, 0, z, w);
		long highestAbsoluteSectionKey = SectionPos4.asLong(x, -1, z, w);
		return this.sectionIds.subSet(lowestAbsoluteSectionKey, highestAbsoluteSectionKey + 1L);
	}

	@Redirect(method = "getChunkKeyFromSectionKey", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	private static long getChunkKeyFromSectionKey(int x, int z, @Local(argsOnly = true, name = "sectionPos") long sectionPos) {
		return ChunkPos4.pack(x, z, SectionPos4.w(sectionPos));
	}
}
