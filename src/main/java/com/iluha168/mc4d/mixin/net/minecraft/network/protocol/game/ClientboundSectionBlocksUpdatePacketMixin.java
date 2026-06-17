package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ClientboundSectionBlocksUpdatePacket.class)
class ClientboundSectionBlocksUpdatePacketMixin {
	@Shadow @Final private SectionPos sectionPos;
	@Shadow @Final private short[] positions;
	@Shadow @Final private BlockState[] states;

	@Redirect(method = "<init>(Lnet/minecraft/core/SectionPos;Lit/unimi/dsi/fastutil/shorts/ShortSet;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"
	))
	private BlockState init_getBlockState(LevelChunkSection section, int sectionX, int sectionY, int sectionZ, @Local(name = "packedPos") short packedPos) {
		return ((LevelChunkSection4) section).getBlockState(sectionX, sectionY, sectionZ, SectionPos4.sectionRelativeW(packedPos));
	}

	@ModifyConstant(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", constant = @Constant(longValue = (1L<<12L) - 1L))
	private long init_posMask(long original) {
		return (1L<<16L) - 1L;
	}
	@ModifyConstant(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", constant = @Constant(intValue = 12))
	private int init_posShift(int original) {
		return 16;
	}

	@ModifyArg(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/FriendlyByteBuf;writeVarLong(J)Lnet/minecraft/network/FriendlyByteBuf;"
	))
	private long write(long value, @Local(name = "i") int i) {
		return (long) Block.getId(this.states[i]) << 16 | Short.toUnsignedLong(this.positions[i]);
	}

	@Redirect(method = "runUpdates(Ljava/util/function/BiConsumer;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos runUpdates(BlockPos.MutableBlockPos cursor, int x, int y, int z, @Local(name = "packedPos") short packedPos) {
		return ((BlockPos4.MutableBlockPos) cursor).set(x, y, z, ((SectionPos4) this.sectionPos).relativeToBlockW(packedPos));
	}
}