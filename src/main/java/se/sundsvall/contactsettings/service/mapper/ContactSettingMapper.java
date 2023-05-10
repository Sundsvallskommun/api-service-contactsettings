package se.sundsvall.contactsettings.service.mapper;

import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;
import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
public class ContactSettingMapper {

	private ContactSettingMapper() {}

	public static ContactSettingEntity toContactSettingEntityFromCreateRequest(final ContactSettingCreateRequest contactSettingCreateRequest) {
		if (isNull(contactSettingCreateRequest)) {
			return null;
		}

		return ContactSettingEntity.create()
			.withPartyId(contactSettingCreateRequest.getPartyId())
			.withChannels(toChannels(contactSettingCreateRequest.getContactChannels()))
			.withAlias(contactSettingCreateRequest.getAlias())
			.withCreatedById(contactSettingCreateRequest.getCreatedById());
	}

	public static ContactSettingEntity toContactSettingEntityFromUpdateRequest(final ContactSettingUpdateRequest contactSettingUpdateRequest) {
		if (isNull(contactSettingUpdateRequest)) {
			return null;
		}

		return ContactSettingEntity.create()
			.withChannels(toChannels(contactSettingUpdateRequest.getContactChannels()))
			.withAlias(contactSettingUpdateRequest.getAlias());
	}

	public static ContactSetting toContactSetting(final ContactSettingEntity contactSettingEntity) {
		if (isNull(contactSettingEntity)) {
			return null;
		}

		return ContactSetting.create()
			.withId(contactSettingEntity.getId())
			.withPartyId(contactSettingEntity.getPartyId())
			.withContactChannels(toContactChannels(contactSettingEntity.getChannels()))
			.withVirtual(isNull(contactSettingEntity.getPartyId()))
			.withAlias(contactSettingEntity.getAlias())
			.withCreated(contactSettingEntity.getCreated())
			.withModified(contactSettingEntity.getModified())
			.withCreatedById(contactSettingEntity.getCreatedById());
	}

	private static List<ContactChannel> toContactChannels(final List<Channel> channels) {
		return Optional.ofNullable(channels).orElse(emptyList()).stream()
			.map(ContactSettingMapper::toContactChannel)
			.toList();
	}

	private static ContactChannel toContactChannel(final Channel channel) {
		if (isNull(channel)) {
			return null;
		}

		return ContactChannel.create()
			.withDisabled(channel.isDisabled())
			.withAlias(channel.getAlias())
			.withContactMethod(ContactMethod.valueOf(channel.getContactMethod()))
			.withDestination(channel.getDestination());
	}

	private static List<Channel> toChannels(final List<ContactChannel> contactChannels) {
		return Optional.ofNullable(contactChannels).orElse(emptyList()).stream()
			.map(ContactSettingMapper::toChannel)
			.toList();
	}

	private static Channel toChannel(final ContactChannel contactChannel) {
		if (isNull(contactChannel)) {
			return null;
		}

		return Channel.create()
			.withDisabled(contactChannel.isDisabled())
			.withAlias(contactChannel.getAlias())
			.withContactMethod(contactChannel.getContactMethod().name())
			.withDestination(contactChannel.getDestination());
	}
}
