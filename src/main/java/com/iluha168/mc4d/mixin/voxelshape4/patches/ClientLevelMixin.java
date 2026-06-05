package com.iluha168.mc4d.mixin.voxelshape4.patches;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
class ClientLevelMixin {
	@Redirect(method = "addDestroyBlockEffect", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	void addDestroyBlockEffect(VoxelShape shape, Shapes.DoubleLineConsumer consumer) {
		// TODO support 4D, this is a temporary crash fix
	}
}
