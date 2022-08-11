create sequence hibernate_sequence start 1 increment 1;

create table file
(
    id        bigserial not null,
    file_name varchar(255),
    primary key (id)
);

create table line
(
    id               bigserial not null,
    random_date      varchar,
    latin_symbols    varchar,
    cyrillic_symbols varchar,
    whole_digit      int4,
    fractional_digit double precision,
    file_id          int8,
    primary key (id)
);

alter table if exists line
    add constraint forgein_key_line_to_file
    foreign key (file_id)
    references line;