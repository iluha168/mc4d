package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundLightUpdatePacket4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ClientboundLightUpdatePacket.class)
class ClientboundLightUpdatePacketMixin implements ClientboundLightUpdatePacket4 {
	@Unique private int w;

	@Inject(method = "<init>(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/lighting/LevelLightEngine;Ljava/util/BitSet;Ljava/util/BitSet;)V", at = @At("TAIL"))
	private void init(ChunkPos pos, LevelLightEngine lightEngine, BitSet skyChangedLightSectionFilter, BitSet blockChangedLightSectionFilter, CallbackInfo ci) {
		this.w = ChunkPos4.as(pos).w();
	}

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	private void init(FriendlyByteBuf input, CallbackInfo ci) {
		this.w = input.readVarInt();
	}

	@Inject(method = "write", at = @At("TAIL"))
	private void write(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeVarInt(this.w);
	}

	@Override
	public int getW() {
		return this.w;
	}
}