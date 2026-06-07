package com.iluha168.mc4d.mixin.commands4;

import com.iluha168.mc4d.commands.arguments.coordinates.Coordinates4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalCoordinates.class)
class LocalCoordinatesMixin implements Coordinates4 {
	@Shadow
	private static double readDouble(StringReader reader, int start) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique	private double ana;

	@Inject(method = "<init>", at = @At("TAIL"))
	void constructor(double left, double up, double forwards, CallbackInfo ci) {
		this.ana = Double.NaN;
	}

	@Redirect(method = "getPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition_new(double x, double y, double z) {
		if (Double.isNaN(this.ana))
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: no ana set."));
		return new Vec4(x, y, z, this.ana);
	}
	@Redirect(method = "getPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition_new(Vec3 rotated, double x, double y, double z, @Local(name = "source") Vec3 source) {
		return ((Vec4) rotated).add(x, y, z, ((Vec4) source).w);
	}

	@Override
	public boolean isWRelative() {
		return true;
	}

	@ModifyExpressionValue(method = "parse", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/commands/arguments/coordinates/LocalCoordinates;"
	))
	private static LocalCoordinates parse(
		LocalCoordinates parsed,
		@Local(argsOnly = true, name = "reader") StringReader reader,
		@Local(name = "start") int start
	) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			((LocalCoordinatesMixin) (Object) parsed).ana = readDouble(reader, start);
			return parsed;
		} else {
			reader.setCursor(start);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}
}
