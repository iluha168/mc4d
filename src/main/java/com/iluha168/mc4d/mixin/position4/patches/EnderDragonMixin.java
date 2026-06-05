package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
class EnderDragonMixin {
	@Shadow
	@Final
	private EnderDragonPart[] subEntities;

	@Definition(id = "subEntities", field = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;subEntities:[Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;")
	@Definition(id = "zo", field = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;zo:D")
	@Definition(id = "oldPos", local = @Local(type = Vec3[].class, name = "oldPos"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Definition(id = "i", local = @Local(type = int.class, name = "i"))
	@Expression("this.subEntities[i].zo = oldPos[i].z")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void subEntitiesWO(
		CallbackInfo ci,
		@Local(name = "i") int i,
		@Local(name = "oldPos") Vec3[] oldPos
	) {
		((Entity4) this.subEntities[i]).setWO(((Vec4) oldPos[i]).w);
	}

	@Definition(id = "subEntities", field = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;subEntities:[Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;")
	@Definition(id = "zOld", field = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;zOld:D")
	@Definition(id = "oldPos", local = @Local(type = Vec3[].class, name = "oldPos"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Definition(id = "i", local = @Local(type = int.class, name = "i"))
	@Expression("this.subEntities[i].zOld = oldPos[i].z")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void subEntitiesWOld(
		CallbackInfo ci,
		@Local(name = "i") int i,
		@Local(name = "oldPos") Vec3[] oldPos
	) {
		((Entity4) this.subEntities[i]).setWOld(((Vec4) oldPos[i]).w);
	}
}
