package com.iluha168.mc4d.world.level.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link LevelData}.
 */
public interface LevelData4 {
	/**
	 * Implemented by {@link LevelData.RespawnData}.
	 */
	interface RespawnData {
		float wRot();
		@ApiStatus.Internal
		void setWRot(float wRot);

		float vRot();
		@ApiStatus.Internal
		void setVRot(float vRot);

		static RespawnData as(LevelData.RespawnData respawnData) {
			return (RespawnData) (Object) respawnData;
		}

		static float wRot(LevelData.RespawnData respawnData) {
			return LevelData4.RespawnData.as(respawnData).wRot();
		}
		static float vRot(LevelData.RespawnData respawnData) {
			return LevelData4.RespawnData.as(respawnData).vRot();
		}

		static LevelData.RespawnData from(GlobalPos globalPos, float yaw, float pitch, float wRot, float vRot) {
			final LevelData.RespawnData respawnData = new LevelData.RespawnData(globalPos, yaw, pitch);
			final RespawnData respawnData4 = RespawnData.as(respawnData);
			respawnData4.setWRot(wRot);
			respawnData4.setVRot(vRot);
			return respawnData;
		}

		static LevelData.RespawnData of(ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch, float wRot, float vRot) {
			return RespawnData.from(
				GlobalPos.of(dimension, pos.immutable()),
				Mth.wrapDegrees(yaw),
				Mth.clamp(pitch, -90.0F, 90.0F),
				Mth.clamp(wRot, -90.0F, 90.0F),
				Mth.wrapDegrees(vRot)
			);
		}
	}
}
