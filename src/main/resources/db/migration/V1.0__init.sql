USE `traffic_generator`;

CREATE TABLE IF NOT EXISTS `endpoint`(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `endpoint` varchar(45) NOT NULL,
    `method` varchar(45) NOT NULL,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `test`(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `endpoint_id` int(11) NOT NULL,
    `name` varchar(45) NOT NULL,
    `number_of_requests` int(11) NOT NULL,
    `number_of_users` int(11) NOT NULL,
    `database_time` bigint(45) NOT NULL,
    `api_time` bigint(45) NOT NULL,
    `application_time` bigint(45) NOT NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`endpoint_id`) REFERENCES `endpoint`(`id`)
);

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE test;
TRUNCATE endpoint;
SET FOREIGN_KEY_CHECKS = 1;