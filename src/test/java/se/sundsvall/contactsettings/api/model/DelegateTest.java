package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DelegateTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Delegate.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var agentId = "agentId";
		final var created = now();
		final var modified = now();
		final var filter = Map.of("key", List.of("value"));
		final var id = "id";
		final var principalId = "principalId";

		final var bean = Delegate.create()
			.withAgentId(agentId)
			.withCreated(created)
			.withFilter(filter)
			.withId(id)
			.withModified(modified)
			.withPrincipalId(principalId);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAgentId()).isEqualTo(agentId);
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getModified()).isEqualTo(modified);
		assertThat(bean.getFilter()).isEqualTo(filter);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getPrincipalId()).isEqualTo(principalId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Delegate.create()).hasAllNullFieldsOrProperties();
		assertThat(new Delegate()).hasAllNullFieldsOrProperties();
	}
}
