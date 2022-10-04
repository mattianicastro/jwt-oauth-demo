CREATE TABLE IF NOT EXISTS users (
                       username varchar(100) primary key,
                       picture text,
                       github_id int,
                       password bytes NOT NULL,
                       salt bytes(16) NOT NULL
);