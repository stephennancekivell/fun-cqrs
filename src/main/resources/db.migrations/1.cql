CREATE KEYSPACE events WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

CREATE TABLE events.events (
  id text,
  created timestamp,
  name text,
  data text,
  PRIMARY KEY (id, created)
)
WITH CLUSTERING ORDER BY (created DESC);

create table events.users(
    email text,
    firstname text,
    surname text,
    address text,
    city text,
    primary key (email)
);

create table events.users_by_city(
    city text,
    email text,
    primary key (city)
);
