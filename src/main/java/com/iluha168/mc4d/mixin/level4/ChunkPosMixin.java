package com.iluha168.mc4d.mixin.level4;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Mixin(ChunkPos.class)
abstract
class ChunkPosMixin implements ChunkPos4 {
	@Shadow
	public abstract int getBlockX(int offset);

	@Shadow
	public abstract int getBlockZ(int offset);

	@Shadow	@Final private int x;
	@Shadow @Final private int z;
	@Unique int w;
	@Unique	private boolean wNotSet;

	@Override
	public int w() {
		if (wNotSet) {
			throw Err4.field4missing("w");
		}
		return w;
	}

	@WrapMethod(method = "equals")
	boolean equals(Object o, Operation<Boolean> original) {
		return original.call(o) && this.w() == ((ChunkPos4) o).w();
	}

	@Override
	public void setW(int w) {
		this.w = w;
		this.wNotSet = false;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void constructor(int x, int z, CallbackInfo ci) {
		this.wNotSet = true;
	}

	@Definition(id = "CODEC", field = "Lnet/minecraft/world/level/ChunkPos;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Codec<ChunkPos> CODEC(Codec<ChunkPos> original) {
		return Codec.INT_STREAM
			.comapFlatMap(
				input -> Util
					.fixedSize(input, 3)
					.map(ints -> ChunkPos4.from(ints[0], ints[1], ints[2])),
				pos -> IntStream.of(pos.x(), pos.z(), ChunkPos4.as(pos).w())
			)
			.stable();
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	private static long INVALID_CHUNK_POS(int x, int z) {
		final int outsideWorldSectionIndex = SectionPos.blockToSectionCoord(Level4.MAX_LEVEL_SIZE) + (ChunkPos.REGION_SIZE + 1) * 2;
		return ChunkPos4.pack(outsideWorldSectionIndex, outsideWorldSectionIndex, outsideWorldSectionIndex);
	}

	@Definition(id = "ZERO", field = "Lnet/minecraft/world/level/ChunkPos;ZERO:Lnet/minecraft/world/level/ChunkPos;")
	@Expression("ZERO = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ChunkPos ZERO(ChunkPos ZERO) {
		ChunkPos4.as(ZERO).setW(0);
		return ZERO;
	}

	@WrapMethod(method = "containing")
	private static ChunkPos containing(BlockPos pos, Operation<ChunkPos> original) {
		ChunkPos chunk = original.call(pos);
		ChunkPos4.as(chunk).setW(SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
		return chunk;
	}

	/**
	 * @author iluha168
	 * @reason Completely different behavior from vanilla.
	 */
	@Overwrite
	public static ChunkPos unpack(long key) {
		return ChunkPos4.from(
			ChunkPos.getX(key),
			ChunkPos.getZ(key),
			ChunkPos4.getW(key)
		);
	}

	@Overwrite
	public static ChunkPos minFromRegion(int regionX, int regionZ) {
		throw Err4.arguments2("ChunkPos4#minFromRegion");
	}

	@Overwrite
	public static ChunkPos maxFromRegion(int regionX, int regionZ) {
		throw Err4.arguments2("ChunkPos4#maxFromRegion");
	}

	@Redirect(method = "isValid()Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;isValid(II)Z"
	))
	boolean isValid_this(int x, int z) {
		return ChunkPos4.isValid(x, z, this.w());
	}

	@Overwrite
	public static boolean isValid(int x, int z) {
		throw Err4.arguments2("ChunkPos4#isValid");
	}

	@Redirect(method = "pack()J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	private long pack_this(int x, int z) {
		return ChunkPos4.pack(x, z, this.w());
	}

	@Overwrite
	public static long pack(int x, int z) {
		throw Err4.arguments2("ChunkPos4#pack");
	}

	@Redirect(method = "pack(Lnet/minecraft/core/BlockPos;)J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	private static long pack(int x, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ChunkPos4.pack(x, z, SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
	}

	/**
	 * @author iluha168
	 * @reason TODO lazy
	 */
	@Overwrite
	public static int getX(long pos) {
		return ChunkPos4.getCoordAtOffset(pos, ChunkPos4.X_OFFSET);
	}

	/**
	 * @author iluha168
	 * @reason TODO lazy
	 */
	@Overwrite
	public static int getZ(long pos) {
		return ChunkPos4.getCoordAtOffset(pos, ChunkPos4.Z_OFFSET);
	}

	@Redirect(method = "hashCode", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;hash(II)I"
	))
	int hashCode(int x, int z) {
		return ChunkPos4.hash(x, z, this.w());
	}

	@Overwrite
	public static int hash(int x, int z) {
		throw Err4.arguments2("ChunkPos4#hash");
	}

	@Overwrite
	public BlockPos getBlockAt(int x, int y, int z) {
		throw Err4.arguments3("SectionPos4#getBlockAt");
	}
	@Override
	public BlockPos getBlockAt(int x, int y, int z, int w) {
		return BlockPos4.from(this.getBlockX(x), y, this.getBlockZ(z), this.getBlockW(w));
	}

	@WrapMethod(method = "getMiddleBlockPosition")
	BlockPos getMiddleBlockPosition(int y, Operation<BlockPos> original) {
		BlockPos pos = original.call(y);
		Vec4i.setW(pos, this.getMiddleBlockW());
		return pos;
	}

	@WrapMethod(method = "contains")
	boolean contains(BlockPos pos, Operation<Boolean> original) {
		final int w = Vec4i.getW(pos);
		return original.call(pos) && w >= this.getMinBlockW() && w <= this.getMaxBlockW();
	}

	@Definition(id = "z", field = "Lnet/minecraft/world/level/ChunkPos;z:I")
	@Expression("return ? + this.z + @(?)")
	@ModifyExpressionValue(method = "toString", at = @At("MIXINEXTRAS:EXPRESSION"))
	public String toString(String rightBracket) {
		return ", " + (this.wNotSet ? "missingno" : this.w) + rightBracket;
	}

	@WrapMethod(method = "getWorldPosition")
	BlockPos getWorldPosition(Operation<BlockPos> original) {
		BlockPos pos = original.call();
		Vec4i.setW(pos, this.getMinBlockW());
		return pos;
	}

	@Redirect(method = "getChessboardDistance(Lnet/minecraft/world/level/ChunkPos;)I", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;getChessboardDistance(II)I"
	))
	int getChessboardDistance(ChunkPos This, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ChunkPos4.as(This).getChessboardDistance(x, z, ChunkPos4.as(pos).w());
	}

	@Overwrite
	public int getChessboardDistance(int x, int z) {
		throw Err4.arguments2("SectionPos4#getChessboardDistance");
	}
	@Override
	public int getChessboardDistance(int x, int z, int w) {
		return MathHelpers.chessboardDistance(x, z, w, this.x, this.z, this.w());
	}

	@Redirect(method = "distanceSquared(Lnet/minecraft/world/level/ChunkPos;)I", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;distanceSquared(II)I"
	))
	int distanceSquared(ChunkPos This, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return this.distanceSquared(x, z, ChunkPos4.as(pos).w());
	}

	@Redirect(method = "distanceSquared(J)I", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;distanceSquared(II)I"
	))
	int distanceSquared(ChunkPos This, int x, int z, @Local(argsOnly = true, name = "pos") long pos) {
		return this.distanceSquared(x, z, ChunkPos4.getW(pos));
	}

	@Overwrite
	private int distanceSquared(int x, int z) {
		throw Err4.arguments2("SectionPos4#distanceSquared");
	}
	@Unique
	private int distanceSquared(int x, int z, int w) {
		int deltaX = x - this.x;
		int deltaZ = z - this.z;
		int deltaW = w - this.w();
		return deltaX * deltaX + deltaZ * deltaZ + deltaW * deltaW;
	}

	@ModifyArgs(method = "rangeClosed(Lnet/minecraft/world/level/ChunkPos;I)Ljava/util/stream/Stream;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;rangeClosed(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/ChunkPos;)Ljava/util/stream/Stream;"
	))
	private static void rangeClosed(
		Args args,
		@Local(argsOnly = true, name = "center") ChunkPos center,
		@Local(argsOnly = true, name = "radius") int radius
	) {
		final int centerW = ChunkPos4.as(center).w();
		((ChunkPos4) args.get(0)).setW(centerW - radius);
		((ChunkPos4) args.get(1)).setW(centerW + radius);
	}

	@WrapOperation(method = "rangeClosed(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/ChunkPos;)Ljava/util/stream/Stream;", at = @At(
		value = "INVOKE",
		target = "Ljava/util/stream/StreamSupport;stream(Ljava/util/Spliterator;Z)Ljava/util/stream/Stream;"
	))
	private static Stream<ChunkPos> rangeClosed(
		Spliterator<ChunkPos> spliterator, boolean parallel, Operation<Stream<ChunkPos>> original,
		@Local(argsOnly = true, name = "from") final ChunkPos from,
		@Local(argsOnly = true, name = "to") final ChunkPos to,
		@Local(name = "xDiff") final int xDiff,
		@Local(name = "zDiff") final int zDiff
	) {
		final int fromW = ChunkPos4.as(from).w();
		final int toW = ChunkPos4.as(to).w();
		int wSize = Math.abs(fromW - toW) + 1;
		final int wDiff = fromW < toW ? 1 : -1;
		return original.call(
			new Spliterators.AbstractSpliterator<ChunkPos>(spliterator.estimateSize() * wSize, spliterator.characteristics()) {
				private @Nullable ChunkPos pos;

				@Override
				public boolean tryAdvance(Consumer<? super ChunkPos> action) {
					if (this.pos == null) {
						this.pos = from;
					} else {
						int x = this.pos.x();
						int z = this.pos.z();
						int w = ChunkPos4.as(this.pos).w();

						if (x != to.x()) {
							this.pos = ChunkPos4.from(x + xDiff, z, w);
						} else if (z != to.z()) {
							this.pos = ChunkPos4.from(from.x(), z + zDiff, w);
						} else if(w != ChunkPos4.as(to).w()) {
							this.pos = ChunkPos4.from(from.x(), from.z(), w + wDiff);
						} else {
							return false;
						}
					}
					action.accept(this.pos);
					return true;
				}
			},
			parallel
		);
	}
}
