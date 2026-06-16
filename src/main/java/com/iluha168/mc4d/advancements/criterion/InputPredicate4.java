package com.iluha168.mc4d.advancements.criterion;

import net.minecraft.advancements.criterion.InputPredicate;

import java.util.Optional;

/**
 * Implemented by {@link net.minecraft.advancements.criterion.InputPredicate}.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface InputPredicate4 {
	Optional<Boolean> ana();
	Optional<Boolean> kata();

	void setAna(Optional<Boolean> ana);
	void setKata(Optional<Boolean> kata);

	static InputPredicate4 as(InputPredicate predicate) {
		return (InputPredicate4) (Object) predicate;
	}

	static InputPredicate from(
		Optional<Boolean> forward,
		Optional<Boolean> backward,
		Optional<Boolean> left,
		Optional<Boolean> right,
		Optional<Boolean> ana,
		Optional<Boolean> kata,
		Optional<Boolean> jump,
		Optional<Boolean> sneak,
		Optional<Boolean> sprint
	) {
		InputPredicate predicate = new InputPredicate(forward, backward, left, right, jump, sneak, sprint);
		InputPredicate4 predicate4 = InputPredicate4.as(predicate);
		predicate4.setAna(ana);
		predicate4.setKata(kata);
		return predicate;
	}
}
