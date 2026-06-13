package com.iluha168.mc4d.mixin.net.minecraft.world.level.lighting;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.world.level.chunk.DataLayer4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerLightSectionStorage.class)
class LayerLightSectionStorageMixin {
	@Redirect(method = "getStoredLevel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/DataLayer;get(III)I"
	))
	int getStoredLevel(DataLayer instance, int x, int y, int z, @Local(argsOnly = true, name = "blockNode") long blockNode) {
		return ((DataLayer4) instance).get(x, y, z, SectionPos.sectionRelative(BlockPos4.getW(blockNode)));
	}

	@Redirect(method = "setStoredLevel", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/DataLayer;set(IIII)V"
	))
	void setStoredLevel(DataLayer instance, int x, int y, int z, int val, @Local(argsOnly = true, name = "blockNode") long blockNode) {
		((DataLayer4) instance).set(x, y, z, SectionPos.sectionRelative(BlockPos4.getW(blockNode)), val);
	}

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = @(?)")
	@Inject(method = "markSectionAndNeighborsAsAffected", at = @At("MIXINEXTRAS:EXPRESSION"))
	void markSectionAndNeighborsAsAffected_w(long sectionNode, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(SectionPos4.w(sectionNode));
	}
	@Definition(id = "offsetY", local = @Local(type = int.class, name = "offsetY"))
	@Expression("offsetY = @(-1)")
	@Inject(method = "markSectionAndNeighborsAsAffected", at = @At("MIXINEXTRAS:EXPRESSION"))
	void markSectionAndNeighborsAsAffected_offsetW(long sectionNode, CallbackInfo ci, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(-1);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "offsetY", local = @Local(type = int.class, name = "offsetY"))
	@Expression("offsetY = offsetY + @(1)")
	@ModifyExpressionValue(method = "markSectionAndNeighborsAsAffected", at = @At("MIXINEXTRAS:EXPRESSION"))
	int markSectionAndNeighborsAsAffected_incrementOffsetW(int one, @Share("offsetW") LocalIntRef offsetW)  {
		offsetW.set(offsetW.get() + 1);
		if (offsetW.get() <= 1) return 0;
		offsetW.set(-1);
		return 1;
	}
	@Redirect(method = "markSectionAndNeighborsAsAffected", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	long markSectionAndNeighborsAsAffected_asLong(int x, int y, int z, @Share("w") LocalIntRef w, @Share("offsetW") LocalIntRef offsetW) {
		return SectionPos4.asLong(x, y, z, w.get() + offsetW.get());
	}

	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = @(-1)")
	@Inject(method = "updateSectionStatus", at = @At("MIXINEXTRAS:EXPRESSION"))
	void updateSectionStatus_offsetW(long sectionNode, boolean sectionEmpty, CallbackInfo ci, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(-1);
	}
	// This does apply properly, IDE is lying. hold on, what?
	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ = offsetZ + @(1)")
	@ModifyExpressionValue(method = "updateSectionStatus", at = @At("MIXINEXTRAS:EXPRESSION"))
	int updateSectionStatus_incrementOffsetW(int one, @Share("offsetW") LocalIntRef offsetW) {
		offsetW.set(offsetW.get() + 1);
		if (offsetW.get() <= 1) return 0;
		offsetW.set(-1);
		return 1;
	}
	@Definition(id = "offsetZ", local = @Local(type = int.class, name = "offsetZ"))
	@Expression("offsetZ != 0")
	@ModifyExpressionValue(method = "updateSectionStatus", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean updateSectionStatus_noZeroOffsetW(boolean offsetZ, @Share("offsetW") LocalIntRef offsetW) {
		return offsetZ || offsetW.get() != 0;
	}
	@Redirect(method = "updateSectionStatus", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;offset(JIII)J"
	))
	long updateSectionStatus_offset(long sectionNode, int stepX, int stepY, int stepZ, @Share("offsetW") LocalIntRef offsetW) {
		return SectionPos4.offset(sectionNode, stepX, stepY, stepZ, offsetW.get());
	}

	@Mixin(LayerLightSectionStorage.SectionState.class)
	static class SectionStateMixin {
		@Unique private static final int MAX_NEIGHBORS = 3*3*3*3 - 1;
		@Unique private static final byte NEIGHBOR_COUNT_MASK = 0b0111_1111;
		@Unique private static final byte HAS_DATA_BIT = (byte) 0b1000_0000;

		@ModifyConstant(method = "hasData(BZ)B", constant = @Constant(intValue = 32))
		private static int hasData$0(int constant) {
			return HAS_DATA_BIT;
		}
		@ModifyConstant(method = "hasData(BZ)B", constant = @Constant(intValue = ~32))
		private static int hasData$1(int constant) {
			return ~HAS_DATA_BIT;
		}

		@ModifyConstant(method = "neighborCount(BI)B", constant = @Constant(intValue = 3*3*3 - 1))
		private static int neighborCount$0(int constant) {
			return MAX_NEIGHBORS;
		}
		@ModifyConstant(method = "neighborCount(BI)B", constant = @Constant(intValue = ~31))
		private static int neighborCount$1(int constant) {
			return ~NEIGHBOR_COUNT_MASK;
		}
		@ModifyConstant(method = "neighborCount(BI)B", constant = @Constant(intValue = 31))
		private static int neighborCount$2(int constant) {
			return NEIGHBOR_COUNT_MASK;
		}
		@Definition(id = "IllegalArgumentException", type = IllegalArgumentException.class)
		@Expression("new IllegalArgumentException(?)")
		@ModifyArg(method = "neighborCount(BI)B", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static String neighborCount_message(String message) {
			return message.replace(Integer.toString(3*3*3 - 1), Integer.toString(MAX_NEIGHBORS));
		}

		@ModifyConstant(method = "hasData(B)Z", constant = @Constant(intValue = 32))
		private static int hasData$2(int constant) {
			return HAS_DATA_BIT;
		}

		@ModifyConstant(method = "neighborCount(B)I", constant = @Constant(intValue = 31))
		private static int neighborCount$3(int constant) {
			return NEIGHBOR_COUNT_MASK;
		}
	}
}
