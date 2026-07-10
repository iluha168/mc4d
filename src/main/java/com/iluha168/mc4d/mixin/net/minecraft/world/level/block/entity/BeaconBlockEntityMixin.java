package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
class BeaconBlockEntityMixin {
	@Shadow
	@Final
	private static int MAX_LEVELS;

	@Inject(method = "tick", at = @At("HEAD"))
	private static void tick_w(Level level, BlockPos pos, BlockState selfState, BeaconBlockEntity entity, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(Vec4i.getW(pos));
	}
	@ModifyExpressionValue(method = "tick", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos tick_checkPos(BlockPos original, @Share("w") LocalIntRef w) {
		Vec4i.setW(original, w.get());
		return original;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
	))
	private static int tick_getHeight(Level instance, Heightmap.Types type, int x, int z, @Share("w") LocalIntRef w) {
		return ((LevelReader4) instance).getHeight(type, x, z, w.get());
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I"
	))
	private static int tick_updateBase(Level level, int x, int y, int z, @Share("w") LocalIntRef w) {
		return updateBase(level, x, y, z, w.get());
	}
	@Redirect(method = "tick", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB tick_AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Share("w") LocalIntRef w) {
		return new AABB4(
			minX, minY, minZ, w.get(),
			maxX, maxY, maxZ, w.get()
		);
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB tick_inflate(AABB instance, double xAdd, double yAdd, double zAdd) {
		return ((AABB4) instance).inflate(xAdd, yAdd, zAdd, zAdd);
	}

	@Overwrite
	@Deprecated
	private static int updateBase(Level level, int x, int y, int z) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static int updateBase(Level level, int x, int y, int z, int w) {
		int levels = 0;

		countLevels: for (int step = 1; step <= MAX_LEVELS; levels = step++) {
			final int ly = y - step;
			if (ly < level.getMinY()) {
				break;
			}

			for (int lx = x - step; lx <= x + step; lx++)
				for (int lz = z - step; lz <= z + step; lz++)
					for (int lw = w - step; lw <= w + step; lw++)
						if (!level.getBlockState(BlockPos4.from(lx, ly, lz, lw)).is(BlockTags.BEACON_BASE_BLOCKS))
							break countLevels;
		}

		return levels;
	}

	@Redirect(method = "applyEffects", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB applyEffects_AABB(BlockPos pos) {
		return new AABB4(pos);
	}
	@Redirect(method = "applyEffects", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB applyEffects_expandTowards(AABB instance, double xa, double ya, double za) {
		return ((AABB4) instance).expandTowards(xa, ya, za, za);
	}
}
