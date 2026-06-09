package com.iluha168.mc4d.mixin.commands4;

import com.iluha168.mc4d.commands.SharedSuggestionProvider4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Mixin(SharedSuggestionProvider.class)
interface SharedSuggestionProviderMixin {
	@Definition(id = "fullValue", local = @Local(type = String.class, name = "fullValue"))
	@Definition(id = "coordinate", local = @Local(type = net.minecraft.commands.SharedSuggestionProvider.TextCoordinates.class, name = "coordinate"))
	@Expression("fullValue = @(? + coordinate.?)")
	@ModifyExpressionValue(method = "suggestCoordinates", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static String suggestCoordinates_0_setFullValue(
		String original,
		@Local(name = "coordinate") SharedSuggestionProvider.TextCoordinates coordinate,
		@Share("fullValue3") LocalRef<String> fullValue3
	) {
		fullValue3.set(original);
		return original + " " + ((SharedSuggestionProvider4.TextCoordinates) coordinate).w();
	}

	@Definition(id = "fullValue", local = @Local(type = String.class, name = "fullValue"))
	@Definition(id = "coordinatex", local = @Local(type = net.minecraft.commands.SharedSuggestionProvider.TextCoordinates.class, name = "coordinatex"))
	@Expression("fullValue = @(? + coordinatex.?)")
	@ModifyExpressionValue(method = "suggestCoordinates", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static String suggestCoordinates_1_setFullValue(
		String original,
		@Local(name = "coordinatex") SharedSuggestionProvider.TextCoordinates coordinatex,
		@Share("fullValue3") LocalRef<String> fullValue3
	) {
		fullValue3.set(original);
		return original + " " + ((SharedSuggestionProvider4.TextCoordinates) coordinatex).w();
	}

	@Definition(id = "fullValue", local = @Local(type = String.class, name = "fullValue"))
	@Definition(id = "coordinatexx", local = @Local(type = net.minecraft.commands.SharedSuggestionProvider.TextCoordinates.class, name = "coordinatexx"))
	@Expression("fullValue = @(? + coordinatexx.?)")
	@ModifyExpressionValue(method = "suggestCoordinates", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static String suggestCoordinates_2_setFullValue(
		String original,
		@Local(name = "coordinatexx") SharedSuggestionProvider.TextCoordinates coordinatexx,
		@Share("fullValue3") LocalRef<String> fullValue3
	) {
		fullValue3.set(original);
		return original + " " + ((SharedSuggestionProvider4.TextCoordinates) coordinatexx).w();
	}

	@Definition(id = "add", method = "Ljava/util/List;add(Ljava/lang/Object;)Z")
	@Definition(id = "fullValue", local = @Local(type = String.class, name = "fullValue"))
	@Expression("?.add(fullValue)")
	@Inject(method = "suggestCoordinates", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void suggestCoordinates_useFullValue3(
		String currentInput, Collection<SharedSuggestionProvider.TextCoordinates> allSuggestions, SuggestionsBuilder builder, Predicate<String> validator, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir,
		@Local(name = "result") List<String> result,
		@Share("fullValue3") LocalRef<String> fullValue3
	) {
		result.add(fullValue3.get());
	}

	@Definition(id = "fields", local = @Local(type = String[].class, name = "fields"))
	@Expression("fields.length")
	@Inject(method = "suggestCoordinates", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static void suggestCoordinates_length3(
		String currentInput, Collection<SharedSuggestionProvider.TextCoordinates> allSuggestions, SuggestionsBuilder builder, Predicate<String> validator, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir,
		@Local(name = "result") List<String> result,
		@Local(name = "fields") String[] fields
	) {
		if (fields.length == 3) {
			for (SharedSuggestionProvider.TextCoordinates coordinatexxx : allSuggestions) {
				String fullValue = fields[0] + " " + fields[1] + " " + fields[2] + " " + ((SharedSuggestionProvider4.TextCoordinates) coordinatexxx).w();
				if (validator.test(fullValue)) {
					result.add(fullValue);
				}
			}
		}
	}

	@Mixin(SharedSuggestionProvider.TextCoordinates.class)
	class TextCoordinatesMixin implements SharedSuggestionProvider4.TextCoordinates {
		@Shadow
		@Final
		public static SharedSuggestionProvider.TextCoordinates DEFAULT_LOCAL;
		@Shadow
		@Final
		public static SharedSuggestionProvider.TextCoordinates DEFAULT_GLOBAL;
		@Unique String w;

		@Override
		public String w() {
			if (this.w == null) {
				throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: w not set."));
			}
			return this.w;
		}

		@Override
		public void setW(String w) {
			this.w = w;
		}

		@Inject(method = "<clinit>", at = @At("TAIL"))
		private static void classInit(CallbackInfo ci) {
			((SharedSuggestionProvider4.TextCoordinates) DEFAULT_LOCAL).setW(DEFAULT_LOCAL.z);
			((SharedSuggestionProvider4.TextCoordinates) DEFAULT_GLOBAL).setW(DEFAULT_GLOBAL.z);
		}
	}
}
