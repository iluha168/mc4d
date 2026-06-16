package com.iluha168.mc4d.net.neoforged.neoforge.model.data;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.neoforged.neoforge.model.data.ModelData;

/**
 * Implemented by {@link net.neoforged.neoforge.model.data.ModelDataManager}.
 */
public interface ModelDataManager4 {
	Long2ObjectFunction<ModelData> snapshotSectionRegion(
		int sectionMinX, int sectionMinY, int sectionMinZ, int sectionMinW,
		int sectionMaxX, int sectionMaxY, int sectionMaxZ, int sectionMaxW
	);
}