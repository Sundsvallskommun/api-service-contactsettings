package se.sundsvall.contactsettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contactsettings.api.model.enums.Operator.EQUALS;

import org.junit.jupiter.api.Test;

class RuleTest {

	@Test
	void testBean() {
		assertThat(Rule.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var attributeName = "attributeName";
		final var attributeValue = "attributeValue";
		final var operator = EQUALS;

		final var bean = Rule.create()
			.withAttributeName(attributeName)
			.withAttributeValue(attributeValue)
			.withOperator(operator);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAttributeName()).isEqualTo(attributeName);
		assertThat(bean.getAttributeValue()).isEqualTo(attributeValue);
		assertThat(bean.getOperator()).isEqualTo(operator);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Rule.create()).hasAllNullFieldsOrProperties();
		assertThat(new Rule()).hasAllNullFieldsOrProperties();
	}
}
