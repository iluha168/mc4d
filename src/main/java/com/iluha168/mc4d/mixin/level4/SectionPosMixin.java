package com.iluha168.mc4d.mixin.level4;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(SectionPos.class)
class SectionPosMixin implements SectionPos4 {
	@WrapMethod(method = "of(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/SectionPos;")
	private static SectionPos of_blockPos(BlockPos pos, Operation<SectionPos> original) {
		SectionPos sectionPos = original.call(pos);
		Vec4i.setW(sectionPos, Vec4i.getW(pos));
		return sectionPos;
	}

	@WrapMethod(method = "of(Lnet/minecraft/world/level/ChunkPos;I)Lnet/minecraft/core/SectionPos;")
	private static SectionPos of_chunkPos(ChunkPos pos, int sectionY, Operation<SectionPos> original) {
		SectionPos sectionPos = original.call(pos, sectionY);
		Vec4i.setW(sectionPos, ChunkPos4.as(pos).w());
		return sectionPos;
	}

	@WrapMethod(method = "of(Lnet/minecraft/core/Position;)Lnet/minecraft/core/SectionPos;")
	private static SectionPos of_position(Position pos, Operation<SectionPos> original) {
		SectionPos sectionPos = original.call(pos);
		if (pos instanceof Vec4 pos4) {
			Vec4i.setW(sectionPos, SectionPos.blockToSectionCoord(pos4.w));
		}
		return sectionPos;
	}

	@WrapMethod(method = "of(J)Lnet/minecraft/core/SectionPos;")
	private static SectionPos of_long(long sectionNode, Operation<SectionPos> original) {
		SectionPos sectionPos = original.call(sectionNode);
		Vec4i.setW(sectionPos, SectionPos4.w(sectionNode));
		return sectionPos;
	}

	@Redirect(method = "offset(JLnet/minecraft/core/Direction;)J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;offset(JIII)J"
	))
	private static long offset(long sectionNode, int stepX, int stepY, int stepZ, @Local(argsOnly = true, name = "offset") Direction offset) {
		final int stepW = Direction4.as(offset).getStepW();
		return SectionPos4.offset(sectionNode, stepX, stepY, stepZ, stepW);
	}

	@Overwrite
	public static long offset(long sectionNode, int stepX, int stepY, int stepZ) {
		throw Err4.arguments3("SectionPos4#offset");
	}

	@Expression("? | ?")
	@ModifyExpressionValue(method = "sectionRelativePos", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static int sectionRelativePos(int original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = SectionPos.sectionRelative(Vec4i.getW(pos));
		return (w << SectionPos4.RELATIVE_W_SHIFT) | original;
	}

	@Override
	public int relativeToBlockW(short relative) {
		return this.minBlockW() + SectionPos4.sectionRelativeW(relative);
	}

	@WrapMethod(method = "relativeToBlockPos")
	BlockPos relativeToBlockPos(short relative, Operation<BlockPos> original) {
		BlockPos pos = original.call(relative);
		Vec4i.setW(pos, this.relativeToBlockW(relative));
		return pos;
	}

	@ModifyConstant(method = "x(J)I", constant = @Constant(ordinal = 0))
	private static int x_left(int original) {
		return Long.SIZE - SectionPos4.X_OFFSET - SectionPos4.PACKED_X_LENGTH;
	}
	@ModifyConstant(method = "x(J)I", constant = @Constant(ordinal = 1))
	private static int x_right(int original) {
		return Long.SIZE - SectionPos4.PACKED_X_LENGTH;
	}

	@ModifyConstant(method = "y(J)I", constant = @Constant(ordinal = 0))
	private static int y_left(int original) {
		return Long.SIZE - SectionPos4.Y_OFFSET - SectionPos4.PACKED_Y_LENGTH;
	}
	@ModifyConstant(method = "y(J)I", constant = @Constant(ordinal = 1))
	private static int y_right(int original) {
		return Long.SIZE - SectionPos4.PACKED_Y_LENGTH;
	}

	@ModifyConstant(method = "z(J)I", constant = @Constant(ordinal = 0))
	private static int z_left(int original) {
		return Long.SIZE - SectionPos4.Z_OFFSET - SectionPos4.PACKED_Z_LENGTH;
	}
	@ModifyConstant(method = "z(J)I", constant = @Constant(ordinal = 1))
	private static int z_right(int original) {
		return Long.SIZE - SectionPos4.PACKED_Z_LENGTH;
	}

	@Override
	public int w() {
		return ((Vec4i) this).getW();
	}

	@Override
	public int minBlockW() {
		return SectionPos.sectionToBlockCoord(this.w());
	}

	@Override
	public int maxBlockW() {
		return SectionPos.sectionToBlockCoord(this.w(), SectionPos.SECTION_MAX_INDEX);
	}

	@Redirect(method = "blockToSection", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	private static long blockToSection(int x, int y, int z, @Local(argsOnly = true, name = "blockNode") long blockNode) {
		return SectionPos4.asLong(x, y, z, SectionPos.blockToSectionCoord(BlockPos4.getW(blockNode)));
	}

	@Overwrite
	public static long getZeroNode(int x, int z) {
		throw Err4.arguments2("SectionPos4#getZeroNode");
	}

	@ModifyConstant(method = "getZeroNode(J)J", constant = @Constant(longValue = -1048576L))
	private static long getZeroNode(long constant) {
		return ~SectionPos4.PACKED_Y_MASK; // Sets Y bits to zero.
	}

	@Redirect(method = "sectionToChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	private static long sectionToChunk(int x, int z, @Local(argsOnly = true, name = "sectionNode") long sectionNode) {
		return ChunkPos4.pack(x, z, SectionPos4.w(sectionNode));
	}

	@WrapMethod(method = "origin")
	BlockPos origin(Operation<BlockPos> original) {
		BlockPos pos = original.call();
		Vec4i.setW(pos, SectionPos.sectionToBlockCoord(this.w()));
		return pos;
	}

	@Redirect(method = "center", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos center(BlockPos origin, int x, int y, int z) {
		return ((BlockPos4) origin).offset(x, y, z, z);
	}

	@WrapMethod(method = "chunk")
	ChunkPos chunk(Operation<ChunkPos> original) {
		ChunkPos pos = original.call();
		ChunkPos4.as(pos).setW(this.w());
		return pos;
	}

	@Redirect(method = "asLong(Lnet/minecraft/core/BlockPos;)J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	private static long asLong(int x, int y, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return SectionPos4.asLong(x, y, z, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	@Overwrite
	public static long asLong(int x, int y, int z) {
		throw Err4.arguments3("SectionPos4#asLong");
	}

	@Redirect(method = "asLong()J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long asLong_this(int x, int y, int z) {
		return SectionPos4.asLong(x, y, z, this.w());
	}

	@Overwrite
	public SectionPos offset(int x, int y, int z) {
		throw Err4.arguments3("SectionPos4#offset");
	}
	@Override
	public SectionPos offset(int x, int y, int z, int w) {
		SectionPos This = (SectionPos) (Object) this;
		return x == 0 && y == 0 && z == 0 && w == 0
			? This
			: SectionPos4.of(This.x() + x, This.y() + y, This.z() + z, this.w() + w);
	}

	@Redirect(method = "blocksInside", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosedStream(IIIIII)Ljava/util/stream/Stream;"
	))
	Stream<BlockPos> blocksInside(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return BlockPos4.betweenClosedStream(
			minX, minY, minZ, this.minBlockW(),
			maxX, maxY, maxZ, this.maxBlockW()
		);
	}

	@Redirect(method = "cube", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;betweenClosedStream(IIIIII)Ljava/util/stream/Stream;"
	))
	private static Stream<SectionPos> cube(
		int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
		@Local(argsOnly = true, name = "center") SectionPos center,
		@Local(argsOnly = true, name = "radius") int radius
	) {
		final int w = ((SectionPos4) center).w();
		return SectionPos4.betweenClosedStream(minX, minY, minZ, w - radius, maxX, maxY, maxZ, w + radius);
	}

	@Redirect(method = "aroundChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;betweenClosedStream(IIIIII)Ljava/util/stream/Stream;"
	))
	private static Stream<SectionPos> aroundChunk(
		int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
		@Local(argsOnly = true, name = "center") ChunkPos center,
		@Local(argsOnly = true, name = "radius") int radius
	) {
		final int w = ChunkPos4.as(center).w();
		return SectionPos4.betweenClosedStream(minX, minY, minZ, w - radius, maxX, maxY, maxZ, w + radius);
	}

	@Overwrite
	public static Stream<SectionPos> betweenClosedStream(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		throw Err4.arguments3("SectionPos4#betweenClosedStream");
	}

	@Redirect(method = "aroundAndAtBlockPos(Lnet/minecraft/core/BlockPos;Lit/unimi/dsi/fastutil/longs/LongConsumer;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;aroundAndAtBlockPos(IIILit/unimi/dsi/fastutil/longs/LongConsumer;)V"
	))
	private static void aroundAndAtBlockPos(
		int blockX, int blockY, int blockZ, LongConsumer sectionConsumer,
		@Local(argsOnly = true, name = "blockPos") BlockPos blockPos
	) {
		SectionPos4.aroundAndAtBlockPos(blockX, blockY, blockZ, Vec4i.getW(blockPos), sectionConsumer);
	}

	@Redirect(method = "aroundAndAtBlockPos(JLit/unimi/dsi/fastutil/longs/LongConsumer;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;aroundAndAtBlockPos(IIILit/unimi/dsi/fastutil/longs/LongConsumer;)V"
	))
	private static void aroundAndAtBlockPos(
		int blockX, int blockY, int blockZ, LongConsumer sectionConsumer,
		@Local(argsOnly = true, name = "blockPos") long blockPos
	) {
		SectionPos4.aroundAndAtBlockPos(blockX, blockY, blockZ, BlockPos4.getW(blockPos), sectionConsumer);
	}

	@Overwrite
	public static void aroundAndAtBlockPos(int blockX, int blockY, int blockZ, LongConsumer sectionConsumer) {
		throw Err4.arguments3("SectionPos4#aroundAndAtBlockPos");
	}
}
