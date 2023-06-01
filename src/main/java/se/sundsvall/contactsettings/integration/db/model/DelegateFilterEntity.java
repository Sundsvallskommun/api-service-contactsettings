package se.sundsvall.contactsettings.integration.db.model;

import static java.util.Objects.isNull;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import se.sundsvall.contactsettings.integration.db.model.listener.DelegateFilterEntityListener;

@Entity
@Table(name = "delegate_filter")
@EntityListeners(DelegateFilterEntityListener.class)
public class DelegateFilterEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "alias")
	private String alias;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "delegate_filter_rule",
		joinColumns = @JoinColumn(
			name = "delegate_filter_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_delegate_filter_delegate_filter_rule")))
	private List<DelegateFilterRule> filterRules;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime modified;

	public static DelegateFilterEntity create() {
		return new DelegateFilterEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DelegateFilterEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public DelegateFilterEntity withAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public List<DelegateFilterRule> getFilterRules() {
		return filterRules;
	}

	public void setFilterRules(List<DelegateFilterRule> filterRules) {

		if (isNull(this.filterRules)) {
			this.filterRules = new ArrayList<>();
		}

		this.filterRules.clear();

		if (filterRules != null) {
			this.filterRules.addAll(filterRules);
		}
	}

	public DelegateFilterEntity withFilterRules(List<DelegateFilterRule> filterRules) {
		this.setFilterRules(filterRules);
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public DelegateFilterEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public DelegateFilterEntity withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, created, filterRules, id, modified);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final DelegateFilterEntity other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && Objects.equals(created, other.created) && Objects.equals(filterRules, other.filterRules) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DelegateFilterEntity [id=").append(id).append(", alias=").append(alias).append(", filterRules=").append(filterRules).append(", created=").append(created).append(", modified=").append(modified).append("]");
		return builder.toString();
	}
}
