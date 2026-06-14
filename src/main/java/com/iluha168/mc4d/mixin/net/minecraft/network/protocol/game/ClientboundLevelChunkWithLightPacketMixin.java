package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.ClientboundLevelChunkWithLightPacket4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ClientboundLevelChunkWithLightPacket.class)
class ClientboundLevelChunkWithLightPacketMixin implements ClientboundLevelChunkWithLightPacket4 {
	@Unique private int w;

	@Inject(method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/lighting/LevelLightEngine;Ljava/util/BitSet;Ljava/util/BitSet;)V", at = @At("TAIL"))
	private void init(
		LevelChunk levelChunk, LevelLightEngine lightEngine, BitSet skyChangedLightSectionFilter, BitSet blockChangedLightSectionFilter, CallbackInfo ci,
		@Local(name = "chunkPos") ChunkPos chunkPos
	) {
		this.w = ChunkPos4.as(chunkPos).w();
	}

	@Definition(id = "x", field = "Lnet/minecraft/network/protocol/game/ClientboundLevelChunkWithLightPacket;x:I")
	@Expression("this.x = @(?)")
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	int read(int original, @Local(argsOnly = true, name = "input") RegistryFriendlyByteBuf input) {
		this.w = input.readInt();
		return original;
	}

	@WrapMethod(method = "write")
	void write(RegistryFriendlyByteBuf output, Operation<Void> original) {
		output.writeInt(this.w);
		original.call(output);
	}

	@Override
	public int getW() {
		return this.w;
	}
}
