package se.sundsvall.contactsettings.api.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Delegate update request model")
public class DelegateUpdateRequest {

	@Schema(description = "Filter used by this delegate")
	private Map<String, List<String>> filter;

	public static DelegateUpdateRequest create() {
		return new DelegateUpdateRequest();
	}

	public Map<String, List<String>> getFilter() {
		return filter;
	}

	public void setFilter(final Map<String, List<String>> filter) {
		this.filter = filter;
	}

	public DelegateUpdateRequest withFilter(final Map<String, List<String>> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(filter);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final DelegateUpdateRequest other)) {
			return false;
		}
		return Objects.equals(filter, other.filter);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DelegateUpdateRequest [filter=").append(filter).append("]");
		return builder.toString();
	}
}
