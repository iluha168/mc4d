package com.iluha168.mc4d.client.renderer.block.dispatch;

import com.iluha168.mc4d.MC4D;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.WeightedVariants;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.WeightedList;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implemented by {@link Variant}.
 */
public interface Variant4 {
	static Variant from(Either<Identifier, List<WRangeModel>> model, Variant.SimpleModelState modelState) {
		return model.map(
			legacy3DOnlyModel -> new Variant(legacy3DOnlyModel, modelState),
			slices -> {
				final Variant variant = new Variant(slices.getFirst().model(), modelState);
				Variant4.as(variant).setWRangeModels(slices);
				return variant;
			}
		);
	}

	static Variant4 as(Variant variant) {
		return (Variant4) (Object) variant;
	}

	/**
	 * Block-local W spans [0;1), scaled by 16 in blockstate definitions for convenience.
	 * This is because voxel shapes are defined with 16 scale too ({@link net.minecraft.world.level.block.Block#box}).
	 */
	float W_SCALE = 16;

	/** The {@code "w=<fromInclusive>;<untilExclusive>"} map key. */
	record WRange(float minW, float maxW) {
		public static final Codec<WRange> CODEC = Codec.STRING.comapFlatMap(WRange::parse, WRange::toKey);

		private static DataResult<WRange> parse(String key) {
			if (key.startsWith("w=")) {
				final int separator = key.indexOf(';', 2);
				if (separator >= 0) try {
					final float minW = Float.parseFloat(key.substring(2, separator));
					final float maxW = Float.parseFloat(key.substring(separator + 1));
					if (minW < maxW)
						return DataResult.success(new WRange(minW, maxW));
					return DataResult.error(() -> "Empty W range: '" + key + "'");
				} catch (NumberFormatException ignored) {}
			}
			return DataResult.error(() -> "W range must look like \"w=<fromInclusive>;<untilExclusive>\", got: '" + key + "'");
		}

		private String toKey() {
			return "w=" + format(this.minW) + ";" + format(this.maxW);
		}
		private static String format(float value) {
			return value == (long) value ? Long.toString((long) value) : Float.toString(value);
		}
	}

	/** The {@code "w=<fromInclusive>;<untilExclusive>": "<Identifier>"} map entry. */
	record WRangeModel(float minW, float maxW, @NonNull Identifier model) {
		public boolean contains(float localW) {
			return localW >= this.minW && localW < this.maxW;
		}
	}

	// A list because the order matters
	Codec<List<WRangeModel>> W_SLICES_CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(WRange.CODEC, Identifier.CODEC))
		.xmap(
			ranges -> ranges.entrySet().stream()
				.map(entry -> new WRangeModel(entry.getKey().minW(), entry.getKey().maxW(), entry.getValue()))
				.toList(),
			slices -> {
				final Map<WRange, Identifier> ranges = new LinkedHashMap<>(slices.size());
				for (final WRangeModel slice : slices)
					ranges.put(new WRange(slice.minW(), slice.maxW()), slice.model());
				return ranges;
			}
		);
	// Preserving backwards compatibility, allowing a vanilla model name, which would render for all slices
	Codec<Either<Identifier, List<WRangeModel>>> MODEL_CODEC = Codec.either(Identifier.CODEC, W_SLICES_CODEC);

	/** The W cross-sections in declaration order, or {@code null} for a 3D-only ({@code "model": "<Identifier>"}) variant. */
	@Nullable List<WRangeModel> wRangeModels();
	void setWRangeModels(@NonNull List<WRangeModel> wRangeModels);
	@ApiStatus.Internal
	void setWRangeModelsForce(@Nullable List<WRangeModel> wRangeModels);

	default Either<Identifier, List<WRangeModel>> wModel() {
		final List<WRangeModel> slices = this.wRangeModels();
		@SuppressWarnings("DataFlowIssue") final Variant This = (Variant) (Object) this;
		return slices == null ? Either.left(This.modelLocation()) : Either.right(slices);
	}

	static void warnWCoverage(BlockStateModel.Unbaked model, String selector, Supplier<String> source) {
		switch (model) {
			case SingleVariant.Unbaked(Variant variant) -> warnWCoverage(variant, selector, source);
			case WeightedVariants.Unbaked(WeightedList<BlockStateModel.Unbaked> entries) ->
				entries.unwrap().forEach(entry -> warnWCoverage(entry.value(), selector, source));
			default -> {}
		}
	}
	private static void warnWCoverage(Variant variant, String selector, Supplier<String> source) {
		final List<WRangeModel> slices = as(variant).wRangeModels();
		if (slices == null) {
			MC4D.LOGGER.warn("3D-only model '{}' for variant: '{}' in '{}'", variant.modelLocation(), selector, source.get());
			return;
		}
		final String gaps = findWGaps(slices);
		if (gaps != null)
			MC4D.LOGGER.warn("Missing model for W ranges {} for variant: '{}' in '{}'", gaps, selector, source.get());
	}

	/** The parts of [0;{@link #W_SCALE}) not covered by any slice, or {@code null} if fully covered. */
	static @Nullable String findWGaps(List<WRangeModel> slices) {
		final List<WRangeModel> sorted = slices.stream().sorted(Comparator.comparingDouble(WRangeModel::minW)).toList();
		StringBuilder gaps = null;
		float cursor = 0.0F;
		for (final WRangeModel slice : sorted) {
			if (slice.minW() > cursor) {
				gaps = appendGap(gaps, cursor, Math.min(slice.minW(), W_SCALE));
			}
			cursor = Math.max(cursor, slice.maxW());
			if (cursor >= W_SCALE) break;
		}
		if (cursor < W_SCALE) gaps = appendGap(gaps, cursor, W_SCALE);
		return gaps == null ? null : gaps.toString();
	}
	private static StringBuilder appendGap(@Nullable StringBuilder gaps, float from, float until) {
		if (gaps == null) gaps = new StringBuilder();
		else gaps.append(", ");
		return gaps.append('[').append(WRange.format(from)).append(';').append(WRange.format(until)).append(')');
	}
}
