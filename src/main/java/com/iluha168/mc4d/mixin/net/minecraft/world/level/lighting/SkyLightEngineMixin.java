package com.iluha168.mc4d.mixin.net.minecraft.world.level.lighting;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.iluha168.mc4d.world.level.chunk.DataLayer4;
import com.iluha168.mc4d.world.level.lighting.ChunkSkyLightSources4;
import com.iluha168.mc4d.world.level.lighting.LightEngine4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.SkyLightEngine;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(SkyLightEngine.class)
class SkyLightEngineMixin {
	@Shadow
	@Final
	private ChunkSkyLightSources emptyChunkSources;

	@Shadow
	private static boolean isSourceLevel(int value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	private static long ADD_SKY_SOURCE_ENTRY;

	@Shadow
	@Final
	private static long REMOVE_TOP_SKY_SOURCE_ENTRY;

	@Shadow
	@Final
	private static long REMOVE_SKY_SOURCE_ENTRY;

	@Unique
	private SkyLightSectionStorage storage() {
		return (SkyLightSectionStorage) ((LightEngine<?, ?>) (Object) this).storage;
	}

	@Overwrite
	@Deprecated
	private int getLowestSourceY(int x, int z, int defaultValue) {
		throw Err4.arguments2(null);
	}
	@Unique
	private int getLowestSourceY(int x, int z, int w, int defaultValue) {
		ChunkSkyLightSources sources = this.getChunkSources(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), SectionPos.blockToSectionCoord(w));
		return sources == null ? defaultValue : ((ChunkSkyLightSources4) sources).getLowestSourceY(SectionPos.sectionRelative(x), SectionPos.sectionRelative(z), SectionPos.sectionRelative(w));
	}

	@Overwrite
	@Deprecated
	private @Nullable ChunkSkyLightSources getChunkSources(int chunkX, int chunkZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private @Nullable ChunkSkyLightSources getChunkSources(int chunkX, int chunkZ, int chunkW) {
		LightChunk chunk = ((ChunkSource4) ((LightEngine<?, ?>) (Object) this).chunkSource).getChunkForLighting(chunkX, chunkZ, chunkW);
		return chunk != null ? chunk.getSkyLightSources() : null;
	}

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(?)")
	@Inject(method = "checkNode", at = @At("MIXINEXTRAS:EXPRESSION"))
	void checkNode_w(long blockNode, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(BlockPos4.getW(blockNode));
	}
	@Definition(id = "getLowestSourceY", method = "Lnet/minecraft/world/level/lighting/SkyLightEngine;getLowestSourceY(III)I")
	@Expression("this.getLowestSourceY(?, ?, ?)")
	@Redirect(method = "checkNode", at = @At("MIXINEXTRAS:EXPRESSION"))
	int checkNode_getLowestSourceY(SkyLightEngine This, int x, int z, int defaultValue, @Share("w") LocalIntRef w) {
		return this.getLowestSourceY(x, z, w.get(), defaultValue);
	}
	@Definition(id = "updateSourcesInColumn", method = "Lnet/minecraft/world/level/lighting/SkyLightEngine;updateSourcesInColumn(III)V")
	@Expression("this.updateSourcesInColumn(?, ?, ?)")
	@Redirect(method = "checkNode", at = @At("MIXINEXTRAS:EXPRESSION"))
	void checkNode_updateSourcesInColumn(SkyLightEngine This, int x, int z, int lowestSourceY, @Share("w") LocalIntRef w) {
		this.updateSourcesInColumn(x, z, w.get(), lowestSourceY);
	}

	@Overwrite
	@Deprecated
	private void updateSourcesInColumn(int x, int z, int lowestSourceY) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void updateSourcesInColumn(int x, int z, int w, int lowestSourceY) {
		int worldBottomY = SectionPos.sectionToBlockCoord(this.storage().getBottomSectionY());
		this.removeSourcesBelow(x, z, w, lowestSourceY, worldBottomY);
		this.addSourcesAbove(x, z, w, lowestSourceY, worldBottomY);
	}

	@Overwrite
	@Deprecated
	private void removeSourcesBelow(int x, int z, int lowestSourceY, int worldBottomY) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void removeSourcesBelow(int x, int z, int w, int lowestSourceY, int worldBottomY) {
		if (lowestSourceY > worldBottomY) {
			int sectionX = SectionPos.blockToSectionCoord(x);
			int sectionZ = SectionPos.blockToSectionCoord(z);
			int sectionW = SectionPos.blockToSectionCoord(w);
			int startY = lowestSourceY - 1;

			for (int sectionY = SectionPos.blockToSectionCoord(startY); this.storage().hasLightDataAtOrBelow(sectionY); sectionY--) {
				if (this.storage().storingLightForSection(SectionPos4.asLong(sectionX, sectionY, sectionZ, sectionW))) {
					int sectionBottomY = SectionPos.sectionToBlockCoord(sectionY);
					int sectionTopY = sectionBottomY + SectionPos.SECTION_MAX_INDEX;

					for (int y = Math.min(sectionTopY, startY); y >= sectionBottomY; y--) {
						long blockNode = BlockPos4.asLong(x, y, z, w);
						if (!isSourceLevel(this.storage().getStoredLevel(blockNode))) {
							return;
						}

						this.storage().setStoredLevel(blockNode, 0);
						((LightEngine<?, ?>) (Object) this).enqueueDecrease(blockNode, y == lowestSourceY - 1 ? REMOVE_TOP_SKY_SOURCE_ENTRY : REMOVE_SKY_SOURCE_ENTRY);
					}
				}
			}
		}
	}

	@Overwrite
	@Deprecated
	private void addSourcesAbove(int x, int z, int lowestSourceY, int worldBottomY) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void addSourcesAbove(int x, int z, int w, int lowestSourceY, int worldBottomY) {
		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		int sectionW = SectionPos.blockToSectionCoord(w);
		int neighborLowestSourceY = Math.max(Math.max(
			Math.max(
				this.getLowestSourceY(x - 1, z, w, Integer.MIN_VALUE),
				this.getLowestSourceY(x + 1, z, w, Integer.MIN_VALUE)
			),
			Math.max(
				this.getLowestSourceY(x, z - 1, w, Integer.MIN_VALUE),
				this.getLowestSourceY(x, z + 1, w, Integer.MIN_VALUE)
			)),
			Math.max(
				this.getLowestSourceY(x, z, w - 1, Integer.MIN_VALUE),
				this.getLowestSourceY(x, z, w + 1, Integer.MIN_VALUE)
			)
		);
		int startY = Math.max(lowestSourceY, worldBottomY);

		for (long sectionNode = SectionPos4.asLong(sectionX, SectionPos.blockToSectionCoord(startY), sectionZ, sectionW);
		     !this.storage().isAboveData(sectionNode);
		     sectionNode = SectionPos.offset(sectionNode, Direction.UP)
		) {
			if (this.storage().storingLightForSection(sectionNode)) {
				int sectionBottomY = SectionPos.sectionToBlockCoord(SectionPos.y(sectionNode));
				int sectionTopY = sectionBottomY + SectionPos.SECTION_MAX_INDEX;

				for (int y = Math.max(sectionBottomY, startY); y <= sectionTopY; y++) {
					long blockNode = BlockPos4.asLong(x, y, z, w);
					if (isSourceLevel(this.storage().getStoredLevel(blockNode))) {
						return;
					}

					this.storage().setStoredLevel(blockNode, 15);
					if (y < neighborLowestSourceY || y == lowestSourceY) {
						((LightEngine<?, ?>) (Object) this).enqueueIncrease(blockNode, ADD_SKY_SOURCE_ENTRY);
					}
				}
			}
		}
	}

	@Definition(id = "localZ", local = @Local(type = int.class, name = "localZ"))
	@Expression("localZ != 0")
	@ModifyExpressionValue(method = "countEmptySectionsBelowIfAtBorder", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean countEmptySectionsBelowIfAtBorder(boolean original, @Share("w") LocalIntRef w, @Local(argsOnly = true, name = "blockNode") long blockNode) {
		w.set(BlockPos4.getW(blockNode));
		final int localW = SectionPos.sectionRelative(w.get());
		return original && localW != 0 && localW != SectionPos.SECTION_MAX_INDEX;
	}
	@Redirect(method = "countEmptySectionsBelowIfAtBorder", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long countEmptySectionsBelowIfAtBorder_asLong(int x, int y, int z, @Share("w") LocalIntRef w) {
		final int sectionW = SectionPos.blockToSectionCoord(w.get());
		return SectionPos4.asLong(x, y, z, sectionW);
	}

	@Redirect(method = "propagateFromEmptySections", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/SkyLightEngine;crossedSectionEdge(Lnet/minecraft/core/Direction;II)Z"
	))
	boolean propagateFromEmptySections_crossedSectionEdge(
		Direction propagationDirection, int x, int z,
		@Local(argsOnly = true, name = "toNode") long toNode,
		@Share("w") LocalIntRef w
	) {
		w.set(BlockPos4.getW(toNode));
		return crossedSectionEdge(propagationDirection, x, z, SectionPos.sectionRelative(w.get()));
	}
	@Definition(id = "sectionZ", local = @Local(type = int.class, name = "sectionZ"))
	@Expression("sectionZ = @(?)")
	@Inject(method = "propagateFromEmptySections", at = @At("MIXINEXTRAS:EXPRESSION"))
	void propagateFromEmptySections_sectionW(
		long toNode, Direction propagationDirection, int toLevel, boolean increase, int emptySectionsBelow, CallbackInfo ci,
		@Share("sectionW") LocalIntRef sectionW,
		@Share("w") LocalIntRef w
	) {
		sectionW.set(SectionPos.blockToSectionCoord(w.get()));
	}
	@Redirect(method = "propagateFromEmptySections", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long propagateFromEmptySections_sectionAsLong(int x, int y, int z, @Share("sectionW") LocalIntRef sectionW) {
		return SectionPos4.asLong(x, y, z, sectionW.get());
	}
	@Redirect(method = "propagateFromEmptySections", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;asLong(III)J"
	))
	long propagateFromEmptySections_blockAsLong(int x, int y, int z, @Share("sectionW") LocalIntRef sectionW) {
		return BlockPos4.asLong(x, y, z, sectionW.get());
	}

	@Overwrite
	@Deprecated
	private static boolean crossedSectionEdge(Direction propagationDirection, int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private static boolean crossedSectionEdge(Direction propagationDirection, int x, int z, int w) {
		if (propagationDirection == Direction4.KATA) return w == SectionPos.SECTION_MAX_INDEX;
		if (propagationDirection == Direction4.ANA ) return w == 0;
		return switch (propagationDirection) {
			case NORTH -> z == SectionPos.SECTION_MAX_INDEX;
			case SOUTH -> z == 0;
			case WEST -> x == SectionPos.SECTION_MAX_INDEX;
			case EAST -> x == 0;
			default -> throw new IllegalArgumentException();
		};
	}

	@Definition(id = "getChunkSources", method = "Lnet/minecraft/world/level/lighting/SkyLightEngine;getChunkSources(II)Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;")
	@Expression("this.getChunkSources(?, ?)")
	@Redirect(method = "setLightEnabled", at = @At("MIXINEXTRAS:EXPRESSION"))
	ChunkSkyLightSources setLightEnabled_getChunkSources(SkyLightEngine This, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return this.getChunkSources(chunkX, chunkZ, ChunkPos4.as(pos).w());
	}
	@Redirect(method = "setLightEnabled", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;getZeroNode(II)J"
	))
	long setLightEnabled_getZeroNode(int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return SectionPos4.getZeroNode(x, z, ChunkPos4.as(pos).w());
	}
	@Redirect(method = "setLightEnabled", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long setLightEnabled_asLong(int x, int y, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return SectionPos4.asLong(x, y, z, ChunkPos4.as(pos).w());
	}

	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;getZeroNode(II)J"
	))
	long propagateLightSources_getZeroNode(int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return SectionPos4.getZeroNode(x, z, ChunkPos4.as(pos).w());
	}
	@Definition(id = "getChunkSources", method = "Lnet/minecraft/world/level/lighting/SkyLightEngine;getChunkSources(II)Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;")
	@Expression("this.getChunkSources(?, ?)")
	@Redirect(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	ChunkSkyLightSources propagateLightSources_getChunkSources(SkyLightEngine This, int chunkX, int chunkZ, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return this.getChunkSources(chunkX, chunkZ, ChunkPos4.as(pos).w());
	}
	@Definition(id = "eastSources", local = @Local(type = ChunkSkyLightSources.class, name = "eastSources"))
	@Expression("eastSources = @(?)")
	@Inject(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	void propagateLightSources_kataAnaSources(
		ChunkPos pos, CallbackInfo ci,
		@Share("kataSources") LocalRef<ChunkSkyLightSources> kataSources,
		@Share("kataSources") LocalRef<ChunkSkyLightSources> anaSources
	) {
		final int w = ChunkPos4.as(pos).w();
		kataSources.set(Objects.requireNonNullElse(this.getChunkSources(pos.x(), pos.z(), w - 1), this.emptyChunkSources));
		anaSources .set(Objects.requireNonNullElse(this.getChunkSources(pos.x(), pos.z(), w + 1), this.emptyChunkSources));
	}
	@Definition(id = "sectionMinZ", local = @Local(type = int.class, name = "sectionMinZ"))
	@Expression("sectionMinZ = @(?)")
	@Inject(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	void propagateLightSources_sectionMinW(ChunkPos pos, CallbackInfo ci, @Share("sectionMinW") LocalIntRef sectionMinW) {
		sectionMinW.set(SectionPos.sectionToBlockCoord(ChunkPos4.as(pos).w()));
	}
	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long propagateLightSources_asLong(int x, int y, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return SectionPos4.asLong(x, y, z, ChunkPos4.as(pos).w());
	}
	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x = @(0)")
	@Inject(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	void propagateLightSources_w(ChunkPos pos, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(0);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x = x + @(1)")
	@ModifyExpressionValue(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	int propagateLightSources_incrementW(int original, @Share("w") LocalIntRef w) {
		w.set(w.get() + 1);
		if (w.get() < LevelChunkSection.SECTION_WIDTH) return 0;
		w.set(0);
		return 1;
	}
	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/ChunkSkyLightSources;getLowestSourceY(II)I"
	))
	int propagateLightSources_getLowestSourceY(ChunkSkyLightSources sources, int x, int z, @Share("w") LocalIntRef w){
		return ((ChunkSkyLightSources4) sources).getLowestSourceY(x, z, w.get());
	}
	@Definition(id = "neighborLowestSourceY", local = @Local(type = int.class, name = "neighborLowestSourceY"))
	@Expression("neighborLowestSourceY = @(?)")
	@ModifyExpressionValue(method = "propagateLightSources", at = @At("MIXINEXTRAS:EXPRESSION"))
	int propagateLightSources(
		int neighborXZLowestSourceY,
		@Share("kataSources") LocalRef<ChunkSkyLightSources4> kataSources,
		@Share("kataSources") LocalRef<ChunkSkyLightSources4> anaSources,
		@Local(name = "x") int x,
		@Local(name = "z") int z,
		@Share("w") LocalIntRef w,
		@Local(name = "sources") ChunkSkyLightSources sources,
		@Share("kataLowestSourceY") LocalIntRef kataLowestSourceY,
		@Share("anaLowestSourceY") LocalIntRef anaLowestSourceY
	) {
		kataLowestSourceY.set(w.get() == 0
			? kataSources.get().getLowestSourceY(x, z, LevelChunkSection.SECTION_WIDTH - 1)
			: ((ChunkSkyLightSources4) sources).getLowestSourceY(x, z, w.get() - 1)
		);
		anaLowestSourceY.set(w.get() == LevelChunkSection.SECTION_WIDTH - 1
			? anaSources.get().getLowestSourceY(x, z, 0)
			: ((ChunkSkyLightSources4) sources).getLowestSourceY(x, z, w.get() + 1)
		);
		return Math.max(neighborXZLowestSourceY, Math.max(kataLowestSourceY.get(), anaLowestSourceY.get()));
	}
	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/DataLayer;set(IIII)V"
	))
	void propagateLightSources_dataLayerSet(DataLayer dataLayer, int x, int y, int z, int val, @Share("w") LocalIntRef w) {
		((DataLayer4) dataLayer).set(x, y, z, w.get(), val);
	}
	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;asLong(III)J"
	))
	long propagateLightSources_asLong(int x, int y, int z, @Share("sectionMinW") LocalIntRef sectionMinW, @Share("w") LocalIntRef w) {
		return BlockPos4.asLong(x, y, z, sectionMinW.get() + w.get());
	}
	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/lighting/LightEngine$QueueEntry;increaseSkySourceInDirections(ZZZZZ)J"
	))
	long propagateLightSources_increaseSkySourceInDirections(
		boolean down, boolean north, boolean south, boolean west, boolean east,
		@Local(name = "y") int y,
		@Share("kataLowestSourceY") LocalIntRef kataLowestSourceY,
		@Share("anaLowestSourceY") LocalIntRef anaLowestSourceY
	) {
		return LightEngine4.QueueEntry.increaseSkySourceInDirections(
			down,
			north, south,
			west, east,
			y < kataLowestSourceY.get(), y < anaLowestSourceY.get()
		);
	}
}
