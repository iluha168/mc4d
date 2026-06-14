package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFluidInteraction.class)
class EntityFluidInteractionMixin {
	@Redirect(method = "update", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/EntityFluidInteraction;hasFluidAndLoaded(Lnet/minecraft/world/level/Level;IIIIII)Z"
	))
	boolean update_hasFluidAndLoaded(
		Level level, int x0, int y0, int z0, int x1, int y1, int z1,
		@Local(name = "box") AABB box,
		@Share("w0") LocalIntRef w0,
		@Share("w1") LocalIntRef w1
	) {
		if (!(box instanceof AABB4 box4)) throw Err4.container3();
		w0.set(Mth.floor(box4.minW));
		w1.set(Mth.ceil(box4.maxW) - 1);
		return hasFluidAndLoaded(level, x0, y0, z0, w0.get() - 1, x1, y1, z1, w1.get() + 1);
	}
	@Definition(id = "eyeBlockZ", local = @Local(type = int.class, name = "eyeBlockZ"))
	@Expression("eyeBlockZ = @(?)")
	@Inject(method = "update", at = @At("MIXINEXTRAS:EXPRESSION"))
	void update_eyeBlockW(Entity entity, boolean ignoreCurrent, CallbackInfo ci, @Share("eyeBlockW") LocalIntRef eyeBlockW) {
		eyeBlockW.set(((Entity4) entity).getBlockW());
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "z0", local = @Local(type = int.class, name = "z0"))
	@Expression("z = @(z0)")
	@Inject(method = "update", at = @At("MIXINEXTRAS:EXPRESSION"))
	void update_w0(Entity entity, boolean ignoreCurrent, CallbackInfo ci, @Share("w") LocalIntRef w, @Share("w0") LocalIntRef w0) {
		w.set(w0.get());
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "update", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int update_incrementW(
		int one,
		@Share("w") LocalIntRef w,
		@Share("w0") LocalIntRef w0, @Share("w1") LocalIntRef w1
	) {
		w.set(w.get() + 1);
		if (w.get() <= w1.get()) return 0;
		w.set(w0.get());
		return 1;
	}
	@Redirect(method = "update", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos update_set(BlockPos.MutableBlockPos mutablePos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) mutablePos).set(x, y, z, w.get());
	}

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "eyeBlockZ", local = @Local(type = int.class, name = "eyeBlockZ"))
	@Expression("z == eyeBlockZ")
	@ModifyExpressionValue(method = "update", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean update_eyeBlockW(boolean original, @Share("w") LocalIntRef w, @Share("eyeBlockW") LocalIntRef eyeBlockW) {
		return original && w.get() == eyeBlockW.get();
	}

	@Overwrite
	@Deprecated
	private static boolean hasFluidAndLoaded(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static boolean hasFluidAndLoaded(Level level, int x0, int y0, int z0, int w0, int x1, int y1, int z1, int w1) {
		int sectionX0 = SectionPos.blockToSectionCoord(x0);
		int sectionY0 = SectionPos.blockToSectionCoord(y0);
		int sectionZ0 = SectionPos.blockToSectionCoord(z0);
		int sectionW0 = SectionPos.blockToSectionCoord(w0);
		int sectionX1 = SectionPos.blockToSectionCoord(x1);
		int sectionY1 = SectionPos.blockToSectionCoord(y1);
		int sectionZ1 = SectionPos.blockToSectionCoord(z1);
		int sectionW1 = SectionPos.blockToSectionCoord(w1);
		boolean hasFluid = false;

		LevelReader4 level4 = (LevelReader4) level;
		for (int chunkZ = sectionZ0; chunkZ <= sectionZ1; chunkZ++)
			for (int chunkX = sectionX0; chunkX <= sectionX1; chunkX++)
				for (int chunkW = sectionW0; chunkW <= sectionW1; chunkW++) {
					ChunkAccess chunk = level4.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.FULL, false);
					if (chunk == null) {
						return false;
					}

					LevelChunkSection[] sections = chunk.getSections();

					for (int sectionY = sectionY0; sectionY <= sectionY1; sectionY++) {
						int sectionIndex = chunk.getSectionIndexFromSectionY(sectionY);
						if (sectionIndex >= 0 && sectionIndex < sections.length) {
							hasFluid |= sections[sectionIndex].hasFluid();
						}
					}
				}

		return hasFluid;
	}

	// TODO Tracker
}
