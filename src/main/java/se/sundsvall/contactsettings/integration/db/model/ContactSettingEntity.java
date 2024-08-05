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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import se.sundsvall.contactsettings.integration.db.model.listener.ContactSettingEntityListener;

@Entity
@Table(name = "contact_setting",
	indexes = {
		@Index(name = "contact_setting_party_id_index", columnList = "party_id"),
		@Index(name = "contact_setting_created_by_id_index", columnList = "created_by_id"),
		@Index(name = "contact_setting_municipality_id_index", columnList = "municipality_id"),
	})
@EntityListeners(ContactSettingEntityListener.class)
public class ContactSettingEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "alias")
	private String alias;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime modified;

	@Column(name = "created_by_id")
	private String createdById;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "contact_setting_channel",
		indexes = {
			@Index(name = "contact_setting_channel_destination_index", columnList = "destination")
		},
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

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public ContactSettingEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
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

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(final String createdById) {
		this.createdById = createdById;
	}

	public ContactSettingEntity withCreatedById(final String createdById) {
		this.createdById = createdById;
		return this;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(final List<Channel> channels) {

		if (isNull(this.channels)) {
			this.channels = new ArrayList<>();
		}

		this.channels.clear();

		if (channels != null) {
			this.channels.addAll(channels);
		}
	}

	public ContactSettingEntity withChannels(final List<Channel> channels) {
		this.setChannels(channels);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, channels, created, createdById, id, modified, municipalityId, partyId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof final ContactSettingEntity other)) { return false; }
		return Objects.equals(alias, other.alias) && Objects.equals(channels, other.channels) && Objects.equals(created, other.created) && Objects.equals(createdById, other.createdById) && Objects.equals(id, other.id) && Objects.equals(modified,
			other.modified) && Objects.equals(municipalityId, other.municipalityId) && Objects.equals(partyId, other.partyId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ContactSettingEntity [id=").append(id).append(", partyId=").append(partyId).append(", municipalityId=").append(municipalityId).append(", alias=").append(alias).append(", created=").append(created).append(", modified=").append(
			modified).append(", createdById=").append(createdById).append(", channels=").append(channels).append("]");
		return builder.toString();
	}
}
