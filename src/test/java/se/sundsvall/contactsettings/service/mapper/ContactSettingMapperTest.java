package se.sundsvall.contactsettings.service.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;
import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ContactSettingMapperTest {

	@Test
	void toContactSetting() {
		final var id = "id";
		final var alias = "alias";
		final var partyId = "partyId";
		final var created = OffsetDateTime.now().minusDays(1);
		final var modified = OffsetDateTime.now();
		final var createdById = "createdById";
		final var channelAlias = "channelAlias";
		final var destination = "destination";
		final var contactMethod = "SMS";

		final var entity = ContactSettingEntity.create().withId(id)
			.withAlias(alias)
			.withPartyId(partyId)
			.withCreated(created)
			.withModified(modified)
			.withCreatedById(createdById)
			.withChannels(List.of(Channel.create().withAlias(channelAlias)
				.withDestination(destination)
				.withContactMethod(contactMethod)));

		// Call
		final var result = ContactSettingMapper.toContactSetting(entity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getAlias()).isEqualTo(alias);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getCreated()).isCloseTo(created, within(1, SECONDS));
		assertThat(result.getModified()).isCloseTo(modified, within(1, SECONDS));
		assertThat(result.getCreatedById()).isEqualTo(createdById);
		assertThat(result.getContactChannels()).hasSize(1);
		assertThat(result.getContactChannels().get(0).getAlias()).isEqualTo(channelAlias);
		assertThat(result.getContactChannels().get(0).getDestination()).isEqualTo(destination);
		assertThat(result.getContactChannels().get(0).getContactMethod()).isEqualTo(ContactMethod.valueOf(contactMethod));
	}

	@Test
	void toContactSettingWhenNull() {
		// Call
		final var result = ContactSettingMapper.toContactSetting(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toContactSettingWhenEmpty() {
		// Call
		final var result = ContactSettingMapper.toContactSetting(ContactSettingEntity.create());

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		assertThat(result.getAlias()).isNull();
		assertThat(result.getPartyId()).isNull();
		assertThat(result.getCreated()).isNull();
		assertThat(result.getModified()).isNull();
		assertThat(result.getCreatedById()).isNull();
		assertThat(result.getContactChannels()).isEmpty();
	}

	@Test
	void toContactSettingEntityFromContactSettingCreateRequest() {
		final var alias = "alias";
		final var partyId = "partyId";
		final var createdById = "createdById";
		final var channelAlias = "channelAlias";
		final var destination = "destination";
		final var contactMethod = "EMAIL";

		final var contactSettingCreateRequest = ContactSettingCreateRequest.create()
			.withAlias(alias)
			.withPartyId(partyId)
			.withCreatedById(createdById)
			.withContactChannels(List.of(se.sundsvall.contactsettings.api.model.ContactChannel.create().withAlias(channelAlias)
				.withDestination(destination)
				.withContactMethod(ContactMethod.valueOf(contactMethod))));

		// Call
		final var result = ContactSettingMapper.toContactSettingEntityFromCreateRequest(contactSettingCreateRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAlias()).isEqualTo(alias);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getCreatedById()).isEqualTo(createdById);
		assertThat(result.getChannels()).hasSize(1);
		assertThat(result.getChannels().get(0).getAlias()).isEqualTo(channelAlias);
		assertThat(result.getChannels().get(0).getDestination()).isEqualTo(destination);
		assertThat(result.getChannels().get(0).getContactMethod()).isEqualTo(contactMethod);
	}

	@Test
	void toContactSettingEntityFromContactSettingCreateRequestWhenNull() {
		// Call
		final var result = ContactSettingMapper.toContactSettingEntityFromCreateRequest(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toContactSettingEntityFromContactSettingCreateRequestWhenEmpty() {
		// Call
		final var result = ContactSettingMapper.toContactSettingEntityFromCreateRequest(ContactSettingCreateRequest.create());

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		assertThat(result.getAlias()).isNull();
		assertThat(result.getPartyId()).isNull();
		assertThat(result.getCreated()).isNull();
		assertThat(result.getModified()).isNull();
		assertThat(result.getCreatedById()).isNull();
		assertThat(result.getChannels()).isEmpty();
	}

	@Test
	void toContactSettingEntityFromContactSettingUpdateRequest() {
		final var alias = "alias";
		final var channelAlias = "channelAlias";
		final var destination = "destination";
		final var contactMethod = "EMAIL";

		final var contactSettingCreateRequest = ContactSettingUpdateRequest.create()
			.withAlias(alias)
			.withContactChannels(List.of(se.sundsvall.contactsettings.api.model.ContactChannel.create().withAlias(channelAlias)
				.withDestination(destination)
				.withContactMethod(ContactMethod.valueOf(contactMethod))));

		// Call
		final var result = ContactSettingMapper.toContactSettingEntityFromUpdateRequest(contactSettingCreateRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAlias()).isEqualTo(alias);
		assertThat(result.getChannels()).hasSize(1);
		assertThat(result.getChannels().get(0).getAlias()).isEqualTo(channelAlias);
		assertThat(result.getChannels().get(0).getDestination()).isEqualTo(destination);
		assertThat(result.getChannels().get(0).getContactMethod()).isEqualTo(contactMethod);
	}

	@Test
	void toContactSettingEntityFromContactSettingUpdateRequestWhenNull() {
		// Call
		final var result = ContactSettingMapper.toContactSettingEntityFromUpdateRequest(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toContactSettingEntityFromContactSettingUpdateRequestWhenEmpty() {
		// Call
		final var result = ContactSettingMapper.toContactSettingEntityFromUpdateRequest(ContactSettingUpdateRequest.create());

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		assertThat(result.getAlias()).isNull();
		assertThat(result.getPartyId()).isNull();
		assertThat(result.getCreated()).isNull();
		assertThat(result.getModified()).isNull();
		assertThat(result.getCreatedById()).isNull();
		assertThat(result.getChannels()).isEmpty();
	}
}
