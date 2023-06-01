package se.sundsvall.contactsettings.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static se.sundsvall.contactsettings.api.model.enums.Operator.toEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.api.model.Rule;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateFilterRule;

public class DelegateMapper {

	private DelegateMapper() {}

	public static Delegate toDelegate(final DelegateEntity delegateEntity) {
		if (isNull(delegateEntity)) {
			return null;
		}

		return Delegate.create()
			.withAgentId(Optional.ofNullable(delegateEntity.getAgent()).orElse(ContactSettingEntity.create()).getId())
			.withCreated(delegateEntity.getCreated())
			.withFilters(toFilterList(delegateEntity.getFilters()))
			.withId(delegateEntity.getId())
			.withModified(delegateEntity.getModified())
			.withPrincipalId(Optional.ofNullable(delegateEntity.getPrincipal()).orElse(ContactSettingEntity.create()).getId());
	}

	public static DelegateEntity toDelegateEntity(final DelegateCreateRequest delegateCreateRequest) {
		if (isNull(delegateCreateRequest)) {
			return null;
		}

		return DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(delegateCreateRequest.getAgentId()))
			.withFilters(toDelegateFilterEntityList(delegateCreateRequest.getFilters()))
			.withPrincipal(ContactSettingEntity.create().withId(delegateCreateRequest.getPrincipalId()));
	}

	public static DelegateFilterEntity mergeIntoDelegateFilterEntity(final DelegateFilterEntity existingDelegateFilterEntity, final Filter filter) {
		if (isNull(existingDelegateFilterEntity)) {
			return null;
		}

		Optional.ofNullable(filter).ifPresent(theFilter -> {
			Optional.ofNullable(theFilter.getAlias()).ifPresent(existingDelegateFilterEntity::setAlias);
			Optional.ofNullable(theFilter.getRules()).map(DelegateMapper::toDelegateFilterRuleList).ifPresent(existingDelegateFilterEntity::setFilterRules);
		});

		return existingDelegateFilterEntity;
	}

	/*
	 * From API to DB
	 */

	public static List<DelegateFilterEntity> toDelegateFilterEntityList(final List<Filter> filterList) {
		return Optional.ofNullable(filterList).orElse(emptyList()).stream()
			.map(DelegateMapper::toDelegateFilterEntity)
			.toList();
	}

	public static DelegateFilterEntity toDelegateFilterEntity(final Filter filter) {
		return Optional.ofNullable(filter)
			.map(filterObject -> DelegateFilterEntity.create()
				.withAlias(filterObject.getAlias())
				.withId(filterObject.getId())
				.withFilterRules(toDelegateFilterRuleList(filterObject.getRules())))
			.orElse(null);
	}

	private static List<DelegateFilterRule> toDelegateFilterRuleList(final List<Rule> ruleList) {
		return new ArrayList<>(Optional.ofNullable(ruleList).orElse(emptyList()).stream()
			.map(ruleObject -> DelegateFilterRule.create()
				.withAttributeName(ruleObject.getAttributeName())
				.withAttributeValue(ruleObject.getAttributeValue())
				.withOperator(ruleObject.getOperator().toString()))
			.toList());
	}

	/*
	 * From DB to API
	 */

	public static List<Filter> toFilterList(final List<DelegateFilterEntity> filterEntityList) {
		return Optional.ofNullable(filterEntityList).orElse(emptyList()).stream()
			.map(DelegateMapper::toFilter)
			.toList();
	}

	public static Filter toFilter(final DelegateFilterEntity filterEntity) {
		return Optional.ofNullable(filterEntity)
			.map(filterEntityObject -> Filter.create()
				.withAlias(filterEntityObject.getAlias())
				.withCreated(filterEntityObject.getCreated())
				.withId(filterEntityObject.getId())
				.withModified(filterEntityObject.getModified())
				.withRules(toRuleList(filterEntityObject.getFilterRules())))
			.orElse(null);
	}

	private static List<Rule> toRuleList(final List<DelegateFilterRule> filterRuleList) {
		return Optional.ofNullable(filterRuleList).orElse(emptyList()).stream()
			.map(delegateFilterRule -> Rule.create()
				.withAttributeName(delegateFilterRule.getAttributeName())
				.withAttributeValue(delegateFilterRule.getAttributeValue())
				.withOperator(toEnum(delegateFilterRule.getOperator())))
			.toList();
	}
}
