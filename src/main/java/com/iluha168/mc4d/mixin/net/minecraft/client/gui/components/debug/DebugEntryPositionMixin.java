package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components.debug;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.debug.DebugEntryPosition;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;

@SuppressWarnings("MalformedFormatString")
@Mixin(DebugEntryPosition.class)
public class DebugEntryPositionMixin {
	@Definition(id = "displayer", local = @Local(type = DebugScreenDisplayer.class, name = "displayer", argsOnly = true))
	@Definition(id = "addToGroup", method = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addToGroup(Lnet/minecraft/resources/Identifier;Ljava/util/Collection;)V")
	@Definition(id = "GROUP", field = "Lnet/minecraft/client/gui/components/debug/DebugEntryPosition;GROUP:Lnet/minecraft/resources/Identifier;")
	@Definition(id = "of", method = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;")
	@Expression("displayer.addToGroup(GROUP, of(@(?), ?, ?, ?, ?))")
	@Redirect(method = "display", at = @At("MIXINEXTRAS:EXPRESSION"))
	String display_XYZW(Locale l, String format, Object[] args, @Local(name = "entity") Entity entity) {
		assert args.length == 3;
		return String.format(
			l, "XYZW: %.3f / %.5f / %.3f / %.3f",
			args[0], args[1], args[2], ((Entity4) entity).getW()
		);
	}
	@Definition(id = "displayer", local = @Local(type = DebugScreenDisplayer.class, name = "displayer", argsOnly = true))
	@Definition(id = "addToGroup", method = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addToGroup(Lnet/minecraft/resources/Identifier;Ljava/util/Collection;)V")
	@Definition(id = "GROUP", field = "Lnet/minecraft/client/gui/components/debug/DebugEntryPosition;GROUP:Lnet/minecraft/resources/Identifier;")
	@Definition(id = "of", method = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;")
	@Expression("displayer.addToGroup(GROUP, of(?, @(?), ?, ?, ?))")
	@Redirect(method = "display", at = @At("MIXINEXTRAS:EXPRESSION"))
	String display_Block(Locale l, String format, Object[] args, @Local(name = "feetPos") BlockPos feetPos) {
		assert args.length == 3;
		return String.format(
			l, format + " %d",
			args[0], args[1], args[2], Vec4i.getW(feetPos)
		);
	}
	@Definition(id = "displayer", local = @Local(type = DebugScreenDisplayer.class, name = "displayer", argsOnly = true))
	@Definition(id = "addToGroup", method = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addToGroup(Lnet/minecraft/resources/Identifier;Ljava/util/Collection;)V")
	@Definition(id = "GROUP", field = "Lnet/minecraft/client/gui/components/debug/DebugEntryPosition;GROUP:Lnet/minecraft/resources/Identifier;")
	@Definition(id = "of", method = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;")
	@Expression("displayer.addToGroup(GROUP, of(?, ?, @(?), ?, ?))")
	@Redirect(method = "display", at = @At("MIXINEXTRAS:EXPRESSION"))
	String display_Chunk(Locale l, String format, Object[] args, @Local(name = "chunkPos") ChunkPos chunkPos) {
		assert args.length == 7;
		ChunkPos4 chunkPos4 = ChunkPos4.as(chunkPos);
		return String.format(
			l, "Chunk: %d %d %d %d [%d %d %d in r.%d.%d.%d.mca]",
			args[0], args[1], args[2], chunkPos4.w(),
			args[3], args[4], chunkPos4.getRegionLocalW(),
			args[5], args[6], chunkPos4.getRegionW()
		);
	}
}
