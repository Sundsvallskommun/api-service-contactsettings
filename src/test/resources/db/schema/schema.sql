
    create table contact_setting (
       id varchar(255) not null,
        alias varchar(255),
        created datetime(6),
        modified datetime(6),
        party_id varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table contact_setting_channel (
       contact_setting_id varchar(255) not null,
        alias varchar(255),
        contact_method varchar(255),
        destination varchar(255),
        disabled bit
    ) engine=InnoDB;

    create table delegate (
       id varchar(255) not null,
        created datetime(6),
        modified datetime(6),
        agent_id varchar(255) not null,
        principal_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table delegate_filter (
       delegate_id varchar(255) not null,
        `key` varchar(255),
        value varchar(255)
    ) engine=InnoDB;
create index contact_setting_party_id_index on contact_setting (party_id);

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
       add constraint fk_delegate_delegate_filter
       foreign key (delegate_id)
       references delegate (id);
