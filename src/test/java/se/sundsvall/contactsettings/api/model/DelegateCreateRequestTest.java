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

class DelegateCreateRequestTest {

	@Test
	void testBean() {
		assertThat(DelegateCreateRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var agentId = "agentId";
		final var filter = Map.of("key", List.of("value"));
		final var principalId = "principalId";

		final var bean = DelegateCreateRequest.create()
			.withAgentId(agentId)
			.withFilter(filter)
			.withPrincipalId(principalId);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAgentId()).isEqualTo(agentId);
		assertThat(bean.getFilter()).isEqualTo(filter);
		assertThat(bean.getPrincipalId()).isEqualTo(principalId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(DelegateCreateRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new DelegateCreateRequest()).hasAllNullFieldsOrProperties();
	}
}
