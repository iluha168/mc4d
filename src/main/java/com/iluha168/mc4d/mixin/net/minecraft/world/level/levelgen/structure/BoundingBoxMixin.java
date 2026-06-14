package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.structure;

import com.google.common.base.MoreObjects;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.stream.IntStream;

@Mixin(BoundingBox.class)
abstract
class BoundingBoxMixin implements BoundingBox4 {
	@Shadow
	private int maxX;

	@Shadow
	private int minX;

	@Shadow
	private int maxZ;

	@Shadow
	private int minZ;

	@Shadow
	private int minY;

	@Shadow
	private int maxY;

	@Shadow
	public abstract int maxZ();

	@Definition(id = "CODEC", field = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Codec<BoundingBox> CODEC(Codec<BoundingBox> original) {
		return Codec.INT_STREAM
			.comapFlatMap(
				input -> Util.fixedSize(input, 8).map(ints -> BoundingBox4.from(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5], ints[6], ints[7])),
				bb -> IntStream.of(bb.minX(), bb.minY(), bb.minZ(), ((BoundingBox4) bb).minW(), bb.maxX(), bb.maxY(), bb.maxZ(), ((BoundingBox4) bb).maxW())
			)
			.stable();
	}

	@ModifyExpressionValue(method = "lambda$static$3", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos STREAM_CODEC_min(BlockPos original, @Local(argsOnly = true, name = "box") BoundingBox box) {
		Vec4i.setW(original, ((BoundingBox4) box).minW());
		return original;
	}
	@ModifyExpressionValue(method = "lambda$static$4", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos STREAM_CODEC_max(BlockPos original, @Local(argsOnly = true, name = "box") BoundingBox box) {
		Vec4i.setW(original, ((BoundingBox4) box).maxW());
		return original;
	}
	@ModifyExpressionValue(method = "lambda$static$5", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private static BoundingBox STREAM_CODEC_new(
		BoundingBox original,
		@Local(argsOnly = true, name = "min") BlockPos min,
		@Local(argsOnly = true, name = "max") BlockPos max
	) {
		BoundingBox4 box4 = (BoundingBox4) original;
		box4.setMinW(Vec4i.getW(min));
		box4.setMaxW(Vec4i.getW(max));
		return original;
	}

	@Unique	private int minW;
	@Unique private int maxW;
	@Unique	private boolean minWNotSet;
	@Unique	private boolean maxWNotSet;

	@Inject(method = "<init>(Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
	void init_blockPos(BlockPos content, CallbackInfo ci) {
		final int w = Vec4i.getW(content);
		this.setMinW(w);
		this.setMaxW(w);
	}
	@Inject(method = "<init>(IIIIII)V", at = @At("TAIL"))
	void init(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfo ci) {
		this.minWNotSet = true;
		this.maxWNotSet = true;
	}

	@ModifyExpressionValue(method = "fromCorners", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private static BoundingBox fromCorners(BoundingBox original, @Local(argsOnly = true, name = "pos0") Vec3i pos0, @Local(argsOnly = true, name = "pos1") Vec3i pos1) {
		BoundingBox4 box4 = (BoundingBox4) original;
		final int pos0w = Vec4i.getW(pos0);
		final int pos1w = Vec4i.getW(pos1);
		box4.setMinW(Math.min(pos0w, pos1w));
		box4.setMaxW(Math.max(pos0w, pos1w));
		return original;
	}

	@ModifyExpressionValue(method = "infinite", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private static BoundingBox infinite(BoundingBox original) {
		BoundingBox4 box4 = (BoundingBox4) original;
		box4.setMinW(Integer.MIN_VALUE);
		box4.setMaxW(Integer.MAX_VALUE);
		return original;
	}

	@Overwrite
	@Deprecated
	public static BoundingBox orientBox(int footX, int footY, int footZ, int offX, int offY, int offZ, int width, int height, int depth, Direction direction) {
		throw Err4.arguments3("BoundingBox4#orientBox");
	}

	@ModifyExpressionValue(method = "intersectingChunks", at = @At(
		value = "NEW",
		target = "(II)Lnet/minecraft/world/level/ChunkPos;",
		ordinal = 0
	))
	ChunkPos intersectingChunks_min(ChunkPos original) {
		int minChunkW = SectionPos.blockToSectionCoord(this.minW());
		ChunkPos4.as(original).setW(minChunkW);
		return original;
	}
	@ModifyExpressionValue(method = "intersectingChunks", at = @At(
		value = "NEW",
		target = "(II)Lnet/minecraft/world/level/ChunkPos;",
		ordinal = 1
	))
	ChunkPos intersectingChunks_max(ChunkPos original) {
		int maxChunkW = SectionPos.blockToSectionCoord(this.maxW());
		ChunkPos4.as(original).setW(maxChunkW);
		return original;
	}

	@WrapMethod(method = "intersects(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)Z")
	boolean intersects(BoundingBox other, Operation<Boolean> original) {
		return original.call(other)
			&& this.maxW() >= ((BoundingBox4) other).minW()
			&& this.minW() <= ((BoundingBox4) other).maxW();
	}

	@Overwrite
	@Deprecated
	public boolean intersects(int minX, int minZ, int maxX, int maxZ) {
		throw Err4.arguments2("BoundingBox4#intersects");
	}
	@Override
	public boolean intersects(int minX, int minZ, int minW, int maxX, int maxZ, int maxW) {
		return this.maxX >= minX && this.minX <= maxX && this.maxZ >= minZ && this.minZ <= maxZ && this.maxW() >= minW && this.minW() <= maxW;
	}

	@ModifyExpressionValue(method = "encapsulatingBoxes", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private static BoundingBox encapsulatingBoxes(BoundingBox original, @Local(name = "first") BoundingBox first) {
		BoundingBox4 result4 = (BoundingBox4) original;
		BoundingBox4 first4 = (BoundingBox4) first;
		result4.setMinW(first4.minW());
		result4.setMaxW(first4.maxW());
		return original;
	}

	@Inject(method = "encapsulate(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", at = @At("HEAD"))
	void encapsulate(BoundingBox other, CallbackInfoReturnable<BoundingBox> cir) {
		BoundingBox4 other4 = (BoundingBox4) other;
		this.setMinW(Math.min(this.minW(), other4.minW()));
		this.setMaxW(Math.max(this.maxW(), other4.maxW()));
	}

	@ModifyExpressionValue(method = "encapsulating", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	private static BoundingBox encapsulating(BoundingBox original, @Local(argsOnly = true, name = "a") BoundingBox a, @Local(argsOnly = true, name = "b") BoundingBox b) {
		BoundingBox4 bb4 = (BoundingBox4) original;
		BoundingBox4 a4 = (BoundingBox4) a;
		BoundingBox4 b4 = (BoundingBox4) b;
		bb4.setMinW(Math.min(a4.minW(), b4.minW()));
		bb4.setMaxW(Math.max(a4.maxW(), b4.maxW()));
		return original;
	}

	@Inject(method = "encapsulate(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", at = @At("HEAD"))
	void encapsulate(BlockPos pos, CallbackInfoReturnable<BoundingBox> cir) {
		final int w = Vec4i.getW(pos);
		this.setMinW(Math.min(this.minW(), w));
		this.setMaxW(Math.max(this.maxW(), w));
	}

	@Overwrite
	@Deprecated
	public BoundingBox move(int dx, int dy, int dz) {
		throw Err4.arguments3("BoundingBox4#move");
	}
	@Override
	public BoundingBox move(int dx, int dy, int dz, int dw) {
		this.minX += dx;
		this.minY += dy;
		this.minZ += dz;
		this.setMinW(this.minW() + dw);
		this.maxX += dx;
		this.maxY += dy;
		this.maxZ += dz;
		this.setMaxW(this.maxW() + dw);
		return (BoundingBox) (Object) this;
	}

	@Redirect(method = "move(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;move(III)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	BoundingBox move(BoundingBox instance, int dx, int dy, int dz, @Local(argsOnly = true, name = "amount") Vec3i amount) {
		return ((BoundingBox4) instance).move(dx, dy, dz, Vec4i.getW(amount));
	}

	@Overwrite
	@Deprecated
	public BoundingBox moved(int dx, int dy, int dz) {
		throw Err4.arguments3("BoundingBox4#moved");
	}
	@Override
	public BoundingBox moved(int dx, int dy, int dz, int dw) {
		return BoundingBox4.from(
			this.minX + dx, this.minY + dy, this.minZ + dz, this.minW() + dw,
			this.maxX + dx, this.maxY + dy, this.maxZ + dz, this.maxW() + dw
		);
	}

	@Redirect(method = "inflatedBy(I)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;inflatedBy(III)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	BoundingBox inflatedBy(BoundingBox instance, int inflateX, int inflateY, int inflateZ) {
		return ((BoundingBox4) instance).inflatedBy(inflateX, inflateY, inflateZ, inflateZ);
	}

	@Overwrite
	@Deprecated
	public BoundingBox inflatedBy(int inflateX, int inflateY, int inflateZ) {
		throw Err4.arguments3("BoundingBox4#inflatedBy");
	}
	@Override
	public BoundingBox inflatedBy(int inflateX, int inflateY, int inflateZ, int inflateW) {
		return BoundingBox4.from(
			this.minX - inflateX, this.minY - inflateY, this.minZ - inflateZ, this.minW() - inflateW,
			this.maxX + inflateX, this.maxY + inflateY, this.maxZ + inflateZ, this.maxW() + inflateW
		);
	}

	@Redirect(method = "isInside(Lnet/minecraft/core/Vec3i;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;isInside(III)Z"
	))
	boolean isInside(BoundingBox instance, int x, int y, int z) {
		return ((BoundingBox4) instance).isInside(x, y, z, z);
	}

	@Overwrite
	@Deprecated
	public boolean isInside(int x, int y, int z) {
		throw Err4.arguments3("BoundingBox4#isInside");
	}
	@Override
	public boolean isInside(int x, int y, int z, int w) {
		return x >= this.minX && x <= this.maxX
			&& z >= this.minZ && z <= this.maxZ
			&& y >= this.minY && y <= this.maxY
			&& w >= this.minW() && w <= this.maxW();
	}

	@ModifyExpressionValue(method = "getLength", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i getLength(Vec3i original) {
		Vec4i.setW(original, this.maxW() - this.minW());
		return original;
	}

	public int getWSpan() {
		return this.maxW() - this.minW() + 1;
	}

	@ModifyExpressionValue(method = "getCenter", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getCenter(BlockPos original) {
		Vec4i.setW(original, this.minW() + (this.maxW() - this.minW + 1) / 2);
		return original;
	}

	@Redirect(method = "forAllCorners", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos forAllCorners(BlockPos.MutableBlockPos instance, int x, int y, int z) {
		return ((BlockPos4.MutableBlockPos) instance).set(x, y, z, this.maxW());
	}
	@Inject(method = "forAllCorners", at = @At("TAIL"))
	void forAllCorners(Consumer<BlockPos> consumer, CallbackInfo ci, @Local(name = "pos") BlockPos.MutableBlockPos pos) {
		BlockPos4.MutableBlockPos pos4 = (BlockPos4.MutableBlockPos) pos;
		consumer.accept(pos4.set(this.maxX, this.maxY, this.maxZ, this.minW()));
		consumer.accept(pos4.set(this.minX, this.maxY, this.maxZ, this.minW));
		consumer.accept(pos4.set(this.maxX, this.minY, this.maxZ, this.minW));
		consumer.accept(pos4.set(this.minX, this.minY, this.maxZ, this.minW));
		consumer.accept(pos4.set(this.maxX, this.maxY, this.minZ, this.minW));
		consumer.accept(pos4.set(this.minX, this.maxY, this.minZ, this.minW));
		consumer.accept(pos4.set(this.maxX, this.minY, this.minZ, this.minW));
		consumer.accept(pos4.set(this.minX, this.minY, this.minZ, this.minW));
	}

	@Definition(id = "add", method = "Lcom/google/common/base/MoreObjects$ToStringHelper;add(Ljava/lang/String;I)Lcom/google/common/base/MoreObjects$ToStringHelper;")
	@Definition(id = "minZ", field = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;minZ:I")
	@Expression("?.add(?, this.minZ)")
	@ModifyExpressionValue(method = "toString", at = @At("MIXINEXTRAS:EXPRESSION"))
	MoreObjects.ToStringHelper toString_minW(MoreObjects.ToStringHelper original) {
		return original.add("minW", this.minWNotSet ? "missingno" : this.minW);
	}
	@Definition(id = "add", method = "Lcom/google/common/base/MoreObjects$ToStringHelper;add(Ljava/lang/String;I)Lcom/google/common/base/MoreObjects$ToStringHelper;")
	@Definition(id = "maxZ", field = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;maxZ:I")
	@Expression("?.add(?, this.maxZ)")
	@ModifyExpressionValue(method = "toString", at = @At("MIXINEXTRAS:EXPRESSION"))
	MoreObjects.ToStringHelper toString_maxW(MoreObjects.ToStringHelper original) {
		return original.add("maxW", this.maxWNotSet ? "missingno" : this.maxW);
	}

	@Expression("? == ?")
	@ModifyExpressionValue(method = "equals", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 2))
	boolean equals(boolean original, @Local(name = "that") BoundingBox that) {
		BoundingBox4 that4 = (BoundingBox4) that;
		return original
			&& this.minW() == that4.minW()
			&& this.maxW() == that4.maxW();
	}

	@ModifyArg(method = "hashCode", at = @At(
		value = "INVOKE",
		target = "Ljava/util/Objects;hash([Ljava/lang/Object;)I"
	))
	Object[] hashCode(Object[] values) {
		return ArrayUtils.addAll(values, this.minW(), this.maxW());
	}

	@Override
	public int minW() {
		if (this.minWNotSet) throw Err4.field4missing("minW");
		return this.minW;
	}
	@Override
	public int maxW() {
		if (this.maxWNotSet) throw Err4.field4missing("maxW");
		return this.maxW;
	}

	@Override
	public void setMinW(int minW) {
		this.minW = minW;
		this.minWNotSet = false;
	}
	@Override
	public void setMaxW(int maxW) {
		this.maxW = maxW;
		this.maxWNotSet = false;
	}
}
