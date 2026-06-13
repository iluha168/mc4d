package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.DataLayer4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DataLayer.class)
abstract
class DataLayerMixin implements DataLayer4 {
	@Shadow
	protected abstract int get(int index);

	@Shadow
	protected abstract void set(int index, int val);

	@ModifyConstant(method = "<init>([B)V", constant = @Constant(intValue = DataLayer4.LAYER_SIZE))
	int SIZE(int constant) {
		return DataLayer4.SIZE;
	}
	@Definition(id = "IllegalArgumentException", type = IllegalArgumentException.class)
	@Definition(id = "data", local = @Local(type = byte[].class, name = "data", argsOnly = true))
	@Expression("new IllegalArgumentException(@(?) + data.length)")
	@ModifyExpressionValue(method = "<init>([B)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	String SIZE(String message) {
		return message.replace(Integer.toString(DataLayer4.LAYER_SIZE), Integer.toString(DataLayer4.SIZE));
	}

	@Overwrite
	@Deprecated
	public int get(int x, int y, int z) {
		throw Err4.arguments3("DataLayer4#get");
	}
	@Override
	public int get(int x, int y, int z, int w) {
		return this.get(getIndex(x, y, z, w));
	}

	@Overwrite
	@Deprecated
	public void set(int x, int y, int z, int val) {
		throw Err4.arguments3("DataLayer4#set");
	}
	@Override
	public void set(int x, int y, int z, int w, int val) {
		this.set(getIndex(x, y, z, w), val);
	}

	@Overwrite
	@Deprecated
	private static int getIndex(int x, int y, int z) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static int getIndex(int x, int y, int z, int w) {
		return w << (3 * DataLayer4.NIBBLE_SIZE) | y << (2 * DataLayer4.NIBBLE_SIZE) | z << DataLayer4.NIBBLE_SIZE | x;
	}

	@ModifyConstant(method = "getData", constant = @Constant(intValue = DataLayer4.LAYER_SIZE))
	int getData(int constant) {
		return DataLayer4.SIZE;
	}

	@ModifyConstant(method = "toString", constant = @Constant(intValue = DataLayer4.NIBBLES_PER_BYTE * DataLayer4.LAYER_SIZE))
	int toString(int constant) {
		return DataLayer4.NIBBLES_PER_BYTE * DataLayer4.SIZE;
	}

	@ModifyConstant(method = "layerToString", constant = @Constant(intValue = LevelChunkSection.SECTION_WIDTH * LevelChunkSection.SECTION_WIDTH))
	int layerToString(int constant) {
		return DataLayer4.NIBBLES_PER_BYTE * DataLayer4.LAYER_SIZE;
	}
}
