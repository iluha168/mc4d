package com.iluha168.mc4d.client.renderer.block.dispatch;

import com.iluha168.mc4d.client.renderer.chunk.RenderSectionRegion4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.Vec4;
import it.unimi.dsi.fastutil.floats.FloatRBTreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * The 4D counterpart of {@link SingleVariant}.
 * Picks one of several 3D models based on the camera's block-local W position.
 */
public class WRangeVariant implements DynamicBlockStateModel {
	private static final float MAX_LOCAL_W = Math.nextDown(Variant4.W_SCALE);

	// These have the same length: baked "model" map entries are stored each at their own index (the same for all 3 arrays, per entry).
	private final float[] minW;
	private final float[] maxW;
	private final BlockStateModelPart[] parts;

	private final BlockStateModelPart missingPart;
	/**
	 * Sorted distinct W values inside (0;{@link Variant4#W_SCALE}) where the selected part changes.
	 * When the camera crosses these values, the chunk meshes are invalidated.
	 */
	private final float[] wBoundaries;
	@BakedQuad.MaterialFlags
	private final int materialFlags;

	private WRangeVariant(float[] minW, float[] maxW, BlockStateModelPart[] parts, BlockStateModelPart missingPart, boolean hasWGaps) {
		this.minW = minW;
		this.maxW = maxW;
		this.parts = parts;
		this.missingPart = missingPart;
		final FloatRBTreeSet boundaries = new FloatRBTreeSet();
		int materialFlags = hasWGaps ? missingPart.materialFlags() : 0;
		for (int i = 0; i < parts.length; i++) {
			if (minW[i] > 0.0F && minW[i] < Variant4.W_SCALE)
				boundaries.add(minW[i]);
			if (maxW[i] > 0.0F && maxW[i] < Variant4.W_SCALE)
				boundaries.add(maxW[i]);
			materialFlags |= parts[i].materialFlags();
		}
		this.wBoundaries = boundaries.toFloatArray();
		this.materialFlags = materialFlags;
	}

	public static BlockStateModel bake(ModelBaker modelBakery, List<Variant4.WRangeModel> slices, ModelState modelState) {
		final int count = slices.size();
		final float[] minW = new float[count];
		final float[] maxW = new float[count];
		final BlockStateModelPart[] parts = new BlockStateModelPart[count];
		for (int i = 0; i < count; i++) {
			final Variant4.WRangeModel slice = slices.get(i);
			minW[i] = slice.minW();
			maxW[i] = slice.maxW();
			parts[i] = SimpleModelWrapper.bake(modelBakery, slice.model(), modelState);
		}
		return new WRangeVariant(minW, maxW, parts, modelBakery.missingBlockModelPart(), Variant4.findWGaps(slices) != null);
	}

	/**
	 * The camera's block-local W, scaled to [0;{@link Variant4#W_SCALE}).
	 */
	private float localCameraW(@NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
		if (level instanceof RenderSectionRegion4 region) {
			// see CompiledSectionMesh4. This is where the boundaries come from, and then get collected into region -> chunk -> level.
			region.addWBoundaries(this.wBoundaries);
			return Math.clamp((float) (region.cameraW() - Vec4i.getW(pos)) * Variant4.W_SCALE, 0.0F, MAX_LOCAL_W);
		}
		final double cameraW = ((Vec4) Minecraft.getInstance().gameRenderer.getMainCamera().position()).w;
		return (float) (cameraW - Math.floor(cameraW)) * Variant4.W_SCALE; // Fractional part of camera W
	}

	/** First matching row wins. */
	private @NonNull BlockStateModelPart partAt(float localW) {
		for (int i = 0; i < this.parts.length; i++)
			if (localW >= this.minW[i] && localW < this.maxW[i])
				return this.parts[i];
		return this.missingPart;
	}

	@Override
	public Material.@NonNull Baked particleMaterial() {
		return this.partAt(this.localCameraW(BlockAndTintGetter.EMPTY, BlockPos.ZERO)).particleMaterial();
	}
	@Override
	public Material.@NonNull Baked particleMaterial(@NonNull BlockAndTintGetter level, @NonNull BlockPos pos, @Nullable BlockState state) {
		return this.partAt(this.localCameraW(level, pos)).particleMaterial();
	}

	@BakedQuad.MaterialFlags
	@Override
	public int materialFlags() {
		return this.materialFlags;
	}
	@BakedQuad.MaterialFlags
	@Override
	public int materialFlags(@NonNull BlockAndTintGetter level, @NonNull BlockPos pos, @Nullable BlockState state) {
		return this.partAt(this.localCameraW(level, pos)).materialFlags();
	}

	@Override
	public @NonNull Object createGeometryKey(@NonNull BlockAndTintGetter level, @NonNull BlockPos pos, @Nullable BlockState state, @Nullable RandomSource random) {
		return this.partAt(this.localCameraW(level, pos));
	}

	@Override
	public void collectParts(@NonNull BlockAndTintGetter level, @NonNull BlockPos pos, @Nullable BlockState state, @Nullable RandomSource random, @NonNull List<BlockStateModelPart> parts) {
		parts.add(this.partAt(this.localCameraW(level, pos)));
	}
}
