CREATE DATABASE IF NOT EXISTS accounts;
USE accounts;


DROP TABLE IF EXISTS authentication;
CREATE TABLE authentication (
    id int(3) PRIMARY KEY AUTO_INCREMENT,
    name varchar(10) NOT NULL
    ) ENGINE = InnoDB;

INSERT INTO authentication(name)
VALUES('spartan');

INSERT INTO authentication(name)
VALUES('starman');

