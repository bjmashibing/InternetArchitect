# 覆盖索引

1、当发起一个被索引覆盖的查询时，在explain的extra列可以看到using index的信息，此时就使用了覆盖索引

```sql
mysql> explain select store_id,film_id from inventory\G
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: inventory
   partitions: NULL
         type: index
possible_keys: NULL
          key: idx_store_id_film_id
      key_len: 3
          ref: NULL
         rows: 4581
     filtered: 100.00
        Extra: Using index
1 row in set, 1 warning (0.01 sec)

```

2、在大多数存储引擎中，覆盖索引只能覆盖那些只访问索引中部分列的查询。不过，可以进一步的进行优化，可以使用innodb的二级索引来覆盖查询。

例如：actor使用innodb存储引擎，并在last_name字段又二级索引，虽然该索引的列不包括主键actor_id，但也能够用于对actor_id做覆盖查询

```sql
mysql> explain select actor_id,last_name from actor where last_name='HOPPER'\G
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: actor
   partitions: NULL
         type: ref
possible_keys: idx_actor_last_name
          key: idx_actor_last_name
      key_len: 137
          ref: const
         rows: 2
     filtered: 100.00
        Extra: Using index
1 row in set, 1 warning (0.00 sec)

```

