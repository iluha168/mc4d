package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.world.level.chunk.DataLayer4;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.world.level.chunk.DataLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientboundLightUpdatePacketData.class)
class ClientboundLightUpdatePacketDataMixin {
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = DataLayer.SIZE))
	private static int size(int constant) {
		return DataLayer4.SIZE;
	}

	// TODO the rest
}
