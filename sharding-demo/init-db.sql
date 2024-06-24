-- auto-generated definition
create table t_address
(
    address_id   bigint auto_increment
        primary key,
    address_name varchar(255) null
);


-- auto-generated definition
create table t_account
(
    account_id  bigint auto_increment
        primary key,
    create_time datetime(6)  null,
    user_name   varchar(255) null
);

-- auto-generated definition
create table t_order_0
(
    account_id  bigint       null,
    create_time datetime(6)  null,
    order_id    bigint auto_increment
        primary key,
    title       varchar(255) null
);

-- auto-generated definition
create table t_order_1
(
    account_id  bigint       null,
    create_time datetime(6)  null,
    order_id    bigint auto_increment
        primary key,
    title       varchar(255) null
);

-- auto-generated definition
create table t_order_item_0
(
    account_id    bigint       null,
    create_time   datetime(6)  null,
    order_id      bigint       null,
    order_item_id bigint auto_increment
        primary key,
    item_name     varchar(255) null
);

create index order_id_index_t_order_item_0
    on t_order_item_0 (order_id);

-- auto-generated definition
create table t_order_item_1
(
    account_id    bigint       null,
    create_time   datetime(6)  null,
    order_id      bigint       null,
    order_item_id bigint auto_increment
        primary key,
    item_name     varchar(255) null
);

create index order_id_index_t_order_item_1
    on t_order_item_1 (order_id);

