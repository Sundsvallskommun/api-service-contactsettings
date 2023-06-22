
    create table contact_setting (
        created datetime(6),
        modified datetime(6),
        alias varchar(255),
        created_by_id varchar(255),
        id varchar(255) not null,
        party_id varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table contact_setting_channel (
        disabled bit,
        alias varchar(255),
        contact_method varchar(255),
        contact_setting_id varchar(255) not null,
        destination varchar(255)
    ) engine=InnoDB;

    create table delegate (
        created datetime(6),
        modified datetime(6),
        agent_id varchar(255) not null,
        id varchar(255) not null,
        principal_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table delegate_filter (
        created datetime(6),
        modified datetime(6),
        alias varchar(255),
        channel varchar(255),
        delegate_id varchar(255),
        id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table delegate_filter_rule (
        attribute_name varchar(255),
        attribute_value varchar(255),
        delegate_filter_id varchar(255) not null,
        operator varchar(255)
    ) engine=InnoDB;

    create index contact_setting_party_id_index 
       on contact_setting (party_id);

    create index contact_setting_created_by_id_index 
       on contact_setting (created_by_id);

    create index contact_setting_channel_destination_index 
       on contact_setting_channel (destination);

    alter table if exists contact_setting_channel 
       add constraint fk_contact_setting_contact_setting_channel 
       foreign key (contact_setting_id) 
       references contact_setting (id);

    alter table if exists delegate 
       add constraint fk_delegate_agent_id_contact_setting_id 
       foreign key (agent_id) 
       references contact_setting (id);

    alter table if exists delegate 
       add constraint fk_delegate_principal_id_contact_setting_id 
       foreign key (principal_id) 
       references contact_setting (id);

    alter table if exists delegate_filter 
       add constraint fk_delegate_id_delegate_filter_delegate_id 
       foreign key (delegate_id) 
       references delegate (id);

    alter table if exists delegate_filter_rule 
       add constraint fk_delegate_filter_delegate_filter_rule 
       foreign key (delegate_filter_id) 
       references delegate_filter (id);
