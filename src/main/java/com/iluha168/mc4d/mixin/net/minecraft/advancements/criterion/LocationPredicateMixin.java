package com.iluha168.mc4d.mixin.net.minecraft.advancements.criterion;

import com.iluha168.mc4d.advancements.criterion.LocationPredicate4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(LocationPredicate.class)
class LocationPredicateMixin implements LocationPredicate4 {
	@Shadow
	@Final
	private Optional<LocationPredicate.PositionPredicate> position;

	@Shadow
	@Final
	private Optional<ResourceKey<Level>> dimension;

	@Shadow
	@Final
	private Optional<HolderSet<Biome>> biomes;

	@Shadow
	@Final
	private Optional<HolderSet<Structure>> structures;

	@Shadow
	@Final
	private Optional<Boolean> smokey;

	@Shadow
	@Final
	private Optional<LightPredicate> light;

	@Shadow
	@Final
	private Optional<BlockPredicate> block;

	@Shadow
	@Final
	private Optional<FluidPredicate> fluid;

	@Shadow
	@Final
	private Optional<Boolean> canSeeSky;

	@Overwrite
	@Deprecated
	public boolean matches(ServerLevel level, double x, double y, double z) {
		throw Err4.arguments3("LocationPredicate4#matches");
	}
	@Override
	public boolean matches(ServerLevel level, double x, double y, double z, double w) {
		if (this.position.isPresent() && !LocationPredicate4.PositionPredicate.as(this.position.get()).matches(x, y, z, w)) {
			return false;
		}
		if (this.dimension.isPresent() && this.dimension.get() != level.dimension()) {
			return false;
		}
		BlockPos pos = BlockPos4.containing(x, y, z, w);
		boolean unloaded = !level.isLoaded(pos);
		if (this.biomes.isPresent() && (unloaded || !this.biomes.get().contains(level.getBiome(pos)))) {
			return false;
		}
		if (this.structures.isPresent() && (unloaded || !level.structureManager().getStructureWithPieceAt(pos, this.structures.get()).isValid())) {
			return false;
		}
		if (this.smokey.isPresent() && (unloaded || this.smokey.get() != CampfireBlock.isSmokeyPos(level, pos))) {
			return false;
		}
		if (this.light.isPresent() && !this.light.get().matches(level, pos)) {
			return false;
		}
		if (this.block.isPresent() && !this.block.get().matches(level, pos)) {
			return false;
		}
		if (this.fluid.isPresent() && !this.fluid.get().matches(level, pos)) {
			return false;
		}
		//noinspection RedundantIfStatement
		if (this.canSeeSky.isPresent() && this.canSeeSky.get() != level.canSeeSky(pos)) {
			return false;
		}
		return true;
	}

	@Mixin(LocationPredicate.Builder.class)
	static class BuilderMixin implements LocationPredicate4.Builder {
		@Unique private MinMaxBounds.Doubles w;

		@Inject(method = "<init>", at = @At("TAIL"))
		void init(CallbackInfo ci) {
			this.w = MinMaxBounds.Doubles.ANY;
		}

		@Override
		public LocationPredicate.Builder setW(MinMaxBounds.Doubles w) {
			this.w = w;
			return (LocationPredicate.Builder) (Object) this;
		}

		@Redirect(method = "build", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancements/criterion/LocationPredicate$PositionPredicate;of(Lnet/minecraft/advancements/criterion/MinMaxBounds$Doubles;Lnet/minecraft/advancements/criterion/MinMaxBounds$Doubles;Lnet/minecraft/advancements/criterion/MinMaxBounds$Doubles;)Ljava/util/Optional;"
		))
		Optional<LocationPredicate.PositionPredicate> build(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
			return LocationPredicate4.PositionPredicate.of(x, y, z, this.w);
		}
	}

	@Mixin(LocationPredicate.PositionPredicate.class)
	static class PositionPredicateMixin implements PositionPredicate {
		@Shadow
		@Final
		private MinMaxBounds.Doubles x;
		@Shadow
		@Final
		private MinMaxBounds.Doubles y;
		@Shadow
		@Final
		private MinMaxBounds.Doubles z;
		@Unique private MinMaxBounds.Doubles w;

		// I am aware about synthetic toString, equals and hashCode, do not think that needs to be done
		@Override
		public MinMaxBounds.Doubles w() {
			return this.w;
		}
		@Override
		public void setW(MinMaxBounds.Doubles w) {
			this.w = w;
		}

		@Definition(id = "CODEC", field = "Lnet/minecraft/advancements/criterion/LocationPredicate$PositionPredicate;CODEC:Lcom/mojang/serialization/Codec;")
		@Expression("CODEC = @(?)")
		@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static Codec<LocationPredicate.PositionPredicate> CODEC(Codec<LocationPredicate.PositionPredicate> original) {
			return RecordCodecBuilder.create(
				i -> i.group(
						MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::x),
						MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::y),
						MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(LocationPredicate.PositionPredicate::z),
						MinMaxBounds.Doubles.CODEC.optionalFieldOf("w", MinMaxBounds.Doubles.ANY).forGetter(predicate -> LocationPredicate4.PositionPredicate.as(predicate).w())
					)
					.apply(i, PositionPredicate::from)
			);

		}

		@Overwrite
		@Deprecated
		private static Optional<LocationPredicate.PositionPredicate> of(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
			throw Err4.arguments3("LocationPredicate4.PositionPredicate#of");
		}

		@Overwrite
		@Deprecated
		public boolean matches(double x, double y, double z) {
			throw Err4.arguments3("LocationPredicate4.PositionPredicate#matches");
		}
		@Override
		public boolean matches(double x, double y, double z, double w) {
			return this.x.matches(x) && this.y.matches(y) && this.z.matches(z) && this.w.matches(w);
		}
	}
}
