package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.contactsettings.api.validator.ValidContactChannel;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "Contact setting create request model")
public class ContactSettingCreateRequest {

	@Schema(description = "ID for the person or organization to whom the contact setting applies", example = "15aee472-46ab-4f03-9605-68bd64ebc73f", requiredMode = REQUIRED)
	@ValidUuid
	private String partyId;

	@Schema(description = "ID of the contact setting that created this instance. Applicable for virtual contact settings.", example = "9ca9425e-42cf-4145-a9e7-d77e1ea9e5b0", requiredMode = REQUIRED)
	@ValidUuid(nullable = true)
	private String createdById;

	@Schema(description = "Alias for the person or organization to whom the contact setting applies", example = "Brorsan")
	private String alias;

	@Schema(description = "List of contact channels which are connected to the contact setting")
	private List<@ValidContactChannel ContactChannel> contactChannels;

	public static ContactSettingCreateRequest create() {
		return new ContactSettingCreateRequest();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public ContactSettingCreateRequest withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(final String createdById) {
		this.createdById = createdById;
	}

	public ContactSettingCreateRequest withCreatedById(final String createdById) {
		this.createdById = createdById;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public ContactSettingCreateRequest withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public List<ContactChannel> getContactChannels() {
		return contactChannels;
	}

	public void setContactChannels(final List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
	}

	public ContactSettingCreateRequest withContactChannels(final List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, contactChannels, createdById, partyId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final ContactSettingCreateRequest other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && Objects.equals(contactChannels, other.contactChannels) && Objects.equals(createdById, other.createdById) && Objects.equals(partyId, other.partyId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ContactSettingCreateRequest [partyId=").append(partyId).append(", createdById=").append(createdById).append(", alias=").append(alias).append(", contactChannels=").append(contactChannels).append("]");
		return builder.toString();
	}
}