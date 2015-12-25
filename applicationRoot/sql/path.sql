-- 时间 yyyy-MM-dd 修改者
-- 如 2015-10-14 wzw

-- 创建表
/**
create table if not exists test (
	testID int(11) primary key,
	name varchar(50)
);

alter table tableName add column name varchar(50); -- 新增表字段
alter table tableName drop column name; -- 删除表字段
alter table tableName change userName userName varchar(50); -- 修改表字段类型  将test表中的userName字段长度改成50

**/


-- 以上仅为参考示例  