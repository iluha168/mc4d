package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.network.protocol.game.ClientboundAddEntityPacket4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundAddEntityPacket.class)
class ClientboundAddEntityPacketMixin implements ClientboundAddEntityPacket4 {
	@Unique private double w;
	@SuppressWarnings("BooleanVariableAlwaysNegated")
	@Unique private boolean wSet;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerEntity;I)V", at = @At("TAIL"))
	void init(Entity entity, ServerEntity serverEntity, int data, CallbackInfo ci) {
		this.setW(((Vec4) serverEntity.getPositionBase()).w());
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;ILnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
	void init(Entity entity, int data, BlockPos pos, CallbackInfo ci) {
		this.setW(Vec4i.getW(pos));
	}

	@Override
	public void setW(double w) {
		this.w = w;
		this.wSet = true;
	}

	@Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
	void init(RegistryFriendlyByteBuf input, CallbackInfo ci) {
		this.setW(input.readDouble());
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(RegistryFriendlyByteBuf output, CallbackInfo ci) {
		output.writeDouble(this.getW());
	}

	@Override
	public double getW() {
		if (!this.wSet) throw Err4.field4missing("w");
		return this.w;
	}
}
