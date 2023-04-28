package se.sundsvall.contactsettings.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

@Schema(description = "Filter model")
public class Filter {

	@Schema(description = "Unique key for the filter", example = "categories")
	private String key;

	@ArraySchema(schema = @Schema(description = "List of values for the filter", example = "[\"electricity\", \"broadband\"]", implementation = String.class))
	private List<String> values;

	public static Filter create() {
		return new Filter();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Filter withKey(String key) {
		this.key = key;
		return this;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Filter withValues(List<String> values) {
		this.values = values;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, values);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Filter)) {
			return false;
		}
		Filter filter = (Filter) o;
		return Objects.equals(key, filter.key) && Objects.equals(values, filter.values);
	}

	@Override
	public String toString() {
		return "Filter [key=" + key + ", values=" + values + "]";
	}
}
