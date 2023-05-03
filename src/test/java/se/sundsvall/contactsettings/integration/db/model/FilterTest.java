package se.sundsvall.contactsettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.jupiter.api.Test;

class FilterTest {

	@Test
	void testBean() {
		assertThat(Filter.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var key = "key";
		final var value = "value";

		final var entity = Filter.create()
			.withKey(key)
			.withValue(value);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getKey()).isEqualTo(key);
		assertThat(entity.getValue()).isEqualTo(value);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(new Filter()).hasAllNullFieldsOrProperties();
		assertThat(Filter.create()).hasAllNullFieldsOrProperties();
	}
}
