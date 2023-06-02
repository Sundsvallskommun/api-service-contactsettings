package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Filter model")
public class Filter {

	@Schema(description = "ID of the filter", example = "5d8403b1-1bf0-4cb1-b39e-c7c504d501a1", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "The filter alias", example = "My filter for delegating messages to my friend", requiredMode = REQUIRED)
	private String alias;

	@Schema(description = "Timestamp when filter was created", example = "2020-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime created;

	@Schema(description = "Timestamp when filter was last modified", example = "2020-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime modified;

	@Schema(description = """
		The filter rules.
		If more than one rule exists, there will be an implicit AND-condition between the rules.
		I.e. all rules must evaluate to true in order to pass the filter.""")
	@NotEmpty
	private List<@Valid Rule> rules;

	public static Filter create() {
		return new Filter();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Filter withId(String id) {
		this.id = id;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Filter withAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public Filter withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public Filter withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public Filter withRules(List<Rule> rules) {
		this.rules = rules;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, created, id, modified, rules);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final Filter other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && Objects.equals(created, other.created) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified) && Objects.equals(rules, other.rules);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Filter [id=").append(id).append(", alias=").append(alias).append(", created=").append(created).append(", modified=").append(modified).append(", rules=").append(rules).append("]");
		return builder.toString();
	}
}
