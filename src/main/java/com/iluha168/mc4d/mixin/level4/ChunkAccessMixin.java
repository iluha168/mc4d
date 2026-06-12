package com.iluha168.mc4d.mixin.level4;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.level.levelgen.Heightmap4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.SharedConstants;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Map;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements ChunkAccess4 {
	// TODO everything else

	@Shadow
	@Final
	protected Map<Heightmap.Types, Heightmap> heightmaps;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Overwrite
	@Deprecated
	public int getHeight(Heightmap.Types type, int x, int z) {
		throw Err4.arguments2("ChunkAccess4#getHeight");
	}
	@Override
	public int getHeight(Heightmap.Types type, int x, int z, int w) {
		Heightmap heightmap = this.heightmaps.get(type);
		if (heightmap == null) {
			//noinspection ConstantValue
			if (SharedConstants.IS_RUNNING_IN_IDE && ((ChunkAccess) (Object) this) instanceof LevelChunk) {
				LOGGER.error("Unprimed heightmap: {} {} {} {}", type, x, z, w);
			}

			Heightmap.primeHeightmaps((ChunkAccess) (Object) this, EnumSet.of(type));
			heightmap = this.heightmaps.get(type);
		}

		return ((Heightmap4) heightmap).getFirstAvailable(
			SectionPos.sectionRelative(x),
			SectionPos.sectionRelative(z),
			SectionPos.sectionRelative(w)
		) - 1;
	}

	// TODO everything else

	@Definition(id = "quartMinZ", local = @Local(type = int.class, name = "quartMinZ"))
	@Expression("quartMinZ = @(?)")
	@Inject(method = "fillBiomesFromNoise", at = @At("MIXINEXTRAS:EXPRESSION"))
	void fillBiomesFromNoise_quartMinW(
		BiomeResolver biomeResolver, Climate.Sampler sampler, CallbackInfo ci,
		@Local(name = "pos") ChunkPos pos,
		@Share("quartMinW") LocalIntRef quartMinW
	) {
		quartMinW.set(QuartPos.fromBlock(ChunkPos4.as(pos).getMinBlockW()));
	}
	@Redirect(method = "fillBiomesFromNoise", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;fillBiomesFromNoise(Lnet/minecraft/world/level/biome/BiomeResolver;Lnet/minecraft/world/level/biome/Climate$Sampler;III)V"
	))
	void fillBiomesFromNoise(
		LevelChunkSection section, BiomeResolver biomeResolver, Climate.Sampler sampler,
		int quartMinX, int quartMinY, int quartMinZ, @Share("quartMinW") LocalIntRef quartMinW
	) {
		if (!(section instanceof LevelChunkSection4 section4 && biomeResolver instanceof BiomeResolver4 biomeResolver4))
			throw Err4.container3();
		section4.fillBiomesFromNoise(biomeResolver4, sampler, quartMinX, quartMinY, quartMinZ, quartMinW.get());
	}

	// TODO everything else
}
