package se.sundsvall.contactsettings.service.util;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static se.sundsvall.contactsettings.api.model.enums.Operator.toEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

public class FilterEvaluationUtils {

	private FilterEvaluationUtils() {}

	/**
	 * Evaluates the inputQuery against a list of DelegateFilter:s.
	 * At least one of the filters must evaluate to true, for the entire filter list-evaluation to be true (OR-condition).
	 *
	 * If any of the input arguments is empty, then this method will evaluate as true.
	 *
	 * @param inputQuery      the input query.
	 * @param delegateFilters the list of the defined filters.
	 * @return whether the filter matches the query or not.
	 */
	public static boolean evaluateFilters(final Map<String, List<String>> inputQuery, List<DelegateFilterEntity> delegateFilters) {
		if (isEmpty(delegateFilters) || isEmpty(inputQuery)) {
			return true;
		}
		return delegateFilters.stream().anyMatch(delegateFilter -> evaluateFilter(inputQuery, delegateFilter));
	}

	/**
	 * Evaluates the inputQuery against the rules in a single DelegateFilter.
	 *
	 * All rules in the filter must evaluate to true, for the entire filter evaluation to be true (AND-condition).
	 *
	 * @param inputQuery     the input query.
	 * @param delegateFilter the defined filter.
	 * @return whether the filter matches the query or not.
	 */
	private static boolean evaluateFilter(final Map<String, List<String>> inputQuery, DelegateFilterEntity delegateFilter) {
		return Optional.ofNullable(delegateFilter.getFilterRules()).orElse(emptyList()).stream()
			.allMatch(rule -> switch (toEnum(rule.getOperator()))
			{
				case EQUALS -> equalsEvaluation(rule, inputQuery);
				case NOT_EQUALS -> notEqualsEvaluation(rule, inputQuery);
			});
	}

	private static boolean equalsEvaluation(DelegateFilterRule rule, Map<String, List<String>> inputQuery) {
		return Optional.ofNullable(inputQuery.get(rule.getAttributeName())).orElse(emptyList()).stream()
			.anyMatch(queryStringValue -> equalsIgnoreCase(queryStringValue, rule.getAttributeValue()));
	}

	private static boolean notEqualsEvaluation(DelegateFilterRule rule, Map<String, List<String>> inputQuery) {
		return !equalsEvaluation(rule, inputQuery);
	}
}
