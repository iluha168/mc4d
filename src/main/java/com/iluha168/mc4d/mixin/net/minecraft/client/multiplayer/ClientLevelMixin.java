package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
class ClientLevelMixin {
	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of a 4D particle engine
	 */
	@Overwrite
	private void doAddParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShowParticles, double x, double y, double z, double xd, double yd, double zd) {

	}

	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of 4D sound engine
	 */
	@Overwrite
	private void playSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed) {

	}

	@Redirect(method = "addDestroyBlockEffect", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	void addDestroyBlockEffect(VoxelShape shape, Shapes.DoubleLineConsumer consumer) {
		// TODO support 4D, this is a temporary crash fix
	}
}
