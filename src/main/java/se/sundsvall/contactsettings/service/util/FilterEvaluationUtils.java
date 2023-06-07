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
	 * At least one filter in the filter list must evaluate to true, for the entire evaluation to be true (OR-condition).
	 *
	 * If any of the input arguments are empty, this method will evaluate to true.
	 *
	 * @param  inputQuery               the input query.
	 * @param  delegateFilterEntityList the list of the defined filters.
	 * @return                          whether the filter matches the query or not.
	 */
	public static boolean evaluate(final Map<String, List<String>> inputQuery, List<DelegateFilterEntity> delegateFilterEntityList) {
		if (isEmpty(delegateFilterEntityList) || isEmpty(inputQuery)) {
			return true;
		}

		return delegateFilterEntityList.stream()
			.anyMatch(delegateFilterEntity -> evaluate(inputQuery, delegateFilterEntity));
	}

	/**
	 * Evaluates the inputQuery against the rules in a single DelegateFilter.
	 *
	 * All rules in the filter must evaluate to true, for the entire evaluation to be true (AND-condition).
	 *
	 * @param  inputQuery           the input query.
	 * @param  delegateFilterEntity the defined filter entity.
	 * @return                      whether the filter matches the query or not.
	 */
	private static boolean evaluate(final Map<String, List<String>> inputQuery, DelegateFilterEntity delegateFilterEntity) {
		return Optional.ofNullable(delegateFilterEntity.getFilterRules()).orElse(emptyList()).stream()
			.allMatch(rule -> switch (toEnum(rule.getOperator()))
			{
				case EQUALS -> equalsEvaluation(inputQuery, rule);
				case NOT_EQUALS -> !equalsEvaluation(inputQuery, rule);
			});
	}

	private static boolean equalsEvaluation(Map<String, List<String>> inputQuery, DelegateFilterRule rule) {
		return Optional.ofNullable(inputQuery.get(rule.getAttributeName())).orElse(emptyList()).stream()
			.anyMatch(queryStringValue -> equalsIgnoreCase(queryStringValue, rule.getAttributeValue()));
	}
}
