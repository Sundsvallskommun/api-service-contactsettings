package se.sundsvall.contactsettings.service;

public class Constants {

	public static final String ERROR_MESSAGE_DELEGATE_NOT_FOUND = "No delegate with id: '%s' could be found!";
	public static final String ERROR_MESSAGE_PRINCIPAL_NOT_FOUND = "No principal with contactSettingsId: '%s' could be found!";
	public static final String ERROR_MESSAGE_AGENT_NOT_FOUND = "No agent with contactSettingsId: '%s' could be found!";
	public static final String ERROR_MESSAGE_FILTER_NOT_FOUND = "No filter with id: '%s' could be found!";
	public static final String ERROR_MESSAGE_DELEGATE_ALREADY_EXIST = "A delegate with this this principal and agent already exists!";
	public static final String ERROR_MESSAGE_CONTACT_SETTING_NOT_FOUND = "No contact-setting with id '%s' could be found";
	public static final String ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ID_NOT_FOUND = "No contact-setting for partyId '%s' could be found";
	public static final String ERROR_MESSAGE_CONTACT_SETTING_BY_PARTY_ALREADY_EXISTS = "A contact-setting with party-id '%s' already exists";

	private Constants() {}
}
