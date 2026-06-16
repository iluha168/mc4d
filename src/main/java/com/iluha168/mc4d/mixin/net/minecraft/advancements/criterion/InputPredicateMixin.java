package com.iluha168.mc4d.mixin.net.minecraft.advancements.criterion;

import com.iluha168.mc4d.advancements.criterion.InputPredicate4;
import com.iluha168.mc4d.world.entity.player.Input4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.InputPredicate;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(InputPredicate.class)
abstract
class InputPredicateMixin implements InputPredicate4 {
	@Shadow
	protected abstract boolean matches(Optional<Boolean> match, boolean value);

	@Unique	private Optional<Boolean> ana;
	@Unique private Optional<Boolean> kata;

	@Override
	public Optional<Boolean> ana() {
		return this.ana;
	}
	@Override
	public Optional<Boolean> kata() {
		return this.kata;
	}

	@Override
	public void setAna(Optional<Boolean> ana) {
		this.ana = ana;
	}
	@Override
	public void setKata(Optional<Boolean> kata) {
		this.kata = kata;
	}

	@Definition(id = "CODEC", field = "Lnet/minecraft/advancements/criterion/InputPredicate;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Codec<InputPredicate> CODEC(Codec<InputPredicate> original) {
		return RecordCodecBuilder.create(
			i -> i.group(
					Codec.BOOL.optionalFieldOf("forward").forGetter(InputPredicate::forward),
					Codec.BOOL.optionalFieldOf("backward").forGetter(InputPredicate::backward),
					Codec.BOOL.optionalFieldOf("left").forGetter(InputPredicate::left),
					Codec.BOOL.optionalFieldOf("right").forGetter(InputPredicate::right),
					Codec.BOOL.optionalFieldOf("ana").forGetter(predicate -> InputPredicate4.as(predicate).ana()),
					Codec.BOOL.optionalFieldOf("kata").forGetter(predicate -> InputPredicate4.as(predicate).kata()),
					Codec.BOOL.optionalFieldOf("jump").forGetter(InputPredicate::jump),
					Codec.BOOL.optionalFieldOf("sneak").forGetter(InputPredicate::sneak),
					Codec.BOOL.optionalFieldOf("sprint").forGetter(InputPredicate::sprint)
				)
				.apply(i, InputPredicate4::from)
		);
	}

	@WrapMethod(method = "matches(Lnet/minecraft/world/entity/player/Input;)Z")
	boolean matches(Input input, Operation<Boolean> original) {
		Input4 input4 = Input4.as(input);
		return original.call(input)
			&& this.matches(this.ana, input4.ana())
			&& this.matches(this.kata, input4.kata());
	}
}
