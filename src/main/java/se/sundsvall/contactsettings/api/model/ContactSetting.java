package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contact setting model", accessMode = READ_ONLY)
public class ContactSetting {

	@Schema(description = "ID for the contact setting", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "ID for the person or organization to whom the contact setting applies", example = "15aee472-46ab-4f03-9605-68bd64ebc73f", accessMode = READ_ONLY)
	private String partyId;

	@Schema(description = "ID of the contact setting that created this instance. Applicable for virtual contact settings.", example = "9ca9425e-42cf-4145-a9e7-d77e1ea9e5b0", accessMode = READ_ONLY)
	private String createdById;

	@Schema(description = "Alias for the person or organization to whom the contact setting applies", example = "Brorsan", accessMode = READ_ONLY)
	private String alias;

	@Schema(description = "Shows if the contact setting is virtual or not. A virtual instance doesn't have a partyId (i.e. doesn't have a direct relation to a real person/organization)", example = "false", accessMode = READ_ONLY)
	private boolean virtual;

	@Schema(description = "List of contact channels which are connected to the contact setting", accessMode = READ_ONLY)
	private List<ContactChannel> contactChannels;

	@Schema(description = "Timestamp when delegate was created", example = "2000-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime created;

	@Schema(description = "Timestamp when delegate was last modified", example = "2000-10-31T01:30:00.000+02:00", accessMode = READ_ONLY)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime modified;

	public static ContactSetting create() {
		return new ContactSetting();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public ContactSetting withId(final String id) {
		this.id = id;
		return this;
	}

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(final String createdById) {
		this.createdById = createdById;
	}

	public ContactSetting withCreatedById(final String createdById) {
		this.createdById = createdById;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public ContactSetting withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public ContactSetting withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public ContactSetting withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public ContactSetting withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public boolean isVirtual() {
		return virtual;
	}

	public void setVirtual(final boolean virtual) {
		this.virtual = virtual;
	}

	public ContactSetting withVirtual(final boolean virtual) {
		this.virtual = virtual;
		return this;
	}

	public List<ContactChannel> getContactChannels() {
		return contactChannels;
	}

	public void setContactChannels(final List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
	}

	public ContactSetting withContactChannels(final List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, contactChannels, created, createdById, id, modified, partyId, virtual);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final ContactSetting other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && Objects.equals(contactChannels, other.contactChannels) && Objects.equals(created, other.created) && Objects.equals(createdById, other.createdById) && Objects.equals(id, other.id) && Objects.equals(
			modified, other.modified) && Objects.equals(partyId, other.partyId) && (virtual == other.virtual);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ContactSetting [id=").append(id).append(", partyId=").append(partyId).append(", createdById=").append(createdById).append(", alias=").append(alias).append(", virtual=").append(virtual).append(", contactChannels=").append(
			contactChannels).append(", created=").append(created).append(", modified=").append(modified).append("]");
		return builder.toString();
	}
}
