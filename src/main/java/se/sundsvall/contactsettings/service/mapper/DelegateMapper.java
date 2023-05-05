package se.sundsvall.contactsettings.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import se.sundsvall.contactsettings.api.model.Delegate;
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

	public static DelegateEntity toDelegateEntity(final Delegate delegate) {
		if (isNull(delegate)) {
			return null;
		}

		return DelegateEntity.create()
			.withAgent(ContactSettingEntity.create().withId(delegate.getAgentId()))
			.withFilters(toListFilter(delegate.getFilter()))
			.withId(delegate.getId())
			.withPrincipal(ContactSettingEntity.create().withId(delegate.getPrincipalId()));
	}

	private static Map<String, List<String>> toMapFilter(final List<Filter> listFilter) {
		return Optional.ofNullable(listFilter).orElse(emptyList()).stream()
			.collect(groupingBy(Filter::getKey, HashMap::new, mapping(Filter::getValue, toList())));
	}

	private static List<Filter> toListFilter(final Map<String, List<String>> mapFilter) {
		return Optional.ofNullable(mapFilter).orElse(emptyMap()).entrySet().stream()
			.flatMap(entry -> entry.getValue().stream()
				.map(value -> Filter.create().withKey(entry.getKey()).withValue(value)))
			.toList();
	}
}
