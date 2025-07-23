--liquibase formatted sql

--changeset denis_vasilev:001-create-users
create extension if not exists pgcrypto;

create table users
(
    id              uuid primary key default gen_random_uuid(),
    name            text not null unique,
    hashed_password text not null
);

create table accounts
(
    id       uuid primary key,
    user_id  uuid           not null,
    currency varchar(3)     not null,
    balance  decimal(18, 6) not null
);

create index idx_accounts_user_id on accounts (user_id);

create table transactions
(
    id         uuid primary key,
    user_id    uuid                                   not null,
    type       varchar(20)                            not null,
    currency   varchar(3)                             not null,
    amount     decimal(18, 6)                         not null,
    status     varchar(20)                            not null,
    created_at timestamp with time zone default now() not null
);

create index idx_transactions_status on transactions (status);
create index idx_transactions_id on transactions (id);

--rollback drop table users;
--rollback drop table accounts;
--rollback drop table transactions;
--rollback drop extension if exists pgcrypto;
