package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.server.level.ColumnPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ColumnPos.class)
class ColumnPosMixin implements ColumnPos4 {
	@Unique	private int w;
	@Unique	private boolean wNotSet;

	@Override
	public int w() {
		if (this.wNotSet) {
			throw Err4.field4missing("w");
		}
		return w;
	}
	@Override
	public void setW(int w) {
		this.w = w;
		this.wNotSet = false;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(int x, int z, CallbackInfo ci) {
		this.wNotSet = true;
	}

	@ModifyExpressionValue(method = "toChunkPos", at = @At(
		value = "NEW",
		target = "(II)Lnet/minecraft/world/level/ChunkPos;"
	))
	ChunkPos toChunkPos(ChunkPos original) {
		ChunkPos4.as(original).setW(SectionPos.blockToSectionCoord(this.w()));
		return original;
	}

	@Redirect(method = "toLong", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ColumnPos;asLong(II)J"
	))
	long toLong(int x, int z) {
		return ColumnPos4.asLong(x, z, this.w());
	}

	@Overwrite
	@Deprecated
	public static long asLong(int x, int z) {
		throw Err4.arguments2("ColumnPos4#asLong");
	}

	@ModifyConstant(method = "getX", constant = @Constant(longValue = 4294967295L))
	private static long getX_mask(long constant) {
		return ColumnPos4.COORD_MASK;
	}

	@ModifyConstant(method = "getZ", constant = @Constant(intValue = 32))
	private static int getZ_bits(int constant) {
		return ColumnPos4.COORD_BITS;
	}
	@ModifyConstant(method = "getZ", constant = @Constant(longValue = 4294967295L))
	private static long getZ_mask(long constant) {
		return ColumnPos4.COORD_MASK;
	}

	@Definition(id = "z", field = "Lnet/minecraft/server/level/ColumnPos;z:I")
	@Expression("return ? + this.z + @(?)")
	@ModifyExpressionValue(method = "toString", at = @At("MIXINEXTRAS:EXPRESSION"))
	public String toString(String rightBracket) {
		return ", " + (this.wNotSet ? "missingno" : this.w) + rightBracket;
	}

	@Redirect(method = "hashCode", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;hash(II)I"
	))
	int hashCode(int x, int z) {
		return ChunkPos4.hash(x, z, this.w());
	}

	@WrapMethod(method = "equals")
	boolean equals(Object o, Operation<Boolean> original) {
		return original.call(o) && this.w() == ((ColumnPos4) o).w();
	}
}
