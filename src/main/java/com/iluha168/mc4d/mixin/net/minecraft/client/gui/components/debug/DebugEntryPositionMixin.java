package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components.debug;

import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.debug.DebugEntryPosition;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;

@Mixin(DebugEntryPosition.class)
public class DebugEntryPositionMixin {
	@Definition(id = "displayer", local = @Local(type = DebugScreenDisplayer.class, name = "displayer", argsOnly = true))
	@Definition(id = "addToGroup", method = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addToGroup(Lnet/minecraft/resources/Identifier;Ljava/util/Collection;)V")
	@Definition(id = "GROUP", field = "Lnet/minecraft/client/gui/components/debug/DebugEntryPosition;GROUP:Lnet/minecraft/resources/Identifier;")
	@Definition(id = "of", method = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;")
	@Expression("displayer.addToGroup(GROUP, of(@(?), ?, ?, ?, ?))")
	@Redirect(method = "display", at = @At("MIXINEXTRAS:EXPRESSION"))
	String display(Locale locale, String format, Object[] args, @Local(name = "entity") Entity entity) {
		assert args.length == 3;
		return String.format(
			locale, "XYZW: %.3f / %.5f / %.3f / %.3f",
			(double) args[0], (double) args[1], (double) args[2], ((Entity4) entity).getW()
		);
	}
}
