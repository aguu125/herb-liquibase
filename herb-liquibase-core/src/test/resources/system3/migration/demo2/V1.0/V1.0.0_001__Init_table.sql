CREATE TABLE `sys_org`
(
    `org_id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `org_name`        varchar(1024)    NOT NULL unique ,
    `encode_password` varchar(1024)       NOT NULL,
    `age`             int(3)           NOT NULL,
    PRIMARY KEY (`org_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
