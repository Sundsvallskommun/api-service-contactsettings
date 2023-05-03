package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Schema(description = "Delegate model")
public class Delegate {

	@Schema(description = "Unique id for the contact setting", example = "0d64c132-3aea-11ec-8d3d-0242ac130003")
	private String contactSettingId;

	@Schema(description = "Filter used by this delegate")
	private Map<String, List<String>> filter;

	public static Delegate create() {
		return new Delegate();
	}

	public String getContactSettingId() {
		return contactSettingId;
	}

	public void setContactSettingId(String contactSettingId) {
		this.contactSettingId = contactSettingId;
	}

	public Delegate withContactSettingId(String contactSettingId) {
		this.contactSettingId = contactSettingId;
		return this;
	}

	public Map<String, List<String>> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, List<String>> filter) {
		this.filter = filter;
	}

	public Delegate withFilter(Map<String, List<String>> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contactSettingId, filter);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Delegate)) {
			return false;
		}
		Delegate delegate = (Delegate) o;
		return Objects.equals(contactSettingId, delegate.contactSettingId) && Objects.equals(filter, delegate.filter);
	}

	@Override
	public String toString() {
		return "Delegate [contactSettingId=" + contactSettingId + ", filter=" + filter + "]";
	}
}
