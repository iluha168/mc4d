package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components.debug;

import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.debug.DebugEntrySectionPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;

@Mixin(DebugEntrySectionPosition.class)
class DebugEntrySectionPositionMixin {
	@Redirect(method = "display", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
	))
	String display(Locale l, String format, Object[] args, @Local(name = "feetPos") BlockPos feetPos) {
		return String.format(l, format+" %02d", ArrayUtils.addAll(args,
			SectionPos.sectionRelative(Vec4i.getW(feetPos))
		));
	}
}
