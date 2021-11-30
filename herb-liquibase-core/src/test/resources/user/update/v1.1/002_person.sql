alter table `person` add column `remark` varchar(500) null comment '备注';
update `person` set `remark`='world';
