package se.sundsvall.contactsettings.integration.db.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import se.sundsvall.contactsettings.integration.db.model.listener.ContactSettingEntityListener;

@Entity
@Table(name = "contact_setting",
	indexes = {
		@Index(name = "contact_setting_party_id_index", columnList = "party_id")
	})
@EntityListeners(ContactSettingEntityListener.class)
public class ContactSettingEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private String id;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "alias")
	private String alias;

	@Column(name = "created")
	private OffsetDateTime created;

	@Column(name = "modified")
	private OffsetDateTime modified;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "contact_setting_channel",
		joinColumns = @JoinColumn(
			name = "contact_setting_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_contact_setting_contact_setting_channel")))
	private List<Channel> channels;

	public static ContactSettingEntity create() {
		return new ContactSettingEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ContactSettingEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public ContactSettingEntity withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public ContactSettingEntity withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public ContactSettingEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public ContactSettingEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(final List<Channel> channels) {
		this.channels = channels;
	}

	public ContactSettingEntity withChannels(final List<Channel> channels) {
		this.channels = channels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, channels, created, id, modified, partyId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final ContactSettingEntity other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && Objects.equals(channels, other.channels) && Objects.equals(created, other.created) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified) && Objects.equals(partyId,
			other.partyId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ContactSettingEntity [id=").append(id).append(", partyId=").append(partyId).append(", alias=").append(alias).append(", created=").append(created).append(", modified=").append(modified).append(", channels=").append(
			channels).append("]");
		return builder.toString();
	}
}
