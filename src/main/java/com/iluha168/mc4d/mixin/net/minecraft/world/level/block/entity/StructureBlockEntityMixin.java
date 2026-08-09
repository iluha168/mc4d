package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.block.entity.BoundingBoxRenderable4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.stream.Stream;

@Mixin(StructureBlockEntity.class)
abstract class StructureBlockEntityMixin extends BlockEntityMixin {
	@Shadow @Final private static BlockPos DEFAULT_POS;
	@Shadow private BlockPos structurePos;
	@Shadow private Vec3i structureSize;

	@Shadow public abstract String getStructureName();

	@Shadow
	@Final
	public static int MAX_OFFSET_PER_AXIS;

	@Shadow
	@Final
	public static int MAX_SIZE_PER_AXIS;

	@Definition(id = "DEFAULT_POS", field = "Lnet/minecraft/world/level/block/entity/StructureBlockEntity;DEFAULT_POS:Lnet/minecraft/core/BlockPos;")
	@Expression("DEFAULT_POS = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos DEFAULT_POS(BlockPos original) {
		Vec4i.setW(original, 0);
		return original;
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	void saveAdditional(ValueOutput output, CallbackInfo ci) {
		output.putInt("posW", Vec4i.getW(this.structurePos));
		output.putInt("sizeW", Vec4i.getW(this.structureSize));
	}

	@ModifyExpressionValue(method = "loadAdditional", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos loadAdditional_structurePos(BlockPos original, @Local(argsOnly = true, name = "input") ValueInput input) {
		Vec4i.setW(original, Mth.clamp(input.getIntOr("posW", Vec4i.getW(DEFAULT_POS)), -MAX_OFFSET_PER_AXIS, MAX_OFFSET_PER_AXIS));
		return original;
	}
	@ModifyExpressionValue(method = "loadAdditional", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i loadAdditional_structureSize(Vec3i original, @Local(argsOnly = true, name = "input") ValueInput input) {
		Vec4i.setW(original, Mth.clamp(input.getIntOr("sizeW", 0), 0, MAX_SIZE_PER_AXIS));
		return original;
	}

	@Definition(id = "BlockPos", type = BlockPos.class)
	@Definition(id = "getMinY", method = "Lnet/minecraft/world/level/Level;getMinY()I")
	@Expression("new BlockPos(?, ?.getMinY(), ?)")
	@ModifyExpressionValue(method = "detectSize", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockPos detectSize_corner1(BlockPos original, @Local(name = "pos") BlockPos pos, @Local(name = "radius") int radius) {
		Vec4i.setW(original, Vec4i.getW(pos) - radius);
		return original;
	}
	@Definition(id = "BlockPos", type = BlockPos.class)
	@Definition(id = "getMaxY", method = "Lnet/minecraft/world/level/Level;getMaxY()I")
	@Expression("new BlockPos(?, ?.getMaxY(), ?)")
	@ModifyExpressionValue(method = "detectSize", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockPos detectSize_corner2(BlockPos original, @Local(name = "pos") BlockPos pos, @Local(name = "radius") int radius) {
		Vec4i.setW(original, Vec4i.getW(pos) + radius);
		return original;
	}
	@Definition(id = "deltaX", local = @Local(type = int.class, name = "deltaX"))
	@Expression("deltaX > 1")
	@ModifyExpressionValue(method = "lambda$detectSize$0", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean detectSize_deltaW(boolean original, @Local(argsOnly = true, name = "bb") BoundingBox bb, @Share("deltaW") LocalIntRef deltaW) {
		final BoundingBox4 bb4 = (BoundingBox4) bb;
		deltaW.set(bb4.maxW() - bb4.minW());
		return original && deltaW.get() > 1;
	}
	@ModifyExpressionValue(method = "lambda$detectSize$0", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos detectSize_structurePos(BlockPos original, @Local(argsOnly = true, name = "pos") BlockPos pos, @Local(argsOnly = true, name = "bb") BoundingBox bb) {
		Vec4i.setW(original, ((BoundingBox4) bb).minW() - Vec4i.getW(pos) + 1);
		return original;
	}
	@ModifyExpressionValue(method = "lambda$detectSize$0", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i detectSize_structureSize(Vec3i original, @Share("deltaW") LocalIntRef deltaW) {
		Vec4i.setW(original, deltaW.get() - 1);
		return original;
	}

	@Overwrite // Scanning a 160x384x160x160 box block-by-block lagged way too much.
	private Stream<BlockPos> getRelatedCorners(BlockPos corner1, BlockPos corner2) {
		@SuppressWarnings("DataFlowIssue") final ChunkSource4 chunkSource = (ChunkSource4) this.level.getChunkSource();
		return ChunkPos
			.rangeClosed(ChunkPos.containing(corner1), ChunkPos.containing(corner2))
			.map(chunkPos -> chunkSource.getChunkNow(chunkPos.x(), chunkPos.z(), ChunkPos4.as(chunkPos).w()))
			.filter(Objects::nonNull)
			.flatMap(chunk -> chunk.getBlockEntities().values().stream())
			.filter(blockEntity -> blockEntity instanceof StructureBlockEntity input
				&& input.getMode() == StructureMode.CORNER
				&& Objects.equals(this.getStructureName(), input.getStructureName()))
			.map(BlockEntity::getBlockPos)
			.filter(BoundingBox.fromCorners(corner1, corner2)::isInside);
	}

	@Redirect(method = "getRenderableBox", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/entity/BoundingBoxRenderable$RenderableBox;fromCorners(IIIIII)Lnet/minecraft/world/level/block/entity/BoundingBoxRenderable$RenderableBox;"
	))
	BoundingBoxRenderable.RenderableBox getRenderableBox(
		int x1, int y1, int z1, int x2, int y2, int z2,
		@Local(name = "pos") BlockPos pos, @Local(name = "size") Vec3i size
	) {
		// TODO Mirror and Rotation
		final int w1 = Vec4i.getW(pos);
		return BoundingBoxRenderable4.RenderableBox.fromCorners(
			x1, y1, z1, w1,
			x2, y2, z2, w1 + Vec4i.getW(size)
		);
	}
}
