package com.iluha168.mc4d.mixin.net.minecraft.client.player;

import com.iluha168.mc4d.world.entity.player.Input4;
import com.iluha168.mc4d.world.phys.HorizontalVec;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.ClientInput;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientInput.class)
class ClientInputMixin {
	@Shadow
	public Input keyPresses;

	@Shadow
	protected Vec2 moveVector;

	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec2;ZERO:Lnet/minecraft/world/phys/Vec2;")
	@Expression("ZERO")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	Vec2 init(Vec2 original) {
		return HorizontalVec.ZERO;
	}

	@ModifyReturnValue(method = "hasForwardImpulse", at = @At("RETURN"))
	boolean hasForwardImpulse(boolean original) {
		// This does diverge from "W axis must behave exactly like X and Z" philosophy,
		// but until entities have 5 head rotations, this is the only way to enable W sprinting.
		return original || Mth.abs(((HorizontalVec) this.moveVector).z) > Mth.EPSILON;
	}

	@ModifyExpressionValue(method = "makeJump", at = @At(
		value = "NEW",
		target = "(ZZZZZZZ)Lnet/minecraft/world/entity/player/Input;"
	))
	Input makeJump(Input original) {
		Input4 keyPresses4 = Input4.as(this.keyPresses);
		Input4 input4 = Input4.as(original);
		input4.setAna(keyPresses4.ana());
		input4.setKata(keyPresses4.kata());
		return original;
	}
}
