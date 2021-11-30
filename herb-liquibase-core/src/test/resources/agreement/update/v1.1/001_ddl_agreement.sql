alter table `agreement` add column `remark` varchar(500) null comment '备注';
update `agreement` set `remark`='hello';
