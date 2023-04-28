package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import java.util.List;
import java.util.Objects;

@Schema(description = "Get contact settings request model")
public class GetContactSettingsParameters {
	@Schema(description = "Unique id for the person or organization to whom the contact setting applies", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid
	private String partyId;

	@Schema(description = "Key of filters for the contact settings", example = "category")
	private String filterKey;

	@Schema(description = "Filter for the contact settings")
	private List<String> filter;

	public static GetContactSettingsParameters create() {
		return new GetContactSettingsParameters();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public GetContactSettingsParameters withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(String filterKey) {
		this.filterKey = filterKey;
	}

	public GetContactSettingsParameters withFilterKey(String filterKey) {
		this.filterKey = filterKey;
		return this;
	}

	public List<String> getFilter() {
		return filter;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	public GetContactSettingsParameters withFilter(List<String> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(partyId, filterKey, filter);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof GetContactSettingsParameters)) {
			return false;
		}
		GetContactSettingsParameters getContactSettingsParameters = (GetContactSettingsParameters) o;
		return Objects.equals(partyId, getContactSettingsParameters.partyId) && Objects.equals(filterKey, getContactSettingsParameters.filterKey) && Objects.equals(filter, getContactSettingsParameters.filter);
	}

	@Override
	public String toString() {
		return "GetContactSettingsParameters [" +
			", partyId=" + partyId +
			", filterKey=" + filterKey +
			", filter=" + filter +
			"]";
	}
}
