# 索引优化分析案例

预先准备好数据

```sql
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `itdragon_order_list`;
CREATE TABLE `itdragon_order_list` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id，默认自增长',
  `transaction_id` varchar(150) DEFAULT NULL COMMENT '交易号',
  `gross` double DEFAULT NULL COMMENT '毛收入(RMB)',
  `net` double DEFAULT NULL COMMENT '净收入(RMB)',
  `stock_id` int(11) DEFAULT NULL COMMENT '发货仓库',
  `order_status` int(11) DEFAULT NULL COMMENT '订单状态',
  `descript` varchar(255) DEFAULT NULL COMMENT '客服备注',
  `finance_descript` varchar(255) DEFAULT NULL COMMENT '财务备注',
  `create_type` varchar(100) DEFAULT NULL COMMENT '创建类型',
  `order_level` int(11) DEFAULT NULL COMMENT '订单级别',
  `input_user` varchar(20) DEFAULT NULL COMMENT '录入人',
  `input_date` varchar(20) DEFAULT NULL COMMENT '录入时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8;

INSERT INTO itdragon_order_list VALUES ('10000', '81X97310V32236260E', '6.6', '6.13', '1', '10', 'ok', 'ok', 'auto', '1', 'itdragon', '2017-08-28 17:01:49');
INSERT INTO itdragon_order_list VALUES ('10001', '61525478BB371361Q', '18.88', '18.79', '1', '10', 'ok', 'ok', 'auto', '1', 'itdragon', '2017-08-18 17:01:50');
INSERT INTO itdragon_order_list VALUES ('10002', '5RT64180WE555861V', '20.18', '20.17', '1', '10', 'ok', 'ok', 'auto', '1', 'itdragon', '2017-09-08 17:01:49');

```

逐步开始进行优化：

第一个案例：

```sql
select * from itdragon_order_list where transaction_id = "81X97310V32236260E";
--通过查看执行计划发现type=all,需要进行全表扫描
explain select * from itdragon_order_list where transaction_id = "81X97310V32236260E";

--优化一、为transaction_id创建唯一索引
 create unique index idx_order_transaID on itdragon_order_list (transaction_id);
--当创建索引之后，唯一索引对应的type是const，通过索引一次就可以找到结果，普通索引对应的type是ref，表示非唯一性索引赛秒，找到值还要进行扫描，直到将索引文件扫描完为止，显而易见，const的性能要高于ref
 explain select * from itdragon_order_list where transaction_id = "81X97310V32236260E";
 
 --优化二、使用覆盖索引，查询的结果变成 transaction_id,当extra出现using index,表示使用了覆盖索引
 explain select transaction_id from itdragon_order_list where transaction_id = "81X97310V32236260E";
```

第二个案例

```sql
--创建复合索引
create index idx_order_levelDate on itdragon_order_list (order_level,input_date);

--创建索引之后发现跟没有创建索引一样，都是全表扫描，都是文件排序
explain select * from itdragon_order_list order by order_level,input_date;

--可以使用force index强制指定索引
explain select * from itdragon_order_list force index(idx_order_levelDate) order by order_level,input_date;
--其实给订单排序意义不大，给订单级别添加索引意义也不大，因此可以先确定order_level的值，然后再给input_date排序
explain select * from itdragon_order_list where order_level=3 order by input_date;
```

