create sequence activationcode_sequence start with 1 increment by 1
create sequence forgotpassword_sequence start with 1 increment by 1
create sequence itemgroups_sequence start with 1 increment by 1
create sequence items_sequence start with 1 increment by 1
create sequence permissiongroups_sequence start with 1 increment by 1
create sequence storageunits_sequence start with 1 increment by 1
create sequence user_sequence start with 1 increment by 1
create sequence warehouses_sequence start with 1 increment by 1

    create table activation_codes (
       activationcode_id bigint not null,
        active bit not null,
        code varchar(255) not null,
        user_user_id bigint,
        primary key (activationcode_id)
    ) engine=InnoDB

    create table forgotpassword_codes (
       forgotpassword_id bigint not null,
        active bit not null,
        code varchar(255) not null,
        issued_on datetime(6) not null,
        user_user_id bigint,
        primary key (forgotpassword_id)
    ) engine=InnoDB

    create table itemgroups (
       itemgroup_id bigint not null,
        mark varchar(255),
        name varchar(255) not null,
        primary key (itemgroup_id)
    ) engine=InnoDB

    create table items (
       item_id bigint not null,
        mark varchar(255),
        notes varchar(255),
        itemgroup_id bigint not null,
        storageunit_id bigint,
        primary key (item_id)
    ) engine=InnoDB

    create table pgroups (
       permissiongroup_id bigint not null,
        group_name varchar(255),
        permissions_list varchar(255),
        primary key (permissiongroup_id)
    ) engine=InnoDB

    create table storageunits (
       storageunit_id bigint not null,
        description varchar(255),
        location varchar(255),
        name varchar(255) not null,
        warehouse_id bigint not null,
        primary key (storageunit_id)
    ) engine=InnoDB

    create table users (
       user_id bigint not null,
        displayname varchar(255),
        email varchar(255) not null,
        password varchar(255) not null,
        password_algo integer not null,
        password_salt varchar(255),
        permission_group varchar(255),
        username varchar(255) not null,
        primary key (user_id)
    ) engine=InnoDB

    create table warehouses (
       warehouse_id bigint not null,
        description varchar(255),
        location varchar(255),
        name varchar(255) not null,
        primary key (warehouse_id)
    ) engine=InnoDB

    alter table activation_codes 
       add constraint UK_orf4l3c7ed8xc967wbfvvn0ey unique (code)

    alter table forgotpassword_codes 
       add constraint UK_f75nqj42hetnqnylemybvtc5p unique (code)

    alter table itemgroups 
       add constraint UK_ixjaobgbb84ew7p5nq4nsiiad unique (mark)

    alter table itemgroups 
       add constraint UK_x1ai4a2rui9wi0qah3u2m0pm unique (name)

    alter table items 
       add constraint UK_bx4uvt19xqhooa0mx5ilfag0c unique (mark)

    alter table users 
       add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email)

    alter table users 
       add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username)

    alter table warehouses 
       add constraint UK_2qm0l82n5ivhyqwmgejxxefm1 unique (name)

    alter table activation_codes 
       add constraint FKmvaxkfwmagbeat6f6tm3k0n9v 
       foreign key (user_user_id) 
       references users (user_id)

    alter table forgotpassword_codes 
       add constraint FKdoajkib8twdjsjbvi6hyoyofv 
       foreign key (user_user_id) 
       references users (user_id)

    alter table items 
       add constraint FKqyl3q0jhvrao0rejraxxiavg2 
       foreign key (itemgroup_id) 
       references itemgroups (itemgroup_id)

    alter table items 
       add constraint FKils3nsh0ddi0uesxam90rrsri 
       foreign key (storageunit_id) 
       references storageunits (storageunit_id)

    alter table storageunits 
       add constraint FK36jxyjanonv6fxy6igndbu3f8 
       foreign key (warehouse_id) 
       references warehouses (warehouse_id)
