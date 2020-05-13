 CREATE TABLE `deparment` (
        `id` int(11) NOT NULL,
        `name` varchar(100)  NOT NULL,
        PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

  CREATE TABLE `person` (
        `id` varchar(32) NOT NULL,
        `name` varchar(100)  NOT NULL,
        PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

insert into `deparment`(`id`,`name`) values(1,'wangsu');
insert into `deparment`(`id`,`name`) values(2,'云计算');
insert into `deparment`(`id`,`name`) values(3,'云计算2');
