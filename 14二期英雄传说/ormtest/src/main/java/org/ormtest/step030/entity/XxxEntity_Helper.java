package org.ormtest.step030.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * 实体助手类, 这个更通用
 */
public class XxxEntity_Helper {
    /**
     * 将数据集装换为实体对象
     *
     * @param entityClazz 实体类
     * @param rs          数据集
     * @param <TEntity>   实体类
     * @return
     * @throws Exception
     */
    public <TEntity> TEntity create(Class<TEntity> entityClazz, ResultSet rs) throws Exception {
        if (null == rs) {
            return null;
        }
        //
        // 更通用的助手类,
        // 甭管实体类是哪个, 也甭管实体类有多少属性,
        // 全灭!
        // 但是,
        // 就是性能太差了...
        //
        // 创建新的实体对象
        Object newEntity = entityClazz.newInstance();

        // 获取类的字段数组
        Field[] fArray = entityClazz.getFields();

        for (Field f : fArray) {
            // 获取字段上注解
            Column annoColumn = f.getAnnotation(Column.class);

            if (annoColumn == null) {
                // 如果注解为空,
                // 则直接跳过...
                continue;
            }

            // 获取列名称
            String colName = annoColumn.name();
            // 从数据库中获取列值
            Object colVal = rs.getObject(colName);

            if (colVal == null) {
                // 如果列值为空,
                // 则直接跳过...
                continue;
            }

            // 设置字段值
            f.set(newEntity, colVal);
        }

        return (TEntity) newEntity;
    }
}
