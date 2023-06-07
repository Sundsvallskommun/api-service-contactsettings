package se.sundsvall.contactsettings.service.mapper;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;
import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

class ContactSettingMapperTest {

	@Test
	void toContactSetting() {

		// Arrange
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

		// Act
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

		// Act
		final var result = ContactSettingMapper.toContactSetting(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toContactSettingWhenEmpty() {

		// Act
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

		// Arrange
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

		// Act
		final var result = ContactSettingMapper.toContactSettingEntity(contactSettingCreateRequest);

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

		// Act
		final var result = ContactSettingMapper.toContactSettingEntity((ContactSettingCreateRequest) null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toContactSettingEntityFromContactSettingCreateRequestWhenEmpty() {

		// Act
		final var result = ContactSettingMapper.toContactSettingEntity(ContactSettingCreateRequest.create());

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
	void mergeIntoContactSettingEntity() {

		// Arrange
		final var alias = "alias";
		final var channelAlias = "channelAlias";
		final var destination = "destination";
		final var contactMethod = "EMAIL";

		final var existingContactSettingEntity = ContactSettingEntity.create()
			.withAlias("oldAlias")
			.withChannels(List.of(Channel.create()
				.withAlias("oldChannelAlias")
				.withContactMethod("SMS")
				.withDestination("oldDestination")));

		final var contactSettingCreateRequest = ContactSettingUpdateRequest.create()
			.withAlias(alias)
			.withContactChannels(List.of(se.sundsvall.contactsettings.api.model.ContactChannel.create().withAlias(channelAlias)
				.withDestination(destination)
				.withContactMethod(ContactMethod.valueOf(contactMethod))));

		// Act
		final var result = ContactSettingMapper.mergeIntoContactSettingEntity(existingContactSettingEntity, contactSettingCreateRequest);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getAlias()).isEqualTo(alias);
		assertThat(result.getChannels()).hasSize(1);
		assertThat(result.getChannels().get(0).getAlias()).isEqualTo(channelAlias);
		assertThat(result.getChannels().get(0).getDestination()).isEqualTo(destination);
		assertThat(result.getChannels().get(0).getContactMethod()).isEqualTo(contactMethod);
	}

	@Test
	void mergeIntoContactSettingEntityWhenExistingEntityIsNull() {

		// Act
		final var result = ContactSettingMapper.mergeIntoContactSettingEntity(null, ContactSettingUpdateRequest.create());

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void mergeIntoContactSettingEntityWhenRequestIsNull() {

		// Arrange
		final var existingContactSettingEntity = ContactSettingEntity.create();

		// Act
		final var result = ContactSettingMapper.mergeIntoContactSettingEntity(ContactSettingEntity.create(), (ContactSettingUpdateRequest) null);

		// Assert
		assertThat(result)
			.isEqualTo(existingContactSettingEntity)
			.hasAllNullFieldsOrProperties();
	}

	@Test
	void mergeIntoContactSettingEntityWhenRequestIsEmpty() {

		// Arrange
		final var existingContactSettingEntity = ContactSettingEntity.create();

		// Act
		final var result = ContactSettingMapper.mergeIntoContactSettingEntity(ContactSettingEntity.create(), ContactSettingUpdateRequest.create());

		// Assert
		assertThat(result)
			.isEqualTo(existingContactSettingEntity)
			.hasAllNullFieldsOrProperties();
	}
}
