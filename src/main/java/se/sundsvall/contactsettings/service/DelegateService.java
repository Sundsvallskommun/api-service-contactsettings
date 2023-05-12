package se.sundsvall.contactsettings.service;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.mergeIntoDelegateEntity;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toDelegate;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toDelegateEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.DelegateUpdateRequest;
import se.sundsvall.contactsettings.api.model.GetDelegatesParameters;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;
import se.sundsvall.contactsettings.integration.db.model.DelegateEntity;
import se.sundsvall.contactsettings.service.mapper.DelegateMapper;

@Service
@Transactional
public class DelegateService {

	private static final String ERROR_MESSAGE_DELEGATE_NOT_FOUND = "No delegate with id: '%s' exists!";
	private static final String ERROR_MESSAGE_PRINCIPAL_NOT_FOUND = "No principal with contactSettingsId: '%s' exists!";
	private static final String ERROR_MESSAGE_AGENT_NOT_FOUND = "No agent with contactSettingsId: '%s' exists!";
	private static final String ERROR_MESSAGE_DELEGATE_ALREADY_EXIST = "A delegate with this this principal and agent already exists!";

	@Autowired
	private DelegateRepository delegateRepository;

	@Autowired
	private ContactSettingRepository contactSettingRepository;

	public Delegate create(final DelegateCreateRequest delegateCreateRequest) {

		// Verify that agent exists.
		if (!contactSettingRepository.existsById(delegateCreateRequest.getAgentId())) {
			throw Problem.valueOf(NOT_FOUND, String.format(ERROR_MESSAGE_AGENT_NOT_FOUND, delegateCreateRequest.getAgentId()));
		}

		// Verify that principal exists.
		if (!contactSettingRepository.existsById(delegateCreateRequest.getPrincipalId())) {
			throw Problem.valueOf(NOT_FOUND, String.format(ERROR_MESSAGE_PRINCIPAL_NOT_FOUND, delegateCreateRequest.getPrincipalId()));
		}

		// Verify that delegate does not already exists.
		if (!delegateRepository.findByPrincipalIdAndAgentId(delegateCreateRequest.getPrincipalId(), delegateCreateRequest.getAgentId()).isEmpty()) {
			throw Problem.valueOf(CONFLICT, ERROR_MESSAGE_DELEGATE_ALREADY_EXIST);
		}

		// All good: proceed
		return toDelegate(delegateRepository.save(toDelegateEntity(delegateCreateRequest)));
	}

	public Delegate read(final String id) {

		// Fetch/validate
		final var delegateEntity = delegateRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_NOT_FOUND, id)));

		// All good: proceed
		return toDelegate(delegateEntity);
	}

	public Delegate update(final String id, final DelegateUpdateRequest delegateUpdateRequest) {

		// Fetch/validate
		final var delegateEntity = delegateRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_NOT_FOUND, id)));

		// All good: proceed
		return toDelegate(delegateRepository.save(mergeIntoDelegateEntity(delegateEntity, delegateUpdateRequest)));
	}

	public void delete(final String id) {

		// Fetch/validate
		final var entity = delegateRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_MESSAGE_DELEGATE_NOT_FOUND, id)));

		// All good: proceed
		delegateRepository.delete(entity);
	}

	public List<Delegate> find(final GetDelegatesParameters parameters) {
		if (isNull(parameters)) {
			return emptyList();
		}

		final var result = new ArrayList<DelegateEntity>();
		if (allNotNull(parameters.getAgentId(), parameters.getPrincipalId())) {
			result.addAll(delegateRepository.findByPrincipalIdAndAgentId(parameters.getPrincipalId(), parameters.getAgentId()));
		} else if (nonNull(parameters.getAgentId())) {
			result.addAll(delegateRepository.findByAgentId(parameters.getAgentId()));
		} else if (nonNull(parameters.getPrincipalId())) {
			result.addAll(delegateRepository.findByPrincipalId(parameters.getPrincipalId()));
		}

		return result.stream()
			.distinct()
			.map(DelegateMapper::toDelegate)
			.toList();
	}
}
