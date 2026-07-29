package com.iluha168.mc4d.mixin.net.minecraft.commands.arguments.coordinates;

import com.iluha168.mc4d.commands.arguments.coordinates.WorldCoordinates4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;

@Mixin(RotationArgument.class)
class RotationArgumentMixin {
	@Shadow
	@Final
	public static SimpleCommandExceptionType ERROR_NOT_COMPLETE;

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Ljava/util/Arrays;asList([Ljava/lang/Object;)Ljava/util/List;"
	))
	private static <T> List<String> EXAMPLES(T[] a) {
		return Arrays.asList("0 0 0 0", "~ ~ ~ ~", "~-5 ~5 ~ ~");
	}

	// TODO ERROR_NOT_COMPLETE

	@ModifyExpressionValue(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/commands/arguments/coordinates/Coordinates;", at = @At(
		value = "NEW",
		target = "(ZD)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;"
	))
	private WorldCoordinate parse_wRot(
		WorldCoordinate original,
		@Local(argsOnly = true, name = "reader") StringReader reader,
		@Local(name = "start") int start
	) throws CommandSyntaxException	{
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			return WorldCoordinate.parseDouble(reader, false); // wRot
		} else {
			reader.setCursor(start);
			throw ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}
	@ModifyExpressionValue(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/commands/arguments/coordinates/Coordinates;", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;"
	))
	private WorldCoordinates parse_vRot(
		WorldCoordinates coordinates,
		@Local(argsOnly = true, name = "reader") StringReader reader,
		@Local(name = "start") int start
	) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			WorldCoordinates4.as(coordinates).setW(WorldCoordinate.parseDouble(reader, false)); // vRot
		} else {
			reader.setCursor(start);
			throw ERROR_NOT_COMPLETE.createWithContext(reader);
		}
		return coordinates;
	}
}
