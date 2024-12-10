package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.contactsettings.api.validator.ValidFindDelegatesParameters;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@ValidFindDelegatesParameters
@Schema(description = "FindDelegatesParameters model")
public class FindDelegatesParameters {

	@Schema(description = "Agent contact setting ID", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid(nullable = true)
	private String agentId;

	@Schema(description = "Principal contact setting ID", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid(nullable = true)
	private String principalId;

	public static FindDelegatesParameters create() {
		return new FindDelegatesParameters();
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(final String agentId) {
		this.agentId = agentId;
	}

	public FindDelegatesParameters withAgentId(final String agentId) {
		this.agentId = agentId;
		return this;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(final String principalId) {
		this.principalId = principalId;
	}

	public FindDelegatesParameters withPrincipalId(final String principalId) {
		this.principalId = principalId;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agentId, principalId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final FindDelegatesParameters other)) {
			return false;
		}
		return Objects.equals(agentId, other.agentId) && Objects.equals(principalId, other.principalId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FindDelegatesParameters [agentId=").append(agentId).append(", principalId=").append(principalId).append("]");
		return builder.toString();
	}
}
