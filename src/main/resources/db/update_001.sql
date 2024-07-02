drop table if exists employees_accounts;
drop table if exists person;
drop table if exists employees;

create table person (
    id serial primary key not null,
    login varchar(2000),
    password varchar(2000)
);

-- insert into person (login, password) values ('parsentev', '123');
-- insert into person (login, password) values ('ban', '123');
-- insert into person (login, password) values ('ivan', '123');

create table employees (
    id serial primary key not null,
    hired timestamp,
    name varchar(2000),
    tin varchar(2000)
);

create table employees_accounts (
    employee_id integer references employees(id),
    accounts_id integer references person(id)
);

select * from person;

select * from employees;

select * from employees_accounts;