package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "Delegate model")
public class Delegate {

	@Schema(description = "Unique id for the delegate", example = "0d64c132-3aea-11ec-8d3d-0242ac130003")
	private String id;

	@Schema(description = "Unique id for the delegate principal (owner)", example = "0d64c132-3aea-11ec-8d3d-0242ac130003")
	@NotNull
	@ValidUuid
	private String principalId;

	@Schema(description = "Unique id for the delegate agent", example = "0d64c132-3aea-11ec-8d3d-0242ac130003")
	@NotNull
	@ValidUuid
	private String agentId;

	@Schema(description = "Timestamp when delegate was created", example = "2000-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime created;

	@Schema(description = "Timestamp when delegate was last modified", example = "2000-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime modified;

	@Schema(description = "Filter used by this delegate")
	private Map<String, List<String>> filter;

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

	public Map<String, List<String>> getFilter() {
		return filter;
	}

	public void setFilter(final Map<String, List<String>> filter) {
		this.filter = filter;
	}

	public Delegate withFilter(final Map<String, List<String>> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agentId, created, filter, id, modified, principalId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final Delegate other)) {
			return false;
		}
		return Objects.equals(agentId, other.agentId) && Objects.equals(created, other.created) && Objects.equals(filter, other.filter) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified) && Objects.equals(principalId,
			other.principalId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Delegate [id=").append(id).append(", principalId=").append(principalId).append(", agentId=").append(agentId).append(", created=").append(created).append(", modified=").append(modified).append(", filter=").append(filter).append("]");
		return builder.toString();
	}
}
