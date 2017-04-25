CREATE TABLE users
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(100) NOT NULL,
    age         INTEGER,
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY(id, name)
);
