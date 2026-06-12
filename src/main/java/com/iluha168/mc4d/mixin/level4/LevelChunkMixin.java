package com.iluha168.mc4d.mixin.level4;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.level.levelgen.Heightmap4;
import com.iluha168.mc4d.world.level.lighting.ChunkSkyLightSources4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
class LevelChunkMixin {
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(?)")
	@Inject(method = "getBlockState", at = @At("MIXINEXTRAS:EXPRESSION"))
	void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir, @Share("w") LocalIntRef w) {
		w.set(Vec4i.getW(pos));
	}
	// TODO getBlockState DebugLevelSource
	@Redirect(method = "getBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState getBlockState(LevelChunkSection currentSection, int sectionX, int sectionY, int sectionZ, @Share("w") LocalIntRef w) {
		return ((LevelChunkSection4) currentSection).getBlockState(sectionX, sectionY, sectionZ, SectionPos.sectionRelative(w.get()));
	}

	@Overwrite
	public FluidState getFluidState(BlockPos pos) {
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();
		final int w = Vec4i.getW(pos);
		ChunkAccess This = ((ChunkAccess) (Object) this);
		try {
			int sectionIndex = This.getSectionIndex(y);
			if (sectionIndex >= 0 && sectionIndex < This.sections.length) {
				LevelChunkSection currentSection = This.sections[sectionIndex];
				if (!currentSection.hasOnlyAir()) {
					return ((LevelChunkSection4) currentSection).getFluidState(
						SectionPos.sectionRelative(x),
						SectionPos.sectionRelative(y),
						SectionPos.sectionRelative(z),
						SectionPos.sectionRelative(w)
					);
				}
			}

			return Fluids.EMPTY.defaultFluidState();
		} catch (Throwable var7) {
			CrashReport report = CrashReport.forThrowable(var7, "Getting fluid state");
			CrashReportCategory category = report.addCategory("Block being got");
			category.setDetail("Location", () -> CrashReportCategory.formatLocation(This, x, y, z)); // TODO formatLocation w
			throw new ReportedException(report);
		}
	}

	@Overwrite
	@Deprecated
	public FluidState getFluidState(int x, int y, int z) {
		throw Err4.arguments3("BlockGetter#getFluidState(BlockPos)");
	}

	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState setBlockState_section(
		LevelChunkSection section, int sectionX, int sectionY, int sectionZ, BlockState state,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Share("localW") LocalIntRef localW
	) {
		localW.set(SectionPos.sectionRelative(Vec4i.getW(pos)));
		return ((LevelChunkSection4) section).setBlockState(sectionX, sectionY, sectionZ, localW.get(), state);
	}
	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z"
	))
	boolean setBlockState_updateHeightmaps(Heightmap heightmap, int localX, int localY, int localZ, BlockState state, @Share("localW") LocalIntRef localW) {
		return ((Heightmap4) heightmap).update(localX, localY, localZ, localW.get(), state);
	}
	// TODO setBlockState_onSectionEmptinessChanged
	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;update(Lnet/minecraft/world/level/BlockGetter;III)Z"
	))
	boolean setBlockState_updateSkyLightSources(ChunkSkyLightSources skyLightSources, BlockGetter level, int x, int y, int z, @Share("localW") LocalIntRef localW) {
		return ((ChunkSkyLightSources4) skyLightSources).update(level, x, y, z, localW.get());
	}
	@Redirect(method = "setBlockState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	BlockState setBlockState(LevelChunkSection section, int sectionX, int sectionY, int sectionZ, @Share("localW") LocalIntRef localW) {
		return ((LevelChunkSection4) section).getBlockState(sectionX, sectionY, sectionZ, localW.get());
	}
}
