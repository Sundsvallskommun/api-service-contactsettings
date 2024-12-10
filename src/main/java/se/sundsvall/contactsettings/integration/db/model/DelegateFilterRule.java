package se.sundsvall.contactsettings.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class DelegateFilterRule {

	@Column(name = "attribute_name")
	private String attributeName;

	@Column(name = "attribute_value")
	private String attributeValue;

	@Column(name = "operator")
	private String operator;

	public static DelegateFilterRule create() {
		return new DelegateFilterRule();
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public DelegateFilterRule withAttributeName(String attributeName) {
		this.attributeName = attributeName;
		return this;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public DelegateFilterRule withAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
		return this;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public DelegateFilterRule withOperator(String operator) {
		this.operator = operator;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(attributeName, attributeValue, operator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final DelegateFilterRule other)) {
			return false;
		}
		return Objects.equals(attributeName, other.attributeName) && Objects.equals(attributeValue, other.attributeValue) && Objects.equals(operator, other.operator);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DelegateFilterRule [attributeName=").append(attributeName).append(", attributeValue=").append(attributeValue).append(", operator=").append(operator).append("]");
		return builder.toString();
	}
}
