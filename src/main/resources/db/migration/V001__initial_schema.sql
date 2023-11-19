create table court
(
    id   uuid primary key not null,
    name varchar(255)     not null
);

create table player
(
    id    uuid primary key not null,
    email varchar(255)     not null
);

create table booking
(
    id        uuid primary key            not null,
    date      timestamptz                 not null,
    court_id  uuid references court (id)  not null,
    player_id uuid references player (id) not null
);