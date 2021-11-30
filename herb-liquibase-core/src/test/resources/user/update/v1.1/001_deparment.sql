alter table `deparment` add column `remark` varchar(500) null comment '备注';
update `deparment` set `remark`='hello';
