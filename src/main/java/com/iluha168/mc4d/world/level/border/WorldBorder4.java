package com.iluha168.mc4d.world.level.border;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;

/**
 * Implemented by {@link net.minecraft.world.level.border.WorldBorder}.
 */
public interface WorldBorder4 {
	// Added 4 at the end due to override clashing
	boolean isWithinBounds4(double minX, double minZ, double minW, double maxX, double maxZ, double maxW);
	boolean isWithinBounds4(double x, double z, double w);
	boolean isWithinBounds4(double x, double z, double w, double margin);

	BlockPos clampToBounds(double x, double y, double z, double w);

	Vec4 clampVec4ToBound(double x, double y, double z, double w);

	double getDistanceToBorder(double x, double z, double w);

	default double getMinW() {
		return this.getMinW(0.0F);
	}
	double getMinW(float deltaPartialTick);

	default double getMaxW() {
		return this.getMaxW(0.0F);
	}
	double getMaxW(float deltaPartialTick);

	double getCenterW();

	void setCenter(double x, double z, double w);

	interface BorderExtent {
		double getMinW(final float deltaPartialTick);
		double getMaxW(final float deltaPartialTick);
	}

	interface Settings {
		static WorldBorder4.Settings as(WorldBorder.Settings settings) {
			return (WorldBorder4.Settings) (Object) settings;
		}

		static WorldBorder.Settings of(
			double centerX,
			double centerZ,
			double centerW,
			double damagePerBlock,
			double safeZone,
			int warningBlocks,
			int warningTime,
			double size,
			long lerpTime,
			double lerpTarget
		) {
			WorldBorder.Settings settings = new WorldBorder.Settings(centerX, centerZ, damagePerBlock, safeZone, warningBlocks, warningTime, size, lerpTime, lerpTarget);
			WorldBorder4.Settings.as(settings).setCenterW(centerW);
			return settings;
		}

		double centerW();
		void setCenterW(double centerW);
	}
}
