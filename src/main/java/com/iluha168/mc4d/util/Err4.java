package com.iluha168.mc4d.util;

import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class Err4 {
	private static @NonNull RuntimeException err(@NonNull String description) {
		return Util.pauseInIde(new IllegalArgumentException("Detected an attempt to use 3D-only features. " + description));
	}

	public static @NonNull RuntimeException return3(@Nullable String replacementMethod) {
		return err("This method is expected to return a 3D value, which is not wide enough to hold the actual 4D return value."
			+ (replacementMethod == null ? "" : "\nCall "+replacementMethod+" instead.")
		);
	}

	private static @NonNull RuntimeException arguments(@NonNull String description, @Nullable String replacementMethod) {
		return err(description +
			"\nIn the future, expect all methods that fit this description to be banned." +
			(replacementMethod == null
				? "\nThis method does not have a replacement, because it is expected to never get called."
				: "\n\nCall "+replacementMethod+" instead."
			)
		);
	}
	public static @NonNull RuntimeException arguments3(@Nullable String replacementMethod) {
		return arguments("A method that has arguments for only 3 out of 4 spatial dimensions has been called.", replacementMethod);
	}
	public static @NonNull RuntimeException arguments2(@Nullable String replacementMethod) {
		return arguments("A method that has arguments for only 2 out of 3 horizontal dimensions has been called." , replacementMethod);
	}

	public static @NonNull RuntimeException field4missing(@NonNull String ...fieldNames) {
		return err("4D coordinate fields ("+String.join(", ", fieldNames)+") are not set." +
			"\nThis can happen if this object has a 3D-only constructor, and we were forced to delay initialization of the 4D fields." +
			"\nHowever, the initialization has not yet happened at the time of requesting the 4D value.");
	}

	public static @NonNull RuntimeException container3() {
		return err("This method has been called with a dimension abstraction container (e.g. Vec3, AABB), when a custom 4D one (e.g. Vec4, AABB4) was expected." +
			"\nCall this method with custom abstractions instead.");
	}

	public static @NonNull RuntimeException math(String description) {
		return err("This operation cannot be extended to 4D. " + description);
	}
}
