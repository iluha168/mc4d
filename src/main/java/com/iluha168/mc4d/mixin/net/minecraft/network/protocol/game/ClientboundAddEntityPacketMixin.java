package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.network.protocol.game.ClientboundAddEntityPacket4;
import com.iluha168.mc4d.server.level.ServerEntity4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundAddEntityPacket.class)
class ClientboundAddEntityPacketMixin implements ClientboundAddEntityPacket4 {
	@Unique private double w;
	@Unique	private boolean initComplete;
	@Unique private byte wRot;
	@Unique private byte vRot;
	@Unique private byte wHeadRot;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerEntity;I)V", at = @At("TAIL"))
	void init(Entity entity, ServerEntity serverEntity, int data, CallbackInfo ci) {
		final ServerEntity4 serverEntity4 = (ServerEntity4) serverEntity;
		this.init_finish(
			((Vec4) serverEntity.getPositionBase()).w,
			serverEntity4.getLastSentWRot(),
			serverEntity4.getLastSentVRot(),
			serverEntity4.getLastSentWHeadRot()
		);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;ILnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
	void init(Entity entity, int data, BlockPos pos, CallbackInfo ci) {
		final Entity4 entity4 = (Entity4) entity;
		this.init_finish(
			Vec4i.getW(pos),
			entity4.getWRot(),
			entity4.getVRot(),
			entity4.getWHeadRot()
		);
	}

	@Override
	public void init_finish(double w, float wRot, float vRot, double wHeadRot) {
		if (this.initComplete) {
			throw new IllegalStateException("Programmer error: ClientboundAddEntityPacket4#init_finish called more than once.");
		}
		this.w = w;
		this.wRot = Mth.packDegrees(wRot);
		this.vRot = Mth.packDegrees(vRot);
		this.wHeadRot = Mth.packDegrees((float) wHeadRot);
		this.initComplete = true;
	}

	@Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
	void init(RegistryFriendlyByteBuf input, CallbackInfo ci) {
		this.w = input.readDouble();
		this.wRot = input.readByte();
		this.vRot = input.readByte();
		this.wHeadRot = input.readByte();
		this.initComplete = true;
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(RegistryFriendlyByteBuf output, CallbackInfo ci) {
		output.writeDouble(this.getW());
		output.writeByte(this.wRot);
		output.writeByte(this.vRot);
		output.writeByte(this.wHeadRot);
	}

	@Override
	public double getW() {
		if (!this.initComplete) throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public float getWRot() {
		if (!this.initComplete) throw Err4.field4missing("wRot");
		return Mth.unpackDegrees(this.wRot);
	}
	@Override
	public float getVRot() {
		if (!this.initComplete) throw Err4.field4missing("vRot");
		return Mth.unpackDegrees(this.vRot);
	}
	@Override
	public float getWHeadRot() {
		if (!this.initComplete) throw Err4.field4missing("wHeadRot");
		return Mth.unpackDegrees(this.wHeadRot);
	}
}
