package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Schema(description = "Get contact settings request model")
public class GetContactSettingsParameters {
	@Schema(description = "Unique id for the person or organization to whom the contact setting applies", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid
	private String partyId;

	@Schema(description = "Filters for the contact settings")
	private Map<String, List<String>> filter;

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

	public Map<String, List<String>> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, List<String>> filter) {
		this.filter = filter;
	}

	public GetContactSettingsParameters withFilter(Map<String, List<String>> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(partyId, filter);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof GetContactSettingsParameters)) {
			return false;
		}
		GetContactSettingsParameters getContactSettingsParameters = (GetContactSettingsParameters) o;
		return Objects.equals(partyId, getContactSettingsParameters.partyId) && Objects.equals(filter, getContactSettingsParameters.filter);
	}

	@Override
	public String toString() {
		return "GetContactSettingsParameters [" +
			", partyId=" + partyId +
			", filter=" + filter +
			"]";
	}
}
