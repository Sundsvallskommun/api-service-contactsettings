package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

@Schema(description = "ContactSettingUpdateRequest model")
public class ContactSettingUpdateRequest {

	@Schema(description = "Alias for this contact setting", examples = "My contact-setting")
	private String alias;

	@Schema(description = "List of contact channels connected to this contact setting")
	private List<@Valid ContactChannel> contactChannels;

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
