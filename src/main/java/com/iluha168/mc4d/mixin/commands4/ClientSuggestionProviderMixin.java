package com.iluha168.mc4d.mixin.commands4;

import com.iluha168.mc4d.commands.SharedSuggestionProvider4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientSuggestionProvider.class)
class ClientSuggestionProviderMixin {
	@Shadow
	private static String prettyPrint(double value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private static String prettyPrint(int value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@ModifyExpressionValue(method = "getRelevantCoordinates", at = @At(
		value = "NEW",
		target = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/commands/SharedSuggestionProvider$TextCoordinates;"
	))
	SharedSuggestionProvider.TextCoordinates getRelevantCoordinates(
		SharedSuggestionProvider.TextCoordinates original,
		@Local(name = "pos") BlockPos pos
	) {
		((SharedSuggestionProvider4.TextCoordinates) original).setW(prettyPrint(Vec4i.getW(pos)));
		return original;
	}

	@ModifyExpressionValue(method = "getAbsoluteCoordinates", at = @At(
		value = "NEW",
		target = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/commands/SharedSuggestionProvider$TextCoordinates;"
	))
	SharedSuggestionProvider.TextCoordinates getAbsoluteCoordinates(
		SharedSuggestionProvider.TextCoordinates original,
		@Local(name = "pos") Vec3 pos
	) {
		((SharedSuggestionProvider4.TextCoordinates) original).setW(prettyPrint(((Vec4) pos).w));
		return original;
	}
}
