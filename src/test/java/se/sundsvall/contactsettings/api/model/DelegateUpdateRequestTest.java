package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DelegateUpdateRequestTest {

	@Test
	void testBean() {
		assertThat(DelegateUpdateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var filter = Map.of("key", List.of("value"));
		final var bean = DelegateUpdateRequest.create()
			.withFilter(filter);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getFilter()).isEqualTo(filter);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(DelegateUpdateRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new DelegateUpdateRequest()).hasAllNullFieldsOrProperties();
	}
}
