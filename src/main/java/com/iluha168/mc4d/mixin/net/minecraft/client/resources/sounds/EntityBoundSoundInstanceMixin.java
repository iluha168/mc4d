package com.iluha168.mc4d.mixin.net.minecraft.client.resources.sounds;

import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoundSoundInstance.class)
class EntityBoundSoundInstanceMixin extends AbstractSoundInstanceMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(SoundEvent event, SoundSource source, float volume, float pitch, Entity entity, long seed, CallbackInfo ci) {
		this.setW((float) ((Entity4) this.entity).getW());
	}

	@Definition(id = "z", field = "Lnet/minecraft/client/resources/sounds/EntityBoundSoundInstance;z:D")
	@Expression("this.z = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick(CallbackInfo ci) {
		this.setW((float) ((Entity4) this.entity).getW());
	}
}
