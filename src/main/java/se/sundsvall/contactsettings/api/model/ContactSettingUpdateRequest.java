package se.sundsvall.contactsettings.api.model;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.contactsettings.api.validator.ValidContactChannel;

@Schema(description = "Contact setting update request model")
public class ContactSettingUpdateRequest {

	@Schema(description = "Alias for this contact setting", example = "My contact-settings")
	private String alias;

	@Schema(description = "List of contact channels which are connected to the contact setting")
	private List<@ValidContactChannel ContactChannel> contactChannels;

	public static ContactSettingUpdateRequest create() {
		return new ContactSettingUpdateRequest();
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public ContactSettingUpdateRequest withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public List<ContactChannel> getContactChannels() {
		return contactChannels;
	}

	public void setContactChannels(final List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
	}

	public ContactSettingUpdateRequest withContactChannels(final List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, contactChannels);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final ContactSettingUpdateRequest other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && Objects.equals(contactChannels, other.contactChannels);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ContactSettingUpdateRequest [alias=").append(alias).append(", contactChannels=").append(contactChannels).append("]");
		return builder.toString();
	}
}
