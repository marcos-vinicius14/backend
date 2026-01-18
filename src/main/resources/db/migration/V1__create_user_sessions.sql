create table if not exists user_sessions (
    id uuid primary key,
    user_id uuid not null,
    access_token text not null,
    refresh_token text not null,
    expires_at timestamp with time zone not null,
    created_at timestamp with time zone not null default now(),
    constraint fk_user_sessions_user foreign key (user_id) references users (id)
);

create index if not exists idx_user_sessions_user_id on user_sessions (user_id);
create index if not exists idx_user_sessions_expires_at on user_sessions (expires_at);
