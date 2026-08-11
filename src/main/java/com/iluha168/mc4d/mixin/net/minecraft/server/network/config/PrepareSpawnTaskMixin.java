package com.iluha168.mc4d.mixin.net.minecraft.server.network.config;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.config.PrepareSpawnTask;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PrepareSpawnTask.class)
class PrepareSpawnTaskMixin {
	@Redirect(method = "start", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	private Vec2 start(float x, float y, @Local(name = "respawnData") LevelData.RespawnData respawnData) {
		final LevelData4.RespawnData respawnData4 = LevelData4.RespawnData.as(respawnData);
		return new RotationVec(x, y, respawnData4.wRot(), respawnData4.vRot());
	}

	@Mixin(targets = "net.minecraft.server.network.config.PrepareSpawnTask$Ready")
	static class ReadyMixin {
		@Shadow @Final private Vec2 spawnAngle;

		@Redirect(method = "spawn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerPlayer;snapTo(Lnet/minecraft/world/phys/Vec3;FF)V"
		))
		private void spawn(ServerPlayer player, Vec3 spawnPosition, float yRot, float xRot) {
			final RotationVec spawnAngle = (RotationVec) this.spawnAngle;
			((Entity4) player).snapTo(spawnPosition, yRot, xRot, spawnAngle.w, spawnAngle.v);
		}
	}
}
