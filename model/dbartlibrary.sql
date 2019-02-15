drop database if exists dbartlibrary;
create database dbartlibrary;
use dbartlibrary;

create table users (
	userId int(11) not null auto_increment,
	name varchar(100) not null,
	email varchar(100) not null,
	password varchar(50) not null,
	number varchar(11) null,
	address varchar(200) null,
	primary key (userId),
	unique (email)
);
create table artworks (
	artworkId int(11) not null auto_increment,
	userId int(11) not null,
	deviceName varchar(100) not null,
	artworkName varchar(100) not null,
	author varchar(100) not null,
	date varchar(100) not null,
	description varchar(300) not null,
	artworkUrl varchar(200) not null,
	primary key (artworkId)
);



