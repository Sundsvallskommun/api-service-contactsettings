package se.sundsvall.contactsettings.integration.db.model;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import se.sundsvall.contactsettings.integration.db.model.listener.DelegateEntityListener;

@Entity
@Table(name = "delegate")
@EntityListeners(DelegateEntityListener.class)
public class DelegateEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private String id;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "principal_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_delegate_principal_id_contact_setting_id"))
	private ContactSettingEntity principal;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "agent_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_delegate_agent_id_contact_setting_id"))
	private ContactSettingEntity agent;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "delegate_filter",
		joinColumns = @JoinColumn(
			name = "delegate_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_delegate_delegate_filter")))
	private List<Filter> filters;

	@Column(name = "created")
	private OffsetDateTime created;

	@Column(name = "modified")
	private OffsetDateTime modified;

	public static DelegateEntity create() {
		return new DelegateEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public DelegateEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public ContactSettingEntity getPrincipal() {
		return principal;
	}

	public void setPrincipal(final ContactSettingEntity principal) {
		this.principal = principal;
	}

	public DelegateEntity withPrincipal(final ContactSettingEntity principal) {
		this.principal = principal;
		return this;
	}

	public ContactSettingEntity getAgent() {
		return agent;
	}

	public void setAgent(final ContactSettingEntity agent) {
		this.agent = agent;
	}

	public DelegateEntity withAgent(final ContactSettingEntity agent) {
		this.agent = agent;
		return this;
	}

	public Map<String, List<String>> filtersAsMap() {
		return Optional.ofNullable(this.filters).orElse(emptyList()).stream()
			.collect(groupingBy(Filter::getKey, HashMap::new, mapping(Filter::getValue, toList())));
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(final List<Filter> filters) {
		this.filters = filters;
	}

	public DelegateEntity withFilters(final List<Filter> filters) {
		this.filters = filters;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public DelegateEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public DelegateEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agent, created, filters, id, modified, principal);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final DelegateEntity other)) {
			return false;
		}
		return Objects.equals(agent, other.agent) && Objects.equals(created, other.created) && Objects.equals(filters, other.filters) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified) && Objects.equals(principal, other.principal);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DelegateEntity [id=").append(id).append(", principal=").append(principal).append(", agent=").append(agent).append(", filters=").append(filters).append(", created=").append(created).append(", modified=").append(modified).append("]");
		return builder.toString();
	}
}
