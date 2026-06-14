package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.network.protocol.game.ClientboundLevelChunkPacketData4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ClientboundLevelChunkPacketData.class)
class ClientboundLevelChunkPacketDataMixin implements ClientboundLevelChunkPacketData4 {
	@Shadow
	@Final
	private List<ClientboundLevelChunkPacketData.BlockEntityInfo> blockEntitiesData;

	@Overwrite
	@Deprecated
	public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int x, int z) {
		throw Err4.arguments2("ClientboundLevelChunkPacketData4#getBlockEntitiesTagsConsumer");
	}
	@Override
	public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int x, int z, int w) {
		return output -> this.getBlockEntitiesTags(output, x, z, w);
	}

	@Overwrite
	@Deprecated
	private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput output, int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput output, int x, int z, int w) {
		int baseX = 16 * x;
		int baseZ = 16 * z;
		int baseW = 16 * w;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (ClientboundLevelChunkPacketData.BlockEntityInfo data : this.blockEntitiesData) {
			int unpackedX = baseX + SectionPos.sectionRelative(data.packedXZ >> 4);
			int unpackedZ = baseZ + SectionPos.sectionRelative(data.packedXZ);
			int unpackedW = baseW + SectionPos.sectionRelative(data.packedXZ >> 8);
			((BlockPos4.MutableBlockPos) pos).set(unpackedX, data.y, unpackedZ, unpackedW);
			output.accept(pos, data.type, data.tag);
		}
	}

	@Mixin(ClientboundLevelChunkPacketData.BlockEntityInfo.class)
	static class BlockEntityInfoMixin {
		@Mutable
		@Shadow
		@Final
		public int packedXZ;

		@Definition(id = "packedXZ", field = "Lnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData$BlockEntityInfo;packedXZ:I")
		@Definition(id = "readByte", method = "Lnet/minecraft/network/RegistryFriendlyByteBuf;readByte()B")
		@Expression("this.packedXZ = @(?.readByte())")
		@Redirect(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
		byte read(RegistryFriendlyByteBuf instance) {
			return 0; // Reading a short instead
		}
		@Definition(id = "packedXZ", field = "Lnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData$BlockEntityInfo;packedXZ:I")
		@Expression("this.packedXZ = @(?)")
		@Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
		void read(RegistryFriendlyByteBuf input, CallbackInfo ci) {
			this.packedXZ = input.readShort();
		}

		@Definition(id = "writeByte", method = "Lnet/minecraft/network/RegistryFriendlyByteBuf;writeByte(I)Lnet/minecraft/network/FriendlyByteBuf;")
		@Definition(id = "packedXZ", field = "Lnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData$BlockEntityInfo;packedXZ:I")
		@Expression("?.writeByte(this.packedXZ)")
		@Redirect(method = "write", at = @At("MIXINEXTRAS:EXPRESSION"))
		FriendlyByteBuf write(RegistryFriendlyByteBuf instance, int packedXZ) {
			return instance.writeShort(packedXZ);
		}

		@Definition(id = "xz", local = @Local(type = int.class, name = "xz"))
		@Expression("xz = @(?)")
		@ModifyExpressionValue(method = "create", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static int create(int xz, @Local(name = "pos") BlockPos pos) {
			return xz | SectionPos.sectionRelative(Vec4i.getW(pos)) << 8;
		}
	}
}
