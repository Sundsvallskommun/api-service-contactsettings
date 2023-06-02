package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Delegate model", accessMode = READ_ONLY)
public class Delegate {

	@Schema(description = "ID of the delegate", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "Contact setting ID of the delegate principal (owner)", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", accessMode = READ_ONLY)
	private String principalId;

	@Schema(description = "Contact setting ID of the delegate agent", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", accessMode = READ_ONLY)
	private String agentId;

	@Schema(description = "Timestamp when delegate was created", example = "2000-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime created;

	@Schema(description = "Timestamp when delegate was last modified", example = "2000-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime modified;

	@Schema(description = """
		The delegate filters.
		If more than one filter exists, there will be an implicit OR-condition between the filters.
		I.e. at least one filter must evaluate to true in order to delegate anything.
		If the filter list is empty, everything will be delegated.""")
	private List<Filter> filters;

	public static Delegate create() {
		return new Delegate();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Delegate withId(final String id) {
		this.id = id;
		return this;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(final String principalId) {
		this.principalId = principalId;
	}

	public Delegate withPrincipalId(final String principalId) {
		this.principalId = principalId;
		return this;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(final String agentId) {
		this.agentId = agentId;
	}

	public Delegate withAgentId(final String agentId) {
		this.agentId = agentId;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Delegate withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public Delegate withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(final List<Filter> filters) {
		this.filters = filters;
	}

	public Delegate withFilters(final List<Filter> filters) {
		this.filters = filters;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agentId, created, filters, id, modified, principalId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final Delegate other)) {
			return false;
		}
		return Objects.equals(agentId, other.agentId) && Objects.equals(created, other.created) && Objects.equals(filters, other.filters) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified) && Objects.equals(principalId,
			other.principalId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Delegate [id=").append(id).append(", principalId=").append(principalId).append(", agentId=").append(agentId).append(", created=").append(created).append(", modified=").append(modified).append(", filters=").append(filters).append(
			"]");
		return builder.toString();
	}
}
