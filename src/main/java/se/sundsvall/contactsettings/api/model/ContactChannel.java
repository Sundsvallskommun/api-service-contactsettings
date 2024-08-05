package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;

@Schema(description = "ContactChannel model")
public class ContactChannel {

	@Schema(description = "Method of contact", requiredMode = REQUIRED)
	@NotNull
	private ContactMethod contactMethod;

	@Schema(description = "Alias for the destination", example = "Private phone", requiredMode = REQUIRED)
	@NotBlank
	private String alias;

	@Schema(description = "Point of destination", example = "+46701234567", requiredMode = REQUIRED)
	@NotBlank
	private String destination;

	@Schema(description = "Signal if channel should be used or not when sending message", example = "true", defaultValue = "false", requiredMode = NOT_REQUIRED)
	private boolean disabled;

	public static ContactChannel create() {
		return new ContactChannel();
	}

	public ContactMethod getContactMethod() {
		return contactMethod;
	}

	public void setContactMethod(final ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
	}

	public ContactChannel withContactMethod(final ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public ContactChannel withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
	}

	public ContactChannel withDestination(final String destination) {
		this.destination = destination;
		return this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	public ContactChannel withDisabled(final boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contactMethod, alias, destination, disabled);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof final ContactChannel other)) {
			return false;
		}

		return Objects.equals(contactMethod, other.contactMethod) && Objects.equals(alias, other.alias) && Objects.equals(destination, other.destination) && (disabled == other.disabled);
	}

	@Override
	public String toString() {
		return "ContactChannel [contactMethod=" + contactMethod + ", alias=" + alias + ", destination=" + destination + ", disabled=" + disabled + "]";
	}
}
