INSERT INTO contact_setting (id, municipality_id, alias, created, modified, created_by_id, party_id) VALUES 
	('a42bfd69-ab22-443c-bdef-1cc6a70bcab3', '2281', 'John Smith', '2023-04-28 12:01:00', '2023-05-28 12:00:00', null, 'db96ca23-7c52-412e-b251-f75fb45551d5'),
	('534ba8a0-7484-45b3-b041-ff90f1228c16', '2281', 'Joe Doe', '2023-04-28 12:02:00', '2023-05-28 12:00:00', null, '62fd9c95-99c0-4874-b0ef-e990aaab03c6'),
	('07025549-3fbd-4db2-ab40-e1b93034b254', '2281', 'Jane Doe', '2023-04-28 12:03:00', '2023-05-28 12:00:00', null, '7af1869c-a8c2-4690-8d89-112ef15b4ffd'),
	('2c94ea99-a1b4-4073-b094-9ff79bad23b0', '2281', 'Virtual friend', '2023-04-28 12:04:00', '2023-05-28 12:00:00', 'a42bfd69-ab22-443c-bdef-1cc6a70bcab3', null), -- Created by John Smith
	('951dacb1-645a-41fd-952a-6089abdce481', '1984', 'Outside Joe', '2023-04-28 12:03:00', '2023-05-28 12:00:00', null, '3189727c-68e9-40f0-b7e4-838aa9752b91'), -- Other municipality
	('eba08ae5-6b62-42f2-aea5-59d12a2b821f', '1984', 'Outside Jim', '2023-04-28 12:03:00', '2023-05-28 12:00:00', null, '9fabc424-be10-49c9-80a8-7a0b3446da78'); -- Other municipality

INSERT INTO contact_setting_channel (contact_setting_id, alias, contact_method, destination, disabled) VALUES
	('a42bfd69-ab22-443c-bdef-1cc6a70bcab3', 'Email', 'EMAIL', 'john.smith@example.com', 0),
	('a42bfd69-ab22-443c-bdef-1cc6a70bcab3', 'SMS', 'SMS', '46701111111', 0),
	('534ba8a0-7484-45b3-b041-ff90f1228c16', 'Email', 'EMAIL', 'joe.doe@example.com', 0),
	('534ba8a0-7484-45b3-b041-ff90f1228c16', 'SMS', 'SMS', '46702222222', 0),
	('07025549-3fbd-4db2-ab40-e1b93034b254', 'Email', 'EMAIL', 'jane.doe@example.com', 0),
	('951dacb1-645a-41fd-952a-6089abdce481', 'Email', 'EMAIL', 'outside.joe@example.com', 0),
	('eba08ae5-6b62-42f2-aea5-59d12a2b821f', 'Email', 'EMAIL', 'outside.jim@example.com', 0);

INSERT INTO delegate (id, principal_id, agent_id) VALUES 
	-- Joe Doe delegates to Jane Doe.
	('4d6adb65-172a-4671-a667-5e142bfc353e', '534ba8a0-7484-45b3-b041-ff90f1228c16', '07025549-3fbd-4db2-ab40-e1b93034b254'),
    -- Mr Outside Joe delegates to Outside Jim.
    ('d929c8ad-7cfb-4e49-ac53-1c26da40aed7', '951dacb1-645a-41fd-952a-6089abdce481', 'eba08ae5-6b62-42f2-aea5-59d12a2b821f');

-- The "Joe -> Jane"-delegate is filtered on facilityId.
INSERT INTO delegate_filter (id, delegate_id, alias) VALUES 
	('4327dae1-a00b-462d-885a-417628ea3114', '4d6adb65-172a-4671-a667-5e142bfc353e', 'Jane will only see messages for summer house'); 
INSERT INTO delegate_filter_rule (delegate_filter_id, attribute_name, operator, attribute_value) VALUES 
    ('4327dae1-a00b-462d-885a-417628ea3114', 'facilityId', 'EQUALS', '12345678');
