## Description
This is the home folder for the authentication service of our application.

## Create a database
- Create a mysql database and a table for users:
```SQL
create table users(username varchar(50) not null primary key,password varchar(500) not null,enabled boolean not null);
```
- Edit the application.properties in the `resources` folder and change the `auth.*` properties.

## How to run it?
**TODO**