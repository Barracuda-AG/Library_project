DROP DATABASE library;
CREATE DATABASE `library` default charset utf8;
USE library;

DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `orders_books`;

CREATE TABLE `orders` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `is_returned` bit(1) NOT NULL,
                          `issue_date` date DEFAULT NULL,
                          `penalty` int(11) DEFAULT NULL,
                          `return_date` date DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `book` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `author` varchar(255) DEFAULT NULL,
                        `publish_date` date DEFAULT NULL,
                        `publisher` varchar(255) DEFAULT NULL,
                        `quantity` int(11) DEFAULT NULL,
                        `title` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

CREATE TABLE `orders_books` (
                                `order_id` bigint(20) NOT NULL,
                                `books_id` bigint(20) NOT NULL,
                                KEY `FK5jfo1ob4ev0f1dtowoul3uo38` (`books_id`),
                                KEY `FKol7arli7ptfejk3kwuo2n2mx3` (`order_id`),
                                CONSTRAINT `FK5jfo1ob4ev0f1dtowoul3uo38` FOREIGN KEY (`books_id`) REFERENCES `book` (`id`),
                                CONSTRAINT `FKol7arli7ptfejk3kwuo2n2mx3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `account_non_locked` bit(1) NOT NULL,
                        `email` varchar(255) DEFAULT NULL,
                        `first_name` varchar(255) DEFAULT NULL,
                        `last_name` varchar(255) DEFAULT NULL,
                        `password` varchar(255) DEFAULT NULL,
                        `role` varchar(255) DEFAULT NULL,
                        `order_id` bigint(20) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
                        KEY `FKoyh53smidv8241km6oi2iclrr` (`order_id`),
                        CONSTRAINT `FKoyh53smidv8241km6oi2iclrr` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

INSERT INTO book (id, author, publish_date, publisher, quantity, title) VALUES
    (1, 'Liane Moriarty', '2018-10-15', 'Penguin', 15, 'Truly, madly, guilty'),
    (2, 'John Tolkien', '1940-05-11', 'London', 12, 'Hobbit'),
    (3, 'Джоан Роулінг', '1998-06-12', 'Київ', 15, 'Гаррі Поттер');

INSERT INTO user VALUES (1, true, 'admin@mail.com', 'admin', 'admin', '$2a$10$eq7CA.nRCeMt18n4RVqRGO/y.2KX/v7y9.8wuzMDHz.K3/NW3aMj2', 'ROLE_ADMIN', null);
INSERT INTO user VALUES (2, true, 'librarian@mail.com', 'John', 'Smith', '$2a$10$fX1kJ18YIG/PqqcOqFA1C.lU8F9RCkEwolmKfv88WUlusJjRZ0mJm', 'ROLE_LIBRARIAN', null);
INSERT INTO user VALUES (3, true, 'user@mail.com', 'Oleksandr', 'Gorbatov', '$2a$10$KN2UH.fCwT6rTbwxLRR6dO18rCA.Ky4cMjJY8dXlcLWoLc8Q37kPe', 'ROLE_USER', null);