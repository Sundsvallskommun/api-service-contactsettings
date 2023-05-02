package se.sundsvall.contactsettings.integration.db.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
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

	@Column(name = "filter")
	private String filter;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "principal_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_delegate_principal_id_contact_setting_id"))
	private ContactSettingEntity principal;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "agent_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_delegate_agent_id_contact_setting_id"))
	private ContactSettingEntity agent;

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

	public String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}

	public DelegateEntity withFilter(final String filter) {
		this.filter = filter;
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
		return Objects.hash(agent, created, filter, id, modified, principal);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final DelegateEntity other)) {
			return false;
		}
		return Objects.equals(agent, other.agent) && Objects.equals(created, other.created) && Objects.equals(filter, other.filter) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified) && Objects.equals(principal, other.principal);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DelegateEntity [id=").append(id).append(", filter=").append(filter).append(", principal=").append(principal).append(", agent=").append(agent).append(", created=").append(created).append(", modified=").append(modified).append("]");
		return builder.toString();
	}
}
