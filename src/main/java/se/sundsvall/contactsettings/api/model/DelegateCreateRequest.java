package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "DelegateCreateRequest model")
public class DelegateCreateRequest {

	@Schema(description = "Contact setting ID of the delegate principal (owner)", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", requiredMode = REQUIRED)
	@ValidUuid
	private String principalId;

	@Schema(description = "Contact setting ID of the delegate agent", example = "4a758ca4-6df5-43f4-a7ce-612f51f9da09", requiredMode = REQUIRED)
	@ValidUuid
	private String agentId;

	@Schema(description = "Filters used by this delegate")
	@NotEmpty
	private List<@Valid Filter> filters;

	public static DelegateCreateRequest create() {
		return new DelegateCreateRequest();
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(final String principalId) {
		this.principalId = principalId;
	}

	public DelegateCreateRequest withPrincipalId(final String principalId) {
		this.principalId = principalId;
		return this;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(final String agentId) {
		this.agentId = agentId;
	}

	public DelegateCreateRequest withAgentId(final String agentId) {
		this.agentId = agentId;
		return this;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(final List<Filter> filters) {
		this.filters = filters;
	}

	public DelegateCreateRequest withFilters(final List<Filter> filters) {
		this.filters = filters;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agentId, filters, principalId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final DelegateCreateRequest other)) {
			return false;
		}
		return Objects.equals(agentId, other.agentId) && Objects.equals(filters, other.filters) && Objects.equals(principalId, other.principalId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DelegateCreateRequest [principalId=").append(principalId).append(", agentId=").append(agentId).append(", filters=").append(filters).append("]");
		return builder.toString();
	}
}
