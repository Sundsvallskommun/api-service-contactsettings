INSERT INTO contact_setting (id, municipality_id, alias, created, modified, created_by_id, party_id) VALUES
	('6c22993a-26b3-4a1f-805e-33a8c2b7c7e5', '2281', 'Mr Blue', '2023-05-23 12:01:00', '2023-05-24 12:00:00', null, 'f6511275-4400-4073-a6be-076e332dc931'),
	('1aa6df8d-6f5f-4e00-91aa-43dee8cfaef7', '2281', 'Mr Pink', '2023-05-23 12:01:00', '2023-05-24 12:00:00', null, '27c4424a-9b9c-49f4-8635-bcaca8cc202b'),
	('41e31470-150b-4db1-b3c1-c8f4108051ab', '2281', 'Mr White', '2023-05-23 12:01:00', '2023-05-24 12:00:00', null, '7903f7a9-325a-4a49-929a-d5952fef5c9a'),
	('a552e909-d2b1-4f08-acbe-938040f95ff3', '2281', 'Mr Brown', '2023-05-23 12:01:00', '2023-05-24 12:00:00', null, 'b63386a0-f19f-4a92-8f88-9fe8e9ad5339'),
	('41ad8bce-6e26-485f-9e4f-f80525b081ba', '2281', 'Mr Green', '2023-05-23 12:01:00', '2023-05-24 12:00:00', null, 'a523a78d-8e41-4487-8f53-8cceaafd8d50'),
	('c2d0a7f8-e0d1-444d-9a7b-3cf6e7b46ad0', '2281', 'Mr Child', '2023-05-23 12:01:00', '2023-05-24 12:00:00', '41e31470-150b-4db1-b3c1-c8f4108051ab', null), -- Created by Mr white
	('951dacb1-645a-41fd-952a-6089abdce481', '1984', 'Outside Joe', '2023-04-28 12:03:00', '2023-05-28 12:00:00', null, '3189727c-68e9-40f0-b7e4-838aa9752b91'), -- Other municipality
	('eba08ae5-6b62-42f2-aea5-59d12a2b821f', '1984', 'Outside Jim', '2023-04-28 12:03:00', '2023-05-28 12:00:00', null, '9fabc424-be10-49c9-80a8-7a0b3446da78'); -- Other municipality

INSERT INTO contact_setting_channel (contact_setting_id, alias, contact_method, destination, disabled) VALUES
	('6c22993a-26b3-4a1f-805e-33a8c2b7c7e5', 'Email', 'EMAIL', 'mr.blue@example.com', 0),
	('1aa6df8d-6f5f-4e00-91aa-43dee8cfaef7', 'Email', 'EMAIL', 'mr.pink@example.com', 0),
	('41e31470-150b-4db1-b3c1-c8f4108051ab', 'Email', 'EMAIL', 'mr.white@example.com', 0),
	('a552e909-d2b1-4f08-acbe-938040f95ff3', 'Email', 'EMAIL', 'mr.brown@example.com', 0),
	('41ad8bce-6e26-485f-9e4f-f80525b081ba', 'Email', 'EMAIL', 'mr.green@example.com', 0),
	('c2d0a7f8-e0d1-444d-9a7b-3cf6e7b46ad0', 'Email', 'EMAIL', 'mr.child@example.com', 0),
	('951dacb1-645a-41fd-952a-6089abdce481', 'Email', 'EMAIL', 'outside.joe@example.com', 0),
	('eba08ae5-6b62-42f2-aea5-59d12a2b821f', 'Email', 'EMAIL', 'outside.jim@example.com', 0);

INSERT INTO delegate (id, principal_id, agent_id, created) VALUES
	-- Mr Blue delegates to Mr Pink.
	('7d5fbffc-d1ff-4fff-86de-8158b4e34459', '6c22993a-26b3-4a1f-805e-33a8c2b7c7e5', '1aa6df8d-6f5f-4e00-91aa-43dee8cfaef7', '2023-06-20 12:01:00'),
	-- Mr Blue delegates to Mr White.
    ('336e4854-87f4-4407-9618-9fcf37e2f14f', '6c22993a-26b3-4a1f-805e-33a8c2b7c7e5', '41e31470-150b-4db1-b3c1-c8f4108051ab', '2023-07-21 12:02:00'),
    -- Mr White delegates to Mr Brown.
    ('a1381b7f-9149-4fd5-a271-5513d9579a8d', '41e31470-150b-4db1-b3c1-c8f4108051ab', 'a552e909-d2b1-4f08-acbe-938040f95ff3', '2023-08-22 12:03:00'),
    -- Mr Green delegates to Mr Brown.
    ('ce4c877c-3b86-497f-84dd-24487ea4d396', '41ad8bce-6e26-485f-9e4f-f80525b081ba', 'a552e909-d2b1-4f08-acbe-938040f95ff3', '2023-08-22 12:04:00'),
    -- Mr Outside Joe delegates to Outside Jim.
    ('d929c8ad-7cfb-4e49-ac53-1c26da40aed7', '951dacb1-645a-41fd-952a-6089abdce481', 'eba08ae5-6b62-42f2-aea5-59d12a2b821f', '2023-08-22 12:04:00');

INSERT INTO delegate_filter (id, delegate_id, alias, channel) VALUES 
	('a28c428b-a374-417e-89ea-3dba7d30a2e9', '7d5fbffc-d1ff-4fff-86de-8158b4e34459', 'Mr Blue delegates to Mr Pink', 'The channel'),
	('153555a5-5a25-41b2-b7b0-cee640427240', '336e4854-87f4-4407-9618-9fcf37e2f14f', 'Mr Blue delegates to Mr White', null),
	('b95eb1ed-0561-49f2-a7dc-5b8bc0411778', 'a1381b7f-9149-4fd5-a271-5513d9579a8d', 'Mr White delegates to Mr Brown', null),
	('daaea12d-5643-4f2d-b9a6-bf15d836fcf5', 'ce4c877c-3b86-497f-84dd-24487ea4d396', 'Mr Green delegates to Mr Brown (MATCH_ALL)', null),
	('1d525a22-c805-494a-acaf-4961728d3a5c', 'ce4c877c-3b86-497f-84dd-24487ea4d396', 'Mr Green delegates to Mr Brown', null),
	('b5bb9604-554d-41dd-9ed7-c47bd9db5642', 'd929c8ad-7cfb-4e49-ac53-1c26da40aed7', 'Mr Outside Joe delegates to Outside Jim', null);

INSERT INTO delegate_filter_rule (delegate_filter_id, attribute_name, operator, attribute_value) VALUES 
	-- The "Mr Blue -> Mr Pink"-delegate is filtered on facilityId.
    ('a28c428b-a374-417e-89ea-3dba7d30a2e9', 'facilityId', 'EQUALS', '123'),
    -- The "Mr Blue -> Mr White"-delegate is filtered on contractId.
    ('153555a5-5a25-41b2-b7b0-cee640427240', 'contractId', 'EQUALS', '456'),
    -- The "Mr White -> Mr Brown"-delegate is filtered on caseId.
    ('b95eb1ed-0561-49f2-a7dc-5b8bc0411778', 'caseId', 'EQUALS', '789'),
    -- The "Mr Green -> Mr Brown"-delegate is filtered on someId.
    ('1d525a22-c805-494a-acaf-4961728d3a5c', 'someId', 'EQUALS', '012'),
    -- The "Mr Green -> Mr Brown"-delegate is using the MATCH_ALL rule.
    ('daaea12d-5643-4f2d-b9a6-bf15d836fcf5', '*', 'EQUALS', '*'),
    -- The "Mr Outside Joe -> Outside Jim"-delegate is using the MATCH_ALL rule.
    ('b5bb9604-554d-41dd-9ed7-c47bd9db5642', '*', 'EQUALS', '*');
    
    