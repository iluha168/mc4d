package com.iluha168.mc4d.mixin.net.minecraft.world.level.gameevent;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameEventDispatcher.class)
class GameEventDispatcherMixin {
	@Definition(id = "chunkZ", local = @Local(type = int.class, name = "chunkZ"))
	@Definition(id = "sectionMinZ", local = @Local(type = int.class, name = "sectionMinZ"))
	@Expression("chunkZ = @(sectionMinZ)")
	@Inject(method = "post", at = @At("MIXINEXTRAS:EXPRESSION"))
	void post_chunkW(
		Holder<GameEvent> gameEvent, Vec3 position, GameEvent.Context context, CallbackInfo ci,
		@Local(name = "center") BlockPos center,
		@Local(name = "radius") int radius,
		@Share("chunkW") LocalIntRef chunkW,
		@Share("sectionMinW") LocalIntRef sectionMinW,
		@Share("sectionMaxW") LocalIntRef sectionMaxW
	) {
		sectionMinW.set(SectionPos.blockToSectionCoord(Vec4i.getW(center) - radius));
		sectionMaxW.set(SectionPos.blockToSectionCoord(Vec4i.getW(center) + radius));
		chunkW.set(sectionMinW.get());
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "chunkZ", local = @Local(type = int.class, name = "chunkZ"))
	@Expression("chunkZ = chunkZ + @(1)")
	@ModifyExpressionValue(method = "post", at = @At("MIXINEXTRAS:EXPRESSION"))
	int post_incrementW(
		int one,
		@Share("chunkW") LocalIntRef chunkW,
		@Share("sectionMinW") LocalIntRef sectionMinW,
		@Share("sectionMaxW") LocalIntRef sectionMaxW
	) {
		chunkW.set(chunkW.get() + 1);
		if (chunkW.get() <= sectionMaxW.get()) return 0;
		chunkW.set(sectionMinW.get());
		return 1;
	}
	@Redirect(method = "post", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerChunkCache;getChunkNow(II)Lnet/minecraft/world/level/chunk/LevelChunk;"
	))
	private LevelChunk post_getChunkNow(
		ServerChunkCache source, int x, int z,
		@Share("chunkW") LocalIntRef chunkW
	) {
		return ((ChunkSource4) source).getChunkNow(x, z, chunkW.get());
	}
}
