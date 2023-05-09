package se.sundsvall.contactsettings.api.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.contactsettings.api.validator.ValidGetDelegatesParameters;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@ValidGetDelegatesParameters
@Schema(description = "GetDelegatesParameters model")
public class GetDelegatesParameters {

	@Schema(description = "Agent contact settings ID", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid(nullable = true)
	private String agentId;

	@Schema(description = "Principal contact settings ID", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid(nullable = true)
	private String principalId;

	public static GetDelegatesParameters create() {
		return new GetDelegatesParameters();
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(final String agentId) {
		this.agentId = agentId;
	}

	public GetDelegatesParameters withAgentId(final String agentId) {
		this.agentId = agentId;
		return this;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(final String principalId) {
		this.principalId = principalId;
	}

	public GetDelegatesParameters withPrincipalId(final String principalId) {
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
		if (!(obj instanceof final GetDelegatesParameters other)) {
			return false;
		}
		return Objects.equals(agentId, other.agentId) && Objects.equals(principalId, other.principalId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("GetDelegatesParameters [agentId=").append(agentId).append(", principalId=").append(principalId).append("]");
		return builder.toString();
	}
}
