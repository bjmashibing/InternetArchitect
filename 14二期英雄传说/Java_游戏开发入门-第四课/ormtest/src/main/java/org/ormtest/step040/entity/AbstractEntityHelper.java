package org.ormtest.step040.entity;

import java.sql.ResultSet;

/**
 * 抽象的实体助手
 */
public abstract class AbstractEntityHelper {
    /**
     * 将数据集转换为实体对象
     *
     * @param rs 数据集
     * @return
     *
     */
    public abstract Object create(ResultSet rs) throws Exception;
}
