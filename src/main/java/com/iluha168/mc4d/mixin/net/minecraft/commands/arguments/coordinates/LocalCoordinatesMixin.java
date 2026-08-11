package com.iluha168.mc4d.mixin.net.minecraft.commands.arguments.coordinates;

import com.iluha168.mc4d.commands.arguments.coordinates.Coordinates4;
import com.iluha168.mc4d.commands.arguments.coordinates.LocalCoordinates4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalCoordinates.class)
class LocalCoordinatesMixin implements Coordinates4, LocalCoordinates4 {
	@Shadow
	private static double readDouble(StringReader reader, int start) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique	private double ana;

	@Override
	public double ana() {
		if (Double.isNaN(this.ana))
			throw Err4.field4missing("ana");
		return this.ana;
	}
	@Override
	public void setAna(double ana) {
		this.ana = ana;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(double left, double up, double forwards, CallbackInfo ci) {
		this.ana = Double.NaN;
	}

	@Redirect(method = "getPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition_new(double x, double y, double z) {
		return new Vec4(x, y, z, this.ana());
	}
	@Redirect(method = "getPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition_add(Vec3 rotated, double x, double y, double z, @Local(name = "source") Vec3 source) {
		return ((Vec4) rotated).add(x, y, z, ((Vec4) source).w);
	}

	@Redirect(method = "getRotation", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;ZERO:Lnet/minecraft/world/phys/Vec2;",
		opcode = Opcodes.GETSTATIC
	))
	Vec2 getRotation() {
		return RotationVec.ZERO;
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
			LocalCoordinates4.as(parsed).setAna(readDouble(reader, start));
			return parsed;
		} else {
			reader.setCursor(start);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}
}
