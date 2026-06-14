package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.structure.templatesystem;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.ArrayHelpers;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.levelgen.structure.templatesystem.StructureTemplate4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.BitSetDiscreteVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.Optional;

@Mixin(StructureTemplate.class)
abstract
class StructureTemplateMixin implements StructureTemplate4 {
	@Shadow
	private Vec3i size;

	@Shadow
	public abstract Vec3i getSize();

	// TODO fillFromWorld

	@Definition(id = "comparator", local = @Local(type = Comparator.class, name = "comparator"))
	@Expression("comparator = @(?)")
	@ModifyExpressionValue(method = "buildInfoList", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Comparator<StructureTemplate.StructureBlockInfo> buildInfoList(Comparator<StructureTemplate.StructureBlockInfo> original) {
		return original.thenComparingInt(o -> Vec4i.getW(o.pos()));
	}

	// TODO fillEntityList

	@Expression("? >= 1")
	@ModifyExpressionValue(method = "placeInWorld", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	boolean placeInWorld(boolean original) {
		return original && Vec4i.getW(this.size) >= 1;
	}
	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Expression("minZ = @(2147483647)")
	@Inject(method = "placeInWorld", at = @At("MIXINEXTRAS:EXPRESSION"))
	void placeInWorld_minW(
		ServerLevelAccessor level, BlockPos position, BlockPos referencePos, StructurePlaceSettings settings, RandomSource random, int updateMode, CallbackInfoReturnable<Boolean> cir,
		@Share("minW") LocalIntRef minW
	) {
		minW.set(Integer.MAX_VALUE);
	}
	@Definition(id = "maxZ", local = @Local(type = int.class, name = "maxZ"))
	@Expression("maxZ = @(-2147483648)")
	@Inject(method = "placeInWorld", at = @At("MIXINEXTRAS:EXPRESSION"))
	void placeInWorld_maxW(
		ServerLevelAccessor level, BlockPos position, BlockPos referencePos, StructurePlaceSettings settings, RandomSource random, int updateMode, CallbackInfoReturnable<Boolean> cir,
		@Share("maxW") LocalIntRef maxW
	) {
		maxW.set(Integer.MIN_VALUE);
	}
	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Definition(id = "min", method = "Ljava/lang/Math;min(II)I")
	@Expression("minZ = @(min(?, ?))")
	@Inject(method = "placeInWorld", at = @At("MIXINEXTRAS:EXPRESSION"))
	void placeInWorld_minWUpdate(
		ServerLevelAccessor level, BlockPos position, BlockPos referencePos, StructurePlaceSettings settings, RandomSource random, int updateMode, CallbackInfoReturnable<Boolean> cir,
		@Share("minW") LocalIntRef minW,
		@Local(name = "blockPos") BlockPos blockPos
	) {
		minW.set(Math.min(minW.get(), Vec4i.getW(blockPos)));
	}
	@Definition(id = "maxZ", local = @Local(type = int.class, name = "maxZ"))
	@Definition(id = "max", method = "Ljava/lang/Math;max(II)I")
	@Expression("maxZ = @(max(?, ?))")
	@Inject(method = "placeInWorld", at = @At("MIXINEXTRAS:EXPRESSION"))
	void placeInWorld_maxWUpdate(
		ServerLevelAccessor level, BlockPos position, BlockPos referencePos, StructurePlaceSettings settings, RandomSource random, int updateMode, CallbackInfoReturnable<Boolean> cir,
		@Share("maxW") LocalIntRef maxW,
		@Local(name = "blockPos") BlockPos blockPos
	) {
		maxW.set(Math.max(maxW.get(), Vec4i.getW(blockPos)));
	}
	@Definition(id = "directions", local = @Local(type = Direction[].class, name = "directions"))
	@Expression("directions = @(?)")
	@ModifyExpressionValue(method = "placeInWorld", at = @At("MIXINEXTRAS:EXPRESSION"))
	Direction[] placeInWorld_directions(Direction[] original) {
		return ArrayUtils.addAll(original, Direction4.KATA, Direction4.ANA);
	}
	@Redirect(method = "placeInWorld", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/world/phys/shapes/BitSetDiscreteVoxelShape;"
	))
	BitSetDiscreteVoxelShape placeInWorld_shape(
		int xSize, int ySize, int zSize,
		@Share("shape") LocalRef<DiscreteVoxelShape> shape,
		@Share("maxW") LocalIntRef maxW,
		@Share("minW") LocalIntRef minW
	) {
		shape.set(new BitSetDiscreteVoxelShape4(xSize, ySize, zSize, maxW.get() - minW.get() + 1));
		return null;
	}
	@Redirect(method = "placeInWorld", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;fill(III)V"
	))
	void placeInWorld_fill(
		DiscreteVoxelShape instance, int x, int y, int z,
		@Share("shape") LocalRef<DiscreteVoxelShape4> shape,
		@Local(name = "blockPos") BlockPos blockPos,
		@Share("minW") LocalIntRef minW
	) {
		shape.get().fill(x, y, z, Vec4i.getW(blockPos) - minW.get());
	}
	@Redirect(method = "placeInWorld", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;updateShapeAtEdge(Lnet/minecraft/world/level/LevelAccessor;ILnet/minecraft/world/phys/shapes/DiscreteVoxelShape;III)V"
	))
	void placeInWorld(
		LevelAccessor level, int updateMode, DiscreteVoxelShape shape, int startX, int startY, int startZ,
		@Share("shape") LocalRef<DiscreteVoxelShape> shape4,
		@Share("minW") LocalIntRef minW
	) {
		StructureTemplate4.updateShapeAtEdge(level, updateMode, shape4.get(), startX, startY, startZ, minW.get());
	}

	@Redirect(method = "updateShapeAtEdge(Lnet/minecraft/world/level/LevelAccessor;ILnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lnet/minecraft/core/BlockPos;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;updateShapeAtEdge(Lnet/minecraft/world/level/LevelAccessor;ILnet/minecraft/world/phys/shapes/DiscreteVoxelShape;III)V"
	))
	private static void updateShapeAtEdge_shortcut(LevelAccessor level, int updateMode, DiscreteVoxelShape shape, int startX, int startY, int startZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		StructureTemplate4.updateShapeAtEdge(level, updateMode, shape, startX, startY, startZ, Vec4i.getW(pos));
	}

	@Overwrite
	@Deprecated
	public static void updateShapeAtEdge(LevelAccessor level, @Block.UpdateFlags int updateMode, DiscreteVoxelShape shape, int startX, int startY, int startZ) {
		throw Err4.arguments3("StructureTemplate4#updateShapeAtEdge");
	}

	@Redirect(method = "lambda$addEntitiesToWorld$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	private static void addEntitiesToWorld(Entity instance, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		((Entity4) instance).snapTo(x, y, z, ((Vec4) pos).w, yRot, xRot);
	}

	@ModifyArg(method = "addEntitiesToWorld", index = 1, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;"
	))
	Tag addEntitiesToWorld(Tag tag, @Local(name = "pos") Vec3 pos) {
		((ListTag) tag).add(DoubleTag.valueOf(((Vec4) pos).w));
		return tag;
	}

	@ModifyExpressionValue(method = "getSize(Lnet/minecraft/world/level/block/Rotation;)Lnet/minecraft/core/Vec3i;", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i getSize(Vec3i original) {
		Vec4i.setW(original, Vec4i.getW(this.size));
		return original;
	}

	@WrapMethod(method = "transform(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;")
	private static BlockPos transform(BlockPos pos, Mirror mirror, Rotation rotation, BlockPos pivot, Operation<BlockPos> original) {
		BlockPos result = original.call(pos, mirror, rotation, pivot);
		Vec4i.setW(result, Vec4i.getW(pos));
		return result;
	}

	@Redirect(method = "transform(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 transform(double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		// TODO different W when Mirror and Rotation
		return new Vec4(x, y, z, ((Vec4) pos).w);
	}

	@Redirect(method = "getZeroPositionWithTransform(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;getZeroPositionWithTransform(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;II)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getZeroPositionWithTransform_shortcut(BlockPos zeroPos, Mirror mirror, Rotation rotation, int sizeX, int sizeZ) {
		return StructureTemplate4.getZeroPositionWithTransform(zeroPos, mirror, rotation, sizeX, sizeZ, Vec4i.getW(this.getSize()));
	}

	@Overwrite
	@Deprecated
	public static BlockPos getZeroPositionWithTransform(BlockPos zeroPos, Mirror mirror, Rotation rotation, int sizeX, int sizeZ) {
		throw Err4.arguments2("StructureTemplate4#getZeroPositionWithTransform");
	}

	@Redirect(method = "getBoundingBox(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;offset(III)Lnet/minecraft/core/Vec3i;"
	))
	private static Vec3i getBoundingBox(Vec3i instance, int x, int y, int z) {
		return ((Vec4i) instance).offset(x, y, z, z);
	}

	@ModifyArg(method = "save", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;newIntegerList([I)Lnet/minecraft/nbt/ListTag;",
		ordinal = 0
	))
	int[] save_blockPos(int[] values, @Local(name = "blockInfo") StructureTemplate.StructureBlockInfo blockInfo) {
		return ArrayUtils.addAll(values, Vec4i.getW(blockInfo.pos()));
	}
	@ModifyArg(method = "save", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;newDoubleList([D)Lnet/minecraft/nbt/ListTag;",
		ordinal = 0
	))
	double[] save_entityPos(double[] values, @Local(name = "entityInfo") StructureTemplate.StructureEntityInfo entityInfo) {
		return ArrayHelpers.addAll(values, ((Vec4) entityInfo.pos).w);
	}
	@ModifyArg(method = "save", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;newIntegerList([I)Lnet/minecraft/nbt/ListTag;",
		ordinal = 1
	))
	int[] save_entityBlockPos(int[] values, @Local(name = "entityInfo") StructureTemplate.StructureEntityInfo entityInfo) {
		return ArrayUtils.addAll(values, Vec4i.getW(entityInfo.blockPos));
	}
	@ModifyArg(method = "save", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;newIntegerList([I)Lnet/minecraft/nbt/ListTag;",
		ordinal = 2
	))
	int[] save_size(int[] values) {
		return ArrayUtils.addAll(values, Vec4i.getW(this.size));
	}

	@ModifyExpressionValue(method = "load", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i load_size(Vec3i original, @Local(name = "sizeTag") ListTag sizeTag) {
		// TODO make strict and force W to be present
		sizeTag.getInt(3).ifPresent(w -> Vec4i.setW(original, w));
		return original;
	}

	@ModifyExpressionValue(method = "lambda$load$0", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 load_entityPos(Vec3 original, @Local(name = "posTag") ListTag posTag) {
		Optional<Double> w = posTag.getDouble(3);
		if (w.isPresent()) {
			return Vec4.of(original, w.get());
		}
		return original;
	}
	@ModifyExpressionValue(method = "lambda$load$0", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos load_entityBlockPos(BlockPos original, @Local(name = "blockPosTag") ListTag blockPosTag) {
		// TODO make strict and force W to be present
		blockPosTag.getInt(3).ifPresent(w -> Vec4i.setW(original, w));
		return original;
	}

	@ModifyExpressionValue(method = "lambda$loadPalette$0", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos loadPalette_blockPos(BlockPos original, @Local(name = "posTag") ListTag posTag) {
		// TODO make strict and force W to be present
		posTag.getInt(3).ifPresent(w -> Vec4i.setW(original, w));
		return original;
	}
}
