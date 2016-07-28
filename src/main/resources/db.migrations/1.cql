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
    -- address_json text, --todo add with replay.
    primary key (email)
);