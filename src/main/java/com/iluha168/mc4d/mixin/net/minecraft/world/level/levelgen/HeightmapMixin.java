package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.Heightmap4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(Heightmap.class)
abstract
class HeightmapMixin implements Heightmap4 {
	@Shadow
	@Final
	private Predicate<BlockState> isOpaque;

	@Shadow
	@Final
	private ChunkAccess chunk;

	@Shadow
	@Final
	private BitStorage data;

	@Shadow
	protected abstract int getFirstAvailable(int index);

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = LevelChunkSection.SECTION_WIDTH * LevelChunkSection.SECTION_WIDTH))
	private int dataSize3D(int value) {
		return value * LevelChunkSection.SECTION_WIDTH;
	}

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(0)")
	@Inject(method = "primeHeightmaps", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void primeHeightmaps_w(ChunkAccess chunk, Set<Heightmap.Types> types, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(0);
	}
	// This does apply properly, IDE is lying
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "primeHeightmaps", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int primeHeightmaps_incrementW(int one, @Share("w") LocalIntRef w) {
		w.set(w.get() + 1);
		if (w.get() < LevelChunkSection.SECTION_WIDTH) return 0;
		w.set(0);
		return 1;
	}
	@Redirect(method = "primeHeightmaps", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private static BlockPos.MutableBlockPos primeHeightmaps_posW(BlockPos.MutableBlockPos pos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) pos).set(x, y, z, w.get());
	}
	@Redirect(method = "primeHeightmaps", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/Heightmap;setHeight(III)V"
	))
	private static void primeHeightmaps_setHeight(Heightmap heightmap, int x, int z, int height, @Share("w") LocalIntRef w) {
		((Heightmap4) heightmap).setHeight(x, z, w.get(), height);
	}

	@Overwrite
	@Deprecated
	public boolean update(int localX, int localY, int localZ, BlockState state) {
		throw Err4.arguments3("Heightmap4#update");
	}
	@Override
	public boolean update(int localX, int localY, int localZ, int localW, BlockState state) {
		int firstAvailable = this.getFirstAvailable(localX, localZ, localW);
		if (localY <= firstAvailable - 2) {
			return false;
		}
		if (this.isOpaque.test(state)) {
			if (localY >= firstAvailable) {
				this.setHeight(localX, localZ, localW, localY + 1);
				return true;
			}
		} else if (firstAvailable - 1 == localY) {
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

			for (int y = localY - 1; y >= this.chunk.getMinY(); y--) {
				((BlockPos4.MutableBlockPos) pos).set(localX, y, localZ, localW);
				if (this.isOpaque.test(this.chunk.getBlockState(pos))) {
					this.setHeight(localX, localZ, localW, y + 1);
					return true;
				}
			}

			this.setHeight(localX, localZ, localW, this.chunk.getMinY());
			return true;
		}

		return false;
	}

	@Overwrite
	@Deprecated
	public int getFirstAvailable(int x, int z) {
		throw Err4.arguments2("Heightmap4#getFirstAvailable");
	}
	@Override
	public int getFirstAvailable(int x, int z, int w) {
		return this.getFirstAvailable(getIndex(x, z, w));
	}

	@Overwrite
	@Deprecated
	public int getHighestTaken(int x, int z) {
		throw Err4.arguments3("Heightmap4#getHighestTaken");
	}
	@Override
	public int getHighestTaken(int x, int z, int w) {
		return this.getFirstAvailable(getIndex(x, z, w)) - 1;
	}

	@Overwrite
	@Deprecated
	private void setHeight(int x, int z, int height) {
		throw Err4.arguments3("Heightmap4#setHeight");
	}
	@Override
	public void setHeight(int x, int z, int w, int height) {
		this.data.set(getIndex(x, z, w), height - this.chunk.getMinY());
	}

	@Unique
	private static int getIndex(int x, int z, int w) {
		return x + (z + w * LevelChunkSection.SECTION_WIDTH) * LevelChunkSection.SECTION_WIDTH;
	}
}
