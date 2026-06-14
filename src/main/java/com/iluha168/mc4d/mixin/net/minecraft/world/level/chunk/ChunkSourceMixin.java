package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ChunkSource.class)
public abstract class ChunkSourceMixin implements ChunkSource4 {
	@Overwrite
	@Deprecated
	public @Nullable LevelChunk getChunk(int x, int z, boolean loadOrGenerate) {
		throw Err4.arguments2("ChunkSource4#getChunk");
	}

	@Overwrite
	@Deprecated
	public @Nullable LightChunk getChunkForLighting(int x, int z) {
		throw Err4.arguments2("ChunkSource4#getChunkForLighting");
	}
}
