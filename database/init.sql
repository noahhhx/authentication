create table public.users
(
    id                  uuid not null primary key,
    account_locked      boolean,
    created_at          timestamp(6) default now(),
    credentials_expired boolean,
    enabled             boolean,
    failed_attempts     integer,
    password            varchar(255),
    username            varchar(255) unique
);

create table public.authority
(
    id        bigserial primary key,
    authority varchar(255),
    user_id   uuid references public.users(id) on delete cascade
);


create index if not exists idx_authority_user_id on public.authority(user_id);

alter table public.users owner to "user";
alter table public.authority owner to "user";

