package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.EntityAttachments4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(EntityAttachments.class)
class EntityAttachmentsMixin implements EntityAttachments4 {
	@Shadow
	@Final
	private Map<EntityAttachment, List<Vec3>> attachments;

	@Overwrite
	@Deprecated
	public EntityAttachments scale(float x, float y, float z) {
		throw Err4.arguments3("EntityAttachments4#scale");
	}
	@Override
	public EntityAttachments scale(float x, float y, float z, float w) {
		return new EntityAttachments(Util.makeEnumMap(EntityAttachment.class, attachment -> {
			List<Vec3> list = new ArrayList<>();

			for (Vec3 vec : this.attachments.get(attachment)) {
				list.add(((Vec4) vec).multiply(x, y, z, w));
			}

			return list;
		}));
	}

	// TODO getNullable
	// TODO get
	// TODO getClamped
	// TODO transformPoint

	@Mixin(EntityAttachments.Builder.class)
	static abstract class BuilderMixin implements EntityAttachments4.Builder {
		@Shadow
		public abstract EntityAttachments.Builder attach(EntityAttachment attachment, Vec3 point);

		@Overwrite
		@Deprecated
		public EntityAttachments.Builder attach(EntityAttachment attachment, float x, float y, float z) {
			//noinspection ConstantValue TODO reconsider when 4D renderer entity models
			if (true) {
				return this.attach(attachment, x, y, z, 0F);
			}
			throw Err4.arguments3("EntityAttachments4.Builder#attach");
		}
		@Override
		public EntityAttachments.Builder attach(EntityAttachment attachment, float x, float y, float z, float w) {
			return this.attach(attachment, new Vec4(x, y, z, w));
		}
	}
}
