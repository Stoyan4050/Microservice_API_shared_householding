## Description
This is the home folder for the authentication service of our application.

## Create a database
- Create a mysql database and a table for users and authorities:
```SQL
create table users(username varchar(50) not null primary key,password varchar(500) not null,enabled boolean not null);

create table authorities (username varchar(50) not null, authority varchar(50) not null, constraint fk_authorities_users foreign key(username) references users(username));

create unique index ix_auth_username on authorities (username,authority);
```
- Edit the application.properties in the `resources` folder and change the `auth.*` properties.

## How to run it?
**TODO**