package se.sundsvall.contactsettings.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

class FilterEvaluationUtilsTest {

	@ParameterizedTest
	@MethodSource("filterMatchesArgumentsProvider")
	void filterMatches(String description, Map<String, List<String>> inputFilter, List<DelegateFilterEntity> delegateFilters, boolean expectedMatch) {

		// Act
		final var result = FilterEvaluationUtils.evaluateFilters(inputFilter, delegateFilters);

		// Assert
		assertThat(result).as(description).isEqualTo(expectedMatch);
	}

	private static Stream<Arguments> filterMatchesArgumentsProvider() {
		return Stream.of(

			Arguments.of(
				"Match with a single EQUALS-rule",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2")))), true),

			Arguments.of(
				"Match with a single EQUALS-rule. Case insensitive",
				Map.of(
					"key1", List.of("VaLuE1", "VALUE2"),
					"key2", List.of("value3", "Value4", "vaLue5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value1"),
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value3"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("VALUE5")))), true),

			Arguments.of(
				"Match with multiple EQUALS-rules in one filter",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value5")))), true),

			Arguments.of(
				"Match with multiple EQUALS-rules in multiple filters",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value1"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value3"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value5")))), true),

			Arguments.of(
				"No-match with a single EQUALS-rule",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value666")))), false),

			Arguments.of(
				"No-match with multiple EQUALS-rules in one filter, where one rule-condition is not fulfilled",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value-no-match")))), false),

			Arguments.of(
				"Match with multiple filters where one filter evaluates to false and another to true (OR-condition)",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value1"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value3"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value-no-match")))), true),

			Arguments.of(
				"Match with a single NOT_EQUALS-rule",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match")))), true),

			Arguments.of(
				"Match with multiple NOT_EQUALS-rules in one filter",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match1"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match3")))), true),

			Arguments.of(
				"Match with multiple NOT_EQUALS-rules in multiple filters",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match1"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match2"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match3"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match5")))), true),

			Arguments.of(
				"No-match with a single NOT_EQUALS-rule",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value1")))), false),

			Arguments.of(
				"No-match with multiple NOT_EQUALS-rules in one filter, where one rule-condition is not fulfilled",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match1"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match2")))), false),

			Arguments.of(
				"Match with multiple NOT_EQUALS-rules in multiple filters where one filter evaluates to false and another to true (OR-condition)",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value4"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match")))), true),

			Arguments.of(
				"Match with mixed EQUALS and NOT_EQUALS-rules in multiple filters",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value4")))), true),

			Arguments.of(
				"Match with mixed EQUALS and NOT_EQUALS-rules in multiple filters, where one rule-condition in one filter is not fulfilled (OR-condition)",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value2"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value-no-match")))), true),

			Arguments.of(
				"No match with mixed EQUALS and NOT_EQUALS-rules in multiple filters.",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value-no-match")))), false),

			Arguments.of(
				"Match if input query is empty",
				Map.of(),
				List.of(
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"))),
					DelegateFilterEntity.create().withFilterRules(List.of(
						DelegateFilterRule.create().withAttributeName("key1").withOperator("NOT_EQUALS").withAttributeValue("value-no-match"),
						DelegateFilterRule.create().withAttributeName("key2").withOperator("EQUALS").withAttributeValue("value-no-match")))), true),

			Arguments.of(
				"Match if delegateFilters is empty",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(), true),

			Arguments.of(
				"Match if delegateFilters is not empty, but the rules are empty",
				Map.of(
					"key1", List.of("value1", "value2"),
					"key2", List.of("value3", "value4", "value5")),
				List.of(
					DelegateFilterEntity.create(),
					DelegateFilterEntity.create()), true),

			Arguments.of(
				"Match if both input query and delegateFilters is empty", Map.of(), List.of(), true),

			Arguments.of(
				"Match if both input query and delegateFilters is null", null, null, true)

		);
	}
}
