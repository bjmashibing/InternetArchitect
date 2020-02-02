# sakila数据库说明

ZIP格式：http://downloads.mysql.com/docs/sakila-db.zip

tar格式 http://downloads.mysql.com/docs/sakila-db.tar.gz

官方文档 [http://dev.mysql.com/doc/sakila/en/index.html](https://dev.mysql.com/doc/sakila/en/index.html)

解压后得到三个文件：

1. sakila-schema.sql 文件包含创建Sakila数据库的结构：表、视图、存储过程和触发器

2. sakila-data.sql文件包含：使用 INSERT语句填充数据及在初始数据加载后，必须创建的触发器的定义

3. sakila.mwb文件是一个MySQL Workbench数据模型，可以在MySQL的工作台打开查看数据库结构。

```sql
--登录mysql
mysql -uroot -p123456
--导入表的结构数据
source /root/sakila-schema.sql
--导入表的数据
source /root/sakila-data.sql
```

##### 梳理各个表的名称和字段名称

1、actor：演员表，演员表列出了所有演员的信息。演员表和电影表之间是多对多的关系，通过film_actor表建立关系

​		actor_id:代理主键，用于唯一标识表中的每个演员

​		first_name: 演员的名字

​		last_name: 演员的姓氏

​		last_update: 该行已创建或最近更新的时间

2、address:地址表，地址表包含客户、员工和商店的地址信息。地址表的主键出现在顾客、 员工、和存储表的外键 

​		address_id: 代理主键用于唯一标识表中的每个地址

​		address: 地址的第一行

​		address2: 一个可选的第二行地址

​		district: 该地区的所属地区，这可以是国家，省，县等

​		city_id: 指向城市表的外键

​		postal_code: 邮政编码

​		phone: 地址的电话号码

​		last_update: 该行已创建或最近更新的时间

3、category：分类表，类别表列出了可以分配到一个电影类别。分类和电影是多对多的关系，通过表film_category建立关系

​		category_id: 代理主键用于唯一标识表中的每个类别

​		name: 类别名称

​		last_update: 该行已创建或最近更新的时间

4、city:城市表，城市表包含的城市名单。城市表使用外键来标示国家；在地址表中被作为外键来使用。

​		city_id: 代理主键用于唯一标识表中的每个城市

​		city: 城市的名字

​		country_id: 外键，用于标示城市所属的国家

​		last_update: 该行已创建或最近更新的时间

5、country：国家表，国家表中包含的国家名单。国家表是指在城市表的外键 。

​		country_id: 代理主键用于唯一标识表中的每个国家

​		country: 国家的名称

​		last_update: 该行已创建或最近更新的时间

6、customer：客户表，客户表包含了所有客户的列表 。 客户表在支付表和租金表被作为外键使用；客户表使用外键来表示地址和存储。

​		customer_id: 代理主键用于唯一标识表中的每个客户

​		store_id: 一个外键，确定客户所属的store。

​		first_name: 客户的名字

​		last_name: 客户的姓氏

​		email: 客户的电子邮件地址

​		address_id: 使用在地址 表的外键来确定客户的地址

​		active: 表示客户是否是活跃的客户

​		create_date: 顾客被添加到系统中的日期。使用 INSERT 触发器自动设置。

​		last_update: 该行已创建或最近更新的时间

7、film:电影表，电影表是一个可能在商店库存的所有影片名单。每部影片的拷贝的实际库存信息保存在库存表。电影表指使用外键来标示语言表；在film_category、film_actor和库存表中作为外键使用。

​		film_id: 代理主键用于唯一标识表中的每个电影

​		title: 影片的标题

​		description: 一个简短的描述或电影的情节摘要

​		release_year: 电影发行的年份

​		language_id: 使用外键来标示语言

​		original_language_id: 电影的原始语音。使用外键来标示语言

​		rental_duration: 租赁期限的长短，以天作为单位

​		rental_rate: 指定的期限内电影的租金

​		length: 影片的长度，以分钟为单位。

​		replacement_cost: 如果电影未被归还或损坏状态向客户收取的款项

​		rating: 分配给电影评级。可以是 G， PG，PG - 13 ， R 或NC - 17

​		special_features: 包括DVD上常见的特殊功能的列表

​		last_update: 该行已创建或最近更新的时间

8、film_actor:电影演员表，film_actor表是用来支持许多电影和演员之间的多对多关系 。对于每一个给定的电影演员，将有film_actor表中列出的演员和电影中的一个行 。

​		actor_id: 用于识别演员的外键

​		film_id: 用于识别电影的外键

​		last_update: 该行已创建或最近更新的时间

9、film_category:电影类别表，film_category表是用来支持许多电影和类别之间的多对多关系 。应用于电影的每个类别中，将有film_category表中列出的类别和电影中的一个行。

​		film_id: 用于识别电影的外键

​		category_id: 用于识别类别的外键

​		last_update: 该行已创建或最近更新的时间

10、film_text:电影信息表，film_text表是Sakila样例数据库唯一使用MyISAM存储引擎的表。MyISAM类型不支持事务处理等高级处理，而InnoDB类型支持。MyISAM类型的表强调的是性能，其执行数度比InnoDB类型更快。此表提供允许全文搜索电影表中列出的影片的标题和描述。film_text表包含的film_id，标题和描述的列电影表，保存的内容与电影表上的内容同步（指电影表的插入、更新和删除操作）

​		film_id: 代理主键用于唯一标识表中的每个电影

​		title: 影片的标题

​		description: 一个简短的描述或电影的情节摘要

11、inventory:库存表，库存表的一行为存放在一个给定的商店里的一个给定的电影的copy副本。库存表是使用外键来识别电影和存储；在出租表中使用外键来识别库存。

​		inventory_id: 理主键用于唯一标识每个项目在库存

​		film_id: 使用外键来识别电影

​		store_id: 使用外键来识别物品所在的商店

​		last_update: 该行已创建或最近更新的时间

12、language:语言表，语言表是一个查找表，列出可能使用的语言，电影可以有自己的语言和原始语言值 。

语言表在电压表中被作为外键来使用。

​		language_id: 代理主键用于唯一标识每一种语言

​		name: 语言的英文名称

​		last_update: 该行已创建或最近更新的时间

13、payment：付款表，付款表记录每个客户的付款，如支付的金额和租金的资料。

付款表使用外键来表示客户、出租、和工作人员。

​		payment_id: 代理主键用于唯一标识每个付款

​		customer_id: 使用外键来标识付款的客户

​		staff_id: 工作人员，负责处理支付 。使用外键来标识

​		rental_id: 租借ID, 外键，参照rental表

​		amount: 付款金额

​		payment_date: 处理付款的日期

​		last_update: 该行已创建或最近更新的时间

14 、rental：租金表，租借表的一行表示每个inventory的租借客户、租借时间、归还时间

租借表是使用外键来标识库存 ，顾客 和工作人员；在支付表中使用了外键来标识租金 。

​		rental_id: 代理主键唯一标识的租金

​		rental_date: 该项目租用的日期和时间

​		inventory_id: 该项目被租用

​		customer_id: 租用该项目的客户

​		return_date: 归还日期

​		staff_id: 处理该项业务的工作人员

​		last_update: 该行已创建或最近更新的时间

15、staff：工作人员表，工作人员表列出了所有的工作人员，包括电子邮件地址，登录信息和图片信息 。

工作人员表是指使用外键来标识存储和地址表；在出租、支付和商店表中作为外键。

​		staff_id: 代理主键唯一标识的工作人员

​		first_name: 工作人员的名字

​		last_name: 工作人员的姓氏

​		address_id: 工作人员的地址在地址表的外键

​		picture: 工作人员的照片，使用了 BLOB属性

​		email: 工作人员的电子邮件地址

​		store_id: 工作人员所在的商店，用外键标识

​		active: 是否是活跃的工作人员。

​		username: 用户名，由工作人员用来访问租赁系统

​		password: 工作人员访问租赁系统所使用的密码。使用了 SHA1 函数

​		last_update: 该行已创建或最近更新的时间

​		active: 是否有效，删除时设置为False		

16、store：商店表，store表列出了系统中的所有商店 。

store使用外键来标识工作人员和地址；在员工、客户、库存表被作为外键使用。

​		store_id: 代理主键唯一标识的商店

​		manager_staff_id: 使用外键来标识这家商店的经理

​		address_id: 使用外键来确定这家店的地址

​		last_update: 该行已创建或最近更新的时间

##### 视图表

1、actor_info视图提供了所有演员的列表及所演的电影, 电影按category分组.

```sql
SELECT
a.actor_id,
a.first_name,
a.last_name,
GROUP_CONCAT(DISTINCT CONCAT(c.name, ‘: ‘,
        (SELECT GROUP_CONCAT(f.title ORDER BY f.title SEPARATOR ‘, ‘)
                    FROM sakila.film f
                    INNER JOIN sakila.film_category fc
                      ON f.film_id = fc.film_id
                    INNER JOIN sakila.film_actor fa
                      ON f.film_id = fa.film_id
                    WHERE fc.category_id = c.category_id
                    AND fa.actor_id = a.actor_id
                 )
             )
             ORDER BY c.name SEPARATOR ‘; ‘)
AS film_info
FROM sakila.actor a
LEFT JOIN sakila.film_actor fa
  ON a.actor_id = fa.actor_id
LEFT JOIN sakila.film_category fc
  ON fa.film_id = fc.film_id
LEFT JOIN sakila.category c
  ON fc.category_id = c.category_id
GROUP BY a.actor_id, a.first_name, a.last_name
```

2、customer_list：客户列表，firstname和lastname连接成fullname，将address, city, country 集成在一个视图里

```sql
SELECT
    cu.customer_id AS ID,
    CONCAT(
        cu.first_name,
        _utf8 ‘ ‘,
        cu.last_name
    ) AS NAME,
    a.address AS address,
    a.postal_code AS `zip code`,
    a.phone AS phone,
    city.city AS city,
    country.country AS country,

IF (
    cu.active,
    _utf8 ‘active‘,
    _utf8 ‘‘
) AS notes,
 cu.store_id AS SID
FROM
    customer AS cu
JOIN address AS a ON cu.address_id = a.address_id
JOIN city ON a.city_id = city.city_id
JOIN country ON city.country_id = country.country_id
```

3、film_list:电影列表视图，包含了每一部电影的信息及电影所对应的演员。电影对应的演员以逗号作为分隔符。连接了 film, film_category, category,film_actor and actor 表的数据

```sql
SELECT
    film.film_id AS FID,
    film.title AS title,
    film.description AS description,
    category. NAME AS category,
    film.rental_rate AS price,
    film.length AS length,
    film.rating AS rating,
    GROUP_CONCAT(
        CONCAT(
            actor.first_name,
            _utf8 ‘ ‘,
            actor.last_name
        ) SEPARATOR ‘, ‘
    ) AS actors
FROM
    category
LEFT JOIN film_category ON category.category_id = film_category.category_id
LEFT JOIN film ON film_category.film_id = film.film_id
JOIN film_actor ON film.film_id = film_actor.film_id
JOIN actor ON film_actor.actor_id = actor.actor_id
GROUP BY
    film.film_id
```

4、nicer_but_slower_film_list:电影列表视图，包含了每一部电影的信息及电影所对应的演员。电影对应的演员以逗号作为分隔符。连接了 film, film_category, category,film_actor` and `actor 表的数据。和The film_list View不同，演员名字只有单词首字母大写了。

```sql
SELECT
    film.film_id AS FID,
    film.title AS title,
    film.description AS description,
    category. NAME AS category,
    film.rental_rate AS price,
    film.length AS length,
    film.rating AS rating,
    GROUP_CONCAT(
        CONCAT(
            CONCAT(
                UCASE(
                    SUBSTR(actor.first_name, 1, 1)
                ),
                LCASE(
                    SUBSTR(
                        actor.first_name,
                        2,
                        LENGTH(actor.first_name)
                    )
                ),
                _utf8 ‘ ‘,
                CONCAT(
                    UCASE(
                        SUBSTR(actor.last_name, 1, 1)
                    ),
                    LCASE(
                        SUBSTR(
                            actor.last_name,
                            2,
                            LENGTH(actor.last_name)
                        )
                    )
                )
            )
        ) SEPARATOR ‘, ‘
    ) AS actors
FROM
    category
LEFT JOIN film_category ON category.category_id = film_category.category_id
LEFT JOIN film ON film_category.film_id = film.film_id
JOIN film_actor ON film.film_id = film_actor.film_id
JOIN actor ON film_actor.actor_id = actor.actor_id
GROUP BY
    film.film_id
```

5、sales_by_film_category:每个电影种类的销售额 , payment →` `rental →inventory → film → film_category → category

```sql
SELECT
c.name AS category
, SUM(p.amount) AS total_sales
FROM payment AS p
INNER JOIN rental AS r ON p.rental_id = r.rental_id
INNER JOIN inventory AS i ON r.inventory_id = i.inventory_id
INNER JOIN film AS f ON i.film_id = f.film_id
INNER JOIN film_category AS fc ON f.film_id = fc.film_id
INNER JOIN category AS c ON fc.category_id = c.category_id
GROUP BY c.name
ORDER BY total_sales DESC
```

6、sales_by_store:每个商店的manager及销售额。payment → rental → inventory → store → staff

```sql
SELECT
CONCAT(c.city, _utf8‘,‘, cy.country) AS store
, CONCAT(m.first_name, _utf8‘ ‘, m.last_name) AS manager
, SUM(p.amount) AS total_sales
FROM payment AS p
INNER JOIN rental AS r ON p.rental_id = r.rental_id
INNER JOIN inventory AS i ON r.inventory_id = i.inventory_id
INNER JOIN store AS s ON i.store_id = s.store_id
INNER JOIN address AS a ON s.address_id = a.address_id
INNER JOIN city AS c ON a.city_id = c.city_id
INNER JOIN country AS cy ON c.country_id = cy.country_id
INNER JOIN staff AS m ON s.manager_staff_id = m.staff_id
GROUP BY s.store_id
ORDER BY cy.country, c.city
```

7、staff_list:工作人员的列表

```sql
SELECT
    s.staff_id AS ID,
    CONCAT(
        s.first_name,
        _utf8 ‘ ‘,
        s.last_name
    ) AS NAME,
    a.address AS address,
    a.postal_code AS `zip code`,
    a.phone AS phone,
    city.city AS city,
    country.country AS country,
    s.store_id AS SID
FROM
    staff AS s
JOIN address AS a ON s.address_id = a.address_id
JOIN city ON a.city_id = city.city_id
JOIN country ON city.country_id = country.country_id
```

各个表的结构关系：

![image-20191204153612196](C:\Users\63198\AppData\Roaming\Typora\typora-user-images\image-20191204153612196.png)