package com.iluha168.mc4d.mixin.net.minecraft.world.entity.item;

import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Definition(id = "zo", field = "Lnet/minecraft/world/entity/item/ItemEntity;zo:D")
	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/item/ItemEntity;getZ()D")
	@Expression("this.zo = this.getZ()")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void setWO(CallbackInfo ci) {
		Entity4 this4 = (Entity4) this;
		this4.setWO(this4.getW());
	}
}
