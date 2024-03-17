create table field
(
    id       serial          NOT NULL,
    name     VARCHAR(255) NOT NULL,
    selector VARCHAR(255) NOT NULL,
    weight   REAL        NOT NULL,
    primary key (id)
);

insert into field(name, selector, weight)
VALUES ('title', 'title', 1.0),
       ('body', 'body', 0.8);