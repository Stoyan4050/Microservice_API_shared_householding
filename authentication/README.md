## Description
This is the home folder for the authentication service of our application.

## Create a database
- Create a mysql database and a table for users and authorities:
```SQL
create table users(username varchar(50) not null primary key,password varchar(500) not null,enabled boolean not null);

create table authorities (username varchar(50) not null primary key, authority varchar(50) not null, constraint fk_authorities_users foreign key(username) references users(username));

create unique index ix_auth_username on authorities (username,authority);
```
- Edit the application.properties in the `resources` folder and change the `auth.*` properties.

## Note
The tables in the database have case-insensitive collation.
I had to alter the tables to have a case-insensitive collation:
```SQL
ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs;
```
However, this would break the foreign key constraints, so they have to be dropped and added again later.
I decided to leave the collation as it is, because we have more pressing issues to deal with.

## How to run it?
**TODO**