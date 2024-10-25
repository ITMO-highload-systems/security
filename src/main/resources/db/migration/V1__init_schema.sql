create table if not exists _user
(
    id       SERIAL PRIMARY KEY,
    email    text unique not null,
    password text        not null,
    role     text        not null,
    banned boolean not null default false,
    deleted boolean not null default false
);

create table token
(
    id SERIAL PRIMARY KEY,
    token text not null ,
    revoked boolean not null default false,
    expired boolean not null default false,
    user_id int references _user
);

insert into _user(email, password, role) values ('admin@gmail.com', '$2a$10$lYyLIR.WhJWATAoj1.53LOINXZ8s1DPjZGJc/NkzjkYxbcB92SwYK', 'ADMIN');
