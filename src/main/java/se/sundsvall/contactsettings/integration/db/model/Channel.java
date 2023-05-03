package se.sundsvall.contactsettings.integration.db.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Channel {

	@Column(name = "contact_method")
	private String contactMethod;

	@Column(name = "alias")
	private String alias;

	@Column(name = "destination")
	private String destination;

	@Column(name = "disabled")
	private boolean disabled;

	public static Channel create() {
		return new Channel();
	}

	public String getContactMethod() {
		return contactMethod;
	}

	public void setContactMethod(final String contactMethod) {
		this.contactMethod = contactMethod;
	}

	public Channel withContactMethod(final String contactMethod) {
		this.contactMethod = contactMethod;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public Channel withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
	}

	public Channel withDestination(final String destination) {
		this.destination = destination;
		return this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	public Channel withDisabled(final boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, contactMethod, destination, disabled);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final Channel other)) {
			return false;
		}
		return Objects.equals(alias, other.alias) && (contactMethod == other.contactMethod) && Objects.equals(destination, other.destination) && (disabled == other.disabled);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Channel [contactMethod=").append(contactMethod).append(", alias=").append(alias).append(", destination=").append(destination).append(", disabled=").append(disabled).append("]");
		return builder.toString();
	}
}
