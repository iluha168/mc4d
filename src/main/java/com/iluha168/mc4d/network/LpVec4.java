package com.iluha168.mc4d.network;

import com.iluha168.mc4d.world.phys.Vec4;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.LpVec3;
import org.jspecify.annotations.NonNull;

/*
 TODO There is some insane compression logic in vanilla specifically tailored to Vec3. Do something like this for Vec4?
 */
public class LpVec4 extends LpVec3 {
	public static @NonNull Vec4 read(@NonNull ByteBuf input) {
		return new Vec4(
			input.readDouble(),
			input.readDouble(),
			input.readDouble(),
			input.readDouble()
		);
	}

	public static void write(@NonNull ByteBuf output, @NonNull Vec4 value) {
		output.writeDouble(value.x);
		output.writeDouble(value.y);
		output.writeDouble(value.z);
		output.writeDouble(value.w);
	}
}
