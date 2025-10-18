create extension if not exists "uuid-ossp";

create table if not exists labels (
    uuid uuid primary key default uuid_generate_v4(),
    label_type varchar(20) not null,
    label_text varchar(100) not null,
    constraint labels_type_text_unique unique (label_type, label_text)
);

create table if not exists ordered_labels (
    label_type varchar(20) not null,
    label_value integer not null,
    label_text varchar(100) not null,
    primary key (label_type, label_value)
);

create table if not exists tasks (
    uuid uuid primary key default uuid_generate_v4(),
    name text,
    status uuid references labels(uuid),
    priority integer,
    effort integer,
    start_date date,
    start_time time,
    end_date date,
    end_time time,
    parent uuid references tasks(uuid),
    properties jsonb,
    done boolean not null
);

create table if not exists task_tags (
    task uuid references tasks(uuid) on delete cascade,
    label uuid references labels(uuid) on delete cascade,
    primary key (task, label)
);

create type property_type as enum('String', 'UUID', 'Integer', 'Boolean', 'Date', 'Time');
create type multiplicity as enum('SINGLE', 'LIST', 'SET');

create table if not exists property_descriptors (
    name varchar(20) primary key,
    type property_type not null,
    subtype text,
    multiplicity multiplicity not null,
    default_value text,
    pseudo_property_provider text
);