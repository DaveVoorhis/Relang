package org.reldb.relang.values;

import org.junit.jupiter.api.Test;
import org.reldb.relang.values.ValueBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class ValueBooleanTest {
	final ValueBoolean t = new ValueBoolean(true);
	final ValueBoolean f = new ValueBoolean(false);

	@Test
	void valueTrueIsJavaTrue() {
		assertThat(t.booleanValue()).isTrue();
	}

	@Test
	void valueFalseIsJavaFalse() {
		assertThat(f.booleanValue()).isFalse();
	}

	@Test
	void notTrueIsFalse() {
		assertThat(t.not().eq(f).booleanValue()).isTrue();
	}

	@Test
	void notTrueIsJavaEqualToFalse() {
		assertThat(t.not().booleanValue()).isFalse();
	}

	@Test
	void trueIsTrue() {
		assertThat(t).isEqualTo(t);
	}

	@Test
	void falseIsFalse() {
		assertThat(f).isEqualTo(f);
	}

	@Test
	void trueIsGreaterThanFalse() {
		assertThat(t.gt(f).booleanValue()).isTrue();
	}
}
