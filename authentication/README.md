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

The tables in the database have case-insensitive collation. I had to alter the tables to have a case-insensitive
collation:

```SQL
ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs;
```

However, this would break the foreign key constraints, so they have to be dropped and added again later. I decided to
leave the collation as it is, because we have more pressing issues to deal with.

This is not an issue for the log in functionality, because Spring Security is smart enough to make binary SQL queries
when authenticating users.

This is an issue for every other microservice (and for user registration), but I did not have time to deal with it.

## How does it work?

The main job of the authentication microservice is to register new users and to check if provided credentials are valid.

It sends a signed JWT token to the gateway microservice that will be used everytime a user makes a request.

When a user registers (via the `/auth/register` URL mapping), the authentication microservice adds their credentials to
the authentication database, and also to the requests microservice's database.

After a user is registered, he/she can use `/auth/login`. The authentication microservice will create and sign a JWT
token and send it to the gateway.

## What still needs to be done?

Because we did not have time to implement everything we wanted, we left some features unimplemented.

Right now, user registration works, but if a user wants to delete their account, there is no communication between the
authentication and requests microservices, so user deletion does not fully work and can even break the state of the
microservices.

Also, password reset is not at all implemented, so users cannot change their passwords.