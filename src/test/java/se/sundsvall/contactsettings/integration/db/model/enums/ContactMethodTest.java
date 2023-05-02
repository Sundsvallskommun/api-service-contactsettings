package se.sundsvall.contactsettings.integration.db.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod.EMAIL;
import static se.sundsvall.contactsettings.integration.db.model.enums.ContactMethod.SMS;

import org.junit.jupiter.api.Test;

class ContactMethodTest {

	@Test
	void enumValues() {
		assertThat(ContactMethod.values()).containsExactlyInAnyOrder(SMS, EMAIL);
	}

	@Test
	void enumToString() {
		assertThat(SMS).hasToString("SMS");
		assertThat(EMAIL).hasToString("EMAIL");
	}
}
