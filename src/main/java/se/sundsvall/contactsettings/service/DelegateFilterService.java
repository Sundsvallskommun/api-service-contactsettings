package se.sundsvall.contactsettings.service;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_DELEGATE_FILTER_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_DELEGATE_NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.mergeIntoDelegateFilterEntity;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toDelegateFilterEntity;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toFilter;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.contactsettings.api.model.Filter;
import se.sundsvall.contactsettings.integration.db.DelegateFilterRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;

@Service
public class DelegateFilterService {

	@Autowired
	private DelegateRepository delegateRepository;

	@Autowired
	private DelegateFilterRepository delegateFilterRepository;

	public Filter create(String delegateId, Filter filter) {

		// Fetch/Validate.
		final var delegateEntity = delegateRepository.findById(delegateId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_NOT_FOUND, delegateId)));

		// Save delegateFilter.
		final var delegateFilterEntity = delegateFilterRepository.save(toDelegateFilterEntity(filter));

		// Add delegateFilter to delegate.
		final var delegateFilters = new ArrayList<>(Optional.ofNullable(delegateEntity.getFilters()).orElse(emptyList()).stream().toList());
		delegateFilters.add(delegateFilterEntity);
		delegateRepository.save(delegateEntity.withFilters(delegateFilters));

		return toFilter(delegateFilterEntity);
	}

	public Filter read(String delegateId, String delegateFilterId) {

		// Fetch/validate
		final var delegateFilterEntity = delegateFilterRepository.findByIdAndDelegateId(delegateFilterId, delegateId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_FILTER_NOT_FOUND, delegateId, delegateFilterId)));

		// All good: proceed
		return toFilter(delegateFilterEntity);
	}

	public Filter update(String delegateId, String delegateFilterId, Filter filter) {

		// Fetch/validate
		final var delegateFilterEntity = delegateFilterRepository.findByIdAndDelegateId(delegateFilterId, delegateId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_FILTER_NOT_FOUND, delegateId, delegateFilterId)));

		// All good: proceed
		return toFilter(delegateFilterRepository.save(mergeIntoDelegateFilterEntity(delegateFilterEntity, filter)));
	}

	public void delete(String delegateId, String delegateFilterId) {

		// Validate
		if (!delegateFilterRepository.existsByIdAndDelegateId(delegateFilterId, delegateId)) {
			throw Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_FILTER_NOT_FOUND, delegateId, delegateFilterId));
		}

		// Delete delegate if the filter is the last filter on the delegate, otherwise.
		if (delegateFilterRepository.countByDelegateId(delegateId) <= 1) {
			delegateRepository.deleteById(delegateId);
			return;
		}

		// More filters exist on delegate, only delete the filter.
		delegateFilterRepository.deleteById(delegateFilterId);
	}
}
