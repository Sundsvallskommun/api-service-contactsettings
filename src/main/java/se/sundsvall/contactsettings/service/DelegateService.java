package se.sundsvall.contactsettings.service;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_AGENT_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_DELEGATE_ALREADY_EXIST;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_DELEGATE_NOT_FOUND;
import static se.sundsvall.contactsettings.service.Constants.ERROR_MESSAGE_PRINCIPAL_NOT_FOUND;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toDelegate;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toDelegateEntity;
import static se.sundsvall.contactsettings.service.mapper.DelegateMapper.toDelegateList;

import java.util.List;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.contactsettings.api.model.Delegate;
import se.sundsvall.contactsettings.api.model.DelegateCreateRequest;
import se.sundsvall.contactsettings.api.model.FindDelegatesParameters;
import se.sundsvall.contactsettings.integration.db.ContactSettingRepository;
import se.sundsvall.contactsettings.integration.db.DelegateRepository;

@Service
public class DelegateService {

	private final DelegateRepository delegateRepository;
	private final ContactSettingRepository contactSettingRepository;

	public DelegateService(DelegateRepository delegateRepository, ContactSettingRepository contactSettingRepository) {
		this.delegateRepository = delegateRepository;
		this.contactSettingRepository = contactSettingRepository;
	}

	public Delegate create(final String municipalityId, final DelegateCreateRequest delegateCreateRequest) {

		// Verifications:
		verifyThatAgentExists(municipalityId, delegateCreateRequest.getAgentId());
		verifyThatPrincipalExists(municipalityId, delegateCreateRequest.getPrincipalId());
		verifyThatDelegateDoesNotAlreadyExist(delegateCreateRequest.getPrincipalId(), delegateCreateRequest.getAgentId());

		// All good: proceed
		return toDelegate(delegateRepository.save(toDelegateEntity(delegateCreateRequest)));
	}

	public Delegate read(final String municipalityId, final String id) {

		// Fetch/validate
		final var entity = delegateRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_DELEGATE_NOT_FOUND.formatted(id)));

		if (!entity.getAgent().getMunicipalityId().equals(municipalityId) || !entity.getPrincipal().getMunicipalityId().equals(municipalityId)) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_DELEGATE_NOT_FOUND.formatted(id));
		}

		// All good: proceed
		return toDelegate(entity);
	}

	public void delete(final String municipalityId, final String id) {

		// Fetch/validate
		final var entity = delegateRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_DELEGATE_NOT_FOUND.formatted(id)));

		if (!entity.getAgent().getMunicipalityId().equals(municipalityId) || !entity.getPrincipal().getMunicipalityId().equals(municipalityId)) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_DELEGATE_NOT_FOUND.formatted(id));
		}

		// All good: proceed
		delegateRepository.delete(entity);
	}

	public List<Delegate> find(final String municipalityId, final FindDelegatesParameters parameters) {
		if (isNull(parameters)) {
			return emptyList();
		}
		if (allNotNull(parameters.getAgentId(), parameters.getPrincipalId())) {
			verifyThatAgentExists(municipalityId, parameters.getAgentId());
			verifyThatPrincipalExists(municipalityId, parameters.getPrincipalId());
			return toDelegateList(delegateRepository.findByPrincipalIdAndAgentId(parameters.getPrincipalId(), parameters.getAgentId()));
		}
		if (nonNull(parameters.getAgentId())) {
			verifyThatAgentExists(municipalityId, parameters.getAgentId());
			return toDelegateList(delegateRepository.findByAgentId(parameters.getAgentId()));
		}
		if (nonNull(parameters.getPrincipalId())) {
			verifyThatPrincipalExists(municipalityId, parameters.getPrincipalId());
			return toDelegateList(delegateRepository.findByPrincipalId(parameters.getPrincipalId()));
		}

		return emptyList();
	}

	private void verifyThatAgentExists(String municipalityId, String agentId) {
		if (!contactSettingRepository.existsByMunicipalityIdAndId(municipalityId, agentId)) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_AGENT_NOT_FOUND.formatted(agentId));
		}
	}

	private void verifyThatPrincipalExists(String municipalityId, String principalId) {
		if (!contactSettingRepository.existsByMunicipalityIdAndId(municipalityId, principalId)) {
			throw Problem.valueOf(NOT_FOUND, ERROR_MESSAGE_PRINCIPAL_NOT_FOUND.formatted(principalId));
		}
	}

	private void verifyThatDelegateDoesNotAlreadyExist(String principalId, String agentId) {
		if (!delegateRepository.findByPrincipalIdAndAgentId(principalId, agentId).isEmpty()) {
			throw Problem.valueOf(CONFLICT, ERROR_MESSAGE_DELEGATE_ALREADY_EXIST);
		}
	}
}
