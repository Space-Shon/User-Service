create table users
(
    id            integer generated always as identity primary key,
    login         varchar(255) not null,
    password_hash varchar(255) not null,
    role          varchar(255) not null,

    constraint uq_user_login unique (login)
)

