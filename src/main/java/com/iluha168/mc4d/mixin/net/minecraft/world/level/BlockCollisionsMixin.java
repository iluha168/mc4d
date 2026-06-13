package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Cursor4D;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.CollisionGetter4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.IAABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisions.class)
public class BlockCollisionsMixin {
	@Shadow
	@Final
	private Cursor3D cursor;

	@Shadow
	private @Nullable BlockGetter cachedBlockGetter;

	@Shadow
	private long cachedBlockGetterPos;

	@Shadow
	@Final
	private CollisionGetter collisionGetter;

	@Redirect(method = "<init>(Lnet/minecraft/world/level/CollisionGetter;Lnet/minecraft/world/phys/shapes/CollisionContext;Lnet/minecraft/world/phys/AABB;ZLjava/util/function/BiFunction;)V", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/core/Cursor3D;"
	))
	Cursor3D useCursor4D(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @Local(argsOnly = true, name = "box") AABB box) {
		IAABB4 box4 = (IAABB4) box;
		return new Cursor4D(
			minX, minY, minZ, Mth.floor(box4.minW() - AABB4.EPSILON) - 1,
			maxX, maxY, maxZ, Mth.floor(box4.maxW() + AABB4.EPSILON) + 1
		);
	}

	@ModifyConstant(method = "computeNext", constant = @Constant(intValue = 3))
	int cursorFaceTypeCheck(int constant, @Share("w") LocalIntRef w) {
		w.set(((Cursor4D) this.cursor).nextW());
		return Cursor4D.TYPE_CORNER4;
	}

	@Overwrite
	@Deprecated
	private @Nullable BlockGetter getChunk(int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private @Nullable BlockGetter getChunk(int x, int z, int w) {
		int chunkX = SectionPos.blockToSectionCoord(x);
		int chunkZ = SectionPos.blockToSectionCoord(z);
		int chunkW = SectionPos.blockToSectionCoord(w);
		long chunkPos = ChunkPos4.pack(chunkX, chunkZ, chunkW);
		if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == chunkPos) {
			return this.cachedBlockGetter;
		} else {
			BlockGetter result = ((CollisionGetter4) this.collisionGetter).getChunkForCollisions(chunkX, chunkZ, chunkW);
			this.cachedBlockGetter = result;
			this.cachedBlockGetterPos = chunkPos;
			return result;
		}
	}

	@Definition(id = "getChunk", method = "Lnet/minecraft/world/level/BlockCollisions;getChunk(II)Lnet/minecraft/world/level/BlockGetter;")
	@Expression("this.getChunk(?, ?)")
	@Redirect(method = "computeNext", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockGetter computeNext_getChunk(BlockCollisions<?> This, int x, int z, @Share("w") LocalIntRef w) {
		return this.getChunk(x, z, w.get());
	}

	@Redirect(method = "computeNext", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos computeNext_set(BlockPos.MutableBlockPos pos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) pos).set(x, y, z, w.get());
	}

	@Redirect(method = "computeNext", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;intersects(DDDDDD)Z"
	))
	boolean intersects(
		AABB instance,
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Share("w") LocalIntRef w
	) {
		AABB4 box = (AABB4) instance;
		return box.intersects(
			minX, minY, minZ, w.get(),
			maxX, maxY, maxZ, w.get() + 1
		);
	}
}
