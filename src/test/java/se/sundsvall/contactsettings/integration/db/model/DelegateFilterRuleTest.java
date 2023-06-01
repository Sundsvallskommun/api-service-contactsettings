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

class DelegateFilterRuleTest {

	@Test
	void testBean() {
		assertThat(DelegateFilterRule.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void hasValidBuilderMethods() {

		final var attributeName = "attributeName";
		final var attributeValue = "attributeValue";
		final var operator = "operator";

		final var entity = DelegateFilterRule.create()
			.withAttributeName(attributeName)
			.withAttributeValue(attributeValue)
			.withOperator(operator);

		assertThat(entity).hasNoNullFieldsOrProperties();
		assertThat(entity.getAttributeName()).isEqualTo(attributeName);
		assertThat(entity.getAttributeValue()).isEqualTo(attributeValue);
		assertThat(entity.getOperator()).isEqualTo(operator);
	}

	@Test
	void hasNoDirtOnCreatedBean() {
		assertThat(new DelegateFilterRule()).hasAllNullFieldsOrProperties();
		assertThat(DelegateFilterRule.create()).hasAllNullFieldsOrProperties();
	}
}
