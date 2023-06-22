package se.sundsvall.contactsettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import se.sundsvall.contactsettings.api.model.enums.Operator;

@Schema(description = """
	The rule model.

	If you want to write a rule that accepts everything, you can use the special MATCH_ALL_RULE:
	{
	   "attributeName": "*",
	   "operator": "EQUALS",
	   "attributeValue": "*"
	}
	""")
public class Rule {

	@Schema(description = "The attribute name to apply the filter rule on", example = "facilityId", requiredMode = REQUIRED)
	@NotBlank
	private String attributeName;

	@Schema(description = "The rule operator", example = "EQUALS", requiredMode = REQUIRED)
	@NotNull
	private Operator operator;

	@Schema(description = "The attribute value to apply the filter rule on", example = "12345678", requiredMode = REQUIRED)
	@NotBlank
	private String attributeValue;

	public static Rule create() {
		return new Rule();
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Rule withAttributeName(String attributeName) {
		this.attributeName = attributeName;
		return this;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Rule withOperator(Operator operator) {
		this.operator = operator;
		return this;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public Rule withAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(attributeName, operator, attributeValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final Rule other)) {
			return false;
		}
		return Objects.equals(attributeName, other.attributeName) && (operator == other.operator) && Objects.equals(attributeValue, other.attributeValue);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FilterRule [attributeName=").append(attributeName).append(", operator=").append(operator).append(", attributeValue=").append(attributeValue).append("]");
		return builder.toString();
	}
}
