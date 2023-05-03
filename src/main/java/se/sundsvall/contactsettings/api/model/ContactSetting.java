package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Contact setting model")
public class ContactSetting {
	@Schema(description = "Unique id for the contact setting", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "Unique id for the person or organization to whom the contact setting applies", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid(nullable = true)
	private String partyId;

	@Schema(description = "Alias for the person or organization to whom the contact setting applies",
			example = "Brorsan")
	private String alias;

	@Schema(description = "Shows if the contact setting is virtual or not",
			example = "false")
	private boolean virtual;

	@Schema(description = "List of contact channels which are connected to the contact setting")
	private List<ContactChannel> contactChannels;

	public static ContactSetting create() {
		return new ContactSetting();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ContactSetting withId(String id) {
		this.id = id;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public ContactSetting withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ContactSetting withAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public boolean isVirtual() {
		return virtual;
	}

	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

	public ContactSetting withVirtual(boolean virtual) {
		this.virtual = virtual;
		return this;
	}

	public List<ContactChannel> getContactChannels() {
		return contactChannels;
	}

	public void setContactChannels(List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
	}

	public ContactSetting withContactChannels(List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, partyId, alias, virtual, contactChannels);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ContactSetting)) {
			return false;
		}
		ContactSetting other = (ContactSetting) obj;
		return Objects.equals(id, other.id) && Objects.equals(partyId, other.partyId)
			&& Objects.equals(alias, other.alias) && virtual == other.virtual
			&& Objects.equals(contactChannels, other.contactChannels);
	}

	@Override
	public String toString() {
		return "ContactSetting [id=" + id + ", partyId=" + partyId + ", alias=" + alias +
			", virtual=" + virtual + ", contactChannels=" + contactChannels + "]";
	}
}
