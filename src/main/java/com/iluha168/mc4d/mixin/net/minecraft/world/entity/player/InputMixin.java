package com.iluha168.mc4d.mixin.net.minecraft.world.entity.player;

import com.iluha168.mc4d.world.entity.player.Input4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Input.class)
class InputMixin implements Input4 {
	@Unique private boolean ana;
	@Unique private boolean kata;

	// probably do not need to override equals and hashCode

	@Override
	public boolean ana() {
		return this.ana;
	}
	@Override
	public boolean kata() {
		return this.kata;
	}

	@Override
	public void setAna(boolean ana) {
		this.ana = ana;
	}
	@Override
	public void setKata(boolean kata) {
		this.kata = kata;
	}

	@Mixin(targets = "net/minecraft/world/entity/player/Input$1")
	static class StreamCodecMixin {
		@Inject(method = "encode(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/world/entity/player/Input;)V", at = @At("TAIL"))
		void encode(FriendlyByteBuf output, Input value, CallbackInfo ci) {
			Input4 value4 = Input4.as(value);
			byte flags = 0;
			flags |= (byte) (value4.ana() ? 1 : 0);
			flags |= (byte) (value4.kata() ? 2 : 0);
			output.writeByte(flags);
		}

		@ModifyExpressionValue(method = "decode(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/world/entity/player/Input;", at = @At(
			value = "NEW",
			target = "(ZZZZZZZ)Lnet/minecraft/world/entity/player/Input;"
		))
		Input decode(Input result, @Local(argsOnly = true, name = "input") FriendlyByteBuf input) {
			Input4 result4 = Input4.as(result);
			byte flags = input.readByte();
			result4.setAna((flags & 1) != 0);
			result4.setKata((flags & 2) != 0);
			return result;
		}
	}
}
