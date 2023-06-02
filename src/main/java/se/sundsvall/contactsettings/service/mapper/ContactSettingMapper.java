package se.sundsvall.contactsettings.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

import java.util.List;
import java.util.Optional;

import se.sundsvall.contactsettings.api.model.ContactChannel;
import se.sundsvall.contactsettings.api.model.ContactSetting;
import se.sundsvall.contactsettings.api.model.ContactSettingCreateRequest;
import se.sundsvall.contactsettings.api.model.ContactSettingUpdateRequest;
import se.sundsvall.contactsettings.api.model.enums.ContactMethod;
import se.sundsvall.contactsettings.integration.db.model.Channel;
import se.sundsvall.contactsettings.integration.db.model.ContactSettingEntity;

public class ContactSettingMapper {

	private ContactSettingMapper() {}

	public static ContactSettingEntity toContactSettingEntity(final ContactSettingCreateRequest contactSettingCreateRequest) {
		return Optional.ofNullable(contactSettingCreateRequest)
			.map(request -> ContactSettingEntity.create()
				.withPartyId(request.getPartyId())
				.withChannels(toChannels(request.getContactChannels()))
				.withAlias(request.getAlias())
				.withCreatedById(request.getCreatedById()))
			.orElse(null);
	}

	// TODO: Gör en merge-metod istället
	public static ContactSettingEntity toContactSettingEntity(final ContactSettingUpdateRequest contactSettingUpdateRequest) {
		return Optional.ofNullable(contactSettingUpdateRequest)
			.map(request -> ContactSettingEntity.create()
				.withChannels(toChannels(request.getContactChannels()))
				.withAlias(request.getAlias()))
			.orElse(null);
	}

	public static ContactSetting toContactSetting(final ContactSettingEntity contactSettingEntity) {
		return Optional.ofNullable(contactSettingEntity)
			.map(entity -> ContactSetting.create()
				.withId(entity.getId())
				.withPartyId(entity.getPartyId())
				.withContactChannels(toContactChannels(entity.getChannels()))
				.withVirtual(isNull(entity.getPartyId()))
				.withAlias(entity.getAlias())
				.withCreated(entity.getCreated())
				.withModified(entity.getModified())
				.withCreatedById(entity.getCreatedById()))
			.orElse(null);
	}

	private static List<ContactChannel> toContactChannels(final List<Channel> channels) {
		return Optional.ofNullable(channels).orElse(emptyList()).stream()
			.map(ContactSettingMapper::toContactChannel)
			.toList();
	}

	private static ContactChannel toContactChannel(final Channel channel) {
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
		return Channel.create()
			.withDisabled(contactChannel.isDisabled())
			.withAlias(contactChannel.getAlias())
			.withContactMethod(contactChannel.getContactMethod().name())
			.withDestination(contactChannel.getDestination());
	}
}
