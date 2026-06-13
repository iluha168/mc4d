package com.iluha168.mc4d.mixin.net.minecraft.world.level.lighting;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.level.lighting.ChunkSkyLightSources4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSkyLightSources.class)
abstract
class ChunkSkyLightSourcesMixin implements ChunkSkyLightSources4 {
	@Shadow
	@Final
	private BlockPos.MutableBlockPos mutablePos1;

	@Shadow
	@Final
	private BlockPos.MutableBlockPos mutablePos2;

	@Shadow
	private static boolean isEdgeOccluded(BlockState topState, BlockState bottomState) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	private int minY;

	@Shadow
	protected abstract int get(int index);

	@Shadow
	protected abstract boolean updateEdge(BlockGetter level, int index, int oldTopEdgeY, BlockPos topPos, BlockState topState, BlockPos bottomPos, BlockState bottomState);

	@Shadow
	protected abstract int extendSourcesBelowWorld(int value);

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = LevelChunkSection.SECTION_WIDTH * LevelChunkSection.SECTION_WIDTH))
	private int dataSize3D(int value) {
		return value * LevelChunkSection.SECTION_WIDTH;
	}

	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x = @(0)")
	@Inject(method = "fillFrom", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void fillFrom_w(ChunkAccess chunk, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(0);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x = x + @(1)")
	@ModifyExpressionValue(method = "fillFrom", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int fillFrom_incrementW(int original, @Share("w") LocalIntRef w) {
		w.set(w.get() + 1);
		if (w.get() < LevelChunkSection.SECTION_WIDTH) return 0;
		w.set(0);
		return 1;
	}
	@Definition(id = "findLowestSourceY", method = "Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;findLowestSourceY(Lnet/minecraft/world/level/chunk/ChunkAccess;III)I")
	@Expression("this.findLowestSourceY(?, ?, ?, ?)")
	@Redirect(method = "fillFrom", at = @At("MIXINEXTRAS:EXPRESSION"))
	int fillFrom_findLowestSourceY(ChunkSkyLightSources This, ChunkAccess chunk, int topSectionIndex, int x, int z, @Share("w") LocalIntRef w) {
		return this.findLowestSourceY(chunk, topSectionIndex, x, z, w.get());
	}
	@Redirect(method = "fillFrom", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;index(II)I"
	))
	int fillFrom_index(int x, int z, @Share("w") LocalIntRef w) {
		return index(x, z, w.get());
	}

	@Overwrite
	@Deprecated
	private int findLowestSourceY(ChunkAccess chunk, int topSectionIndex, int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private int findLowestSourceY(ChunkAccess chunk, int topSectionIndex, int x, int z, int w) {
		int topY = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(topSectionIndex) + 1);
		BlockPos.MutableBlockPos topPos = ((BlockPos4.MutableBlockPos) this.mutablePos1).set(x, topY, z, w);
		BlockPos.MutableBlockPos bottomPos = this.mutablePos2.setWithOffset(topPos, Direction.DOWN);
		BlockState topState = Blocks.AIR.defaultBlockState();

		for (int sectionIndex = topSectionIndex; sectionIndex >= 0; sectionIndex--) {
			LevelChunkSection section = chunk.getSection(sectionIndex);
			if (section.hasOnlyAir()) {
				topState = Blocks.AIR.defaultBlockState();
				int sectionY = chunk.getSectionYFromSectionIndex(sectionIndex);
				topPos.setY(SectionPos.sectionToBlockCoord(sectionY));
				bottomPos.setY(topPos.getY() - 1);
			} else {
				//noinspection ConstantValue
				for (int y = 15; y >= 0; y--) {
					BlockState bottomState = ((LevelChunkSection4) section).getBlockState(x, y, z, w);
					if (isEdgeOccluded(topState, bottomState)) {
						return topPos.getY();
					}

					topState = bottomState;
					topPos.set(bottomPos);
					bottomPos.move(Direction.DOWN);
				}
			}
		}

		return this.minY;
	}

	@Overwrite
	@Deprecated
	public boolean update(BlockGetter level, int x, int y, int z) {
		throw Err4.arguments3("ChunkSkyLightSources4#update");
	}
	@Override
	public boolean update(BlockGetter level, int x, int y, int z, int w) {
		int upperEdgeY = y + 1;
		int index = index(x, z, w);
		int currentLowestSourceY = this.get(index);
		if (upperEdgeY < currentLowestSourceY) {
			return false;
		}
		BlockPos topPos = ((BlockPos4.MutableBlockPos) this.mutablePos1).set(x, y + 1, z, w);
		BlockState topState = level.getBlockState(topPos);
		BlockPos middlePos = ((BlockPos4.MutableBlockPos) this.mutablePos2).set(x, y, z, w);
		BlockState middleState = level.getBlockState(middlePos);
		if (this.updateEdge(level, index, currentLowestSourceY, topPos, topState, middlePos, middleState)) {
			return true;
		}
		BlockPos bottomPos = ((BlockPos4.MutableBlockPos) this.mutablePos1).set(x, y - 1, z, w);
		BlockState bottomState = level.getBlockState(bottomPos);
		return this.updateEdge(level, index, currentLowestSourceY, middlePos, middleState, bottomPos, bottomState);
	}

	@Overwrite
	@Deprecated
	public int getLowestSourceY(int x, int z) {
		throw Err4.arguments2("ChunkSkyLightSources4#getLowestSourceY");
	}
	@Override
	public int getLowestSourceY(int x, int z, int w) {
		return this.extendSourcesBelowWorld(this.get(index(x, z, w)));
	}

	@Overwrite
	@Deprecated
	private static int index(int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private static int index(int x, int z, int w) {
		return x + (z + w * LevelChunkSection.SECTION_WIDTH) * LevelChunkSection.SECTION_WIDTH;
	}
}
