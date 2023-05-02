INSERT INTO contact_setting (id, alias, created, modified, party_id) VALUES 
	('a42bfd69-ab22-443c-bdef-1cc6a70bcab3', 'John Smith', '2023-04-28 12:01:00', '2023-05-28 12:00:00', 'db96ca23-7c52-412e-b251-f75fb45551d5'),
	('534ba8a0-7484-45b3-b041-ff90f1228c16', 'Joe Doe', '2023-04-28 12:02:00', '2023-05-28 12:00:00', '62fd9c95-99c0-4874-b0ef-e990aaab03c6'),
	('07025549-3fbd-4db2-ab40-e1b93034b254', 'Jane Doe', '2023-04-28 12:03:00', '2023-05-28 12:00:00', '7af1869c-a8c2-4690-8d89-112ef15b4ffd');

INSERT INTO contact_setting_channel (contact_setting_id, alias, contact_method, destination, disabled) VALUES
	('a42bfd69-ab22-443c-bdef-1cc6a70bcab3', 'Email', 'EMAIL', 'john.smith@example.com', 0),
	('a42bfd69-ab22-443c-bdef-1cc6a70bcab3', 'SMS', 'SMS', '46701111111', 0),
	('534ba8a0-7484-45b3-b041-ff90f1228c16', 'Email', 'EMAIL', 'joe.doe@example.com', 0),
	('534ba8a0-7484-45b3-b041-ff90f1228c16', 'SMS', 'SMS', '46702222222', 0),
	('07025549-3fbd-4db2-ab40-e1b93034b254', 'Email', 'EMAIL', 'jane.doe@example.com', 0);

INSERT INTO delegate (id, filter, principal_id, agent_id) VALUES 
	-- Joe Doe delegates to Jane Doe, because they are married and love each other.
	('4d6adb65-172a-4671-a667-5e142bfc353e', 'Filter A', '534ba8a0-7484-45b3-b041-ff90f1228c16', '07025549-3fbd-4db2-ab40-e1b93034b254'); 
