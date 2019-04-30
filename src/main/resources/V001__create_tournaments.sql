CREATE TABLE tournaments (
    id     VARCHAR NOT NULL,
    secret VARCHAR NOT NULL,
    date   TIMESTAMP WITH TIME ZONE NOT NULL,
    name   VARCHAR,
    PRIMARY KEY(id),
);
