package se.sundsvall.contactsettings.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.DelegateUpdateRequest;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.integration.db.model.Filter;

public class DelegateMapper {

	private DelegateMapper() {}

	public static Delegate toDelegate(final DelegateEntity delegateEntity) {
		if (isNull(delegateEntity)) {
			return null;
		}

		return Delegate.create()
			.withAgentId(Optional.ofNullable(delegateEntity.getAgent()).orElse(ContactSettingEntity.create()).getId())
			.withCreated(delegateEntity.getCreated())
			.withFilter(toMapFilter(delegateEntity.getFilters()))
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
			.withFilters(toListFilter(delegateCreateRequest.getFilter()))
			.withPrincipal(ContactSettingEntity.create().withId(delegateCreateRequest.getPrincipalId()));
	}

	public static DelegateEntity mergeIntoDelegateEntity(final DelegateEntity existingDelegateEntity, final DelegateUpdateRequest delegateUpdateRequest) {
		if (isNull(existingDelegateEntity)) {
			return null;
		}

		Optional.ofNullable(delegateUpdateRequest.getFilter())
			.ifPresent(filter -> existingDelegateEntity.setFilters(toListFilter(filter)));

		return existingDelegateEntity;
	}

	private static Map<String, List<String>> toMapFilter(final List<Filter> listFilter) {
		return Optional.ofNullable(listFilter).orElse(emptyList()).stream()
			.collect(groupingBy(Filter::getKey, HashMap::new, mapping(Filter::getValue, toList())));
	}

	private static List<Filter> toListFilter(final Map<String, List<String>> mapFilter) {
		return new ArrayList<>(Optional.ofNullable(mapFilter).orElse(emptyMap()).entrySet().stream()
			.flatMap(entry -> entry.getValue().stream()
				.map(value -> Filter.create().withKey(entry.getKey()).withValue(value)))
			.toList());
	}
}
