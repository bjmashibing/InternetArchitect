package com.mashibing.springboot.mapper;

import java.io.Serializable;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * DAO公共基类，由MybatisGenerator自动生成请勿修改
 * @param <Model> The Model Class 这里是泛型不是Model类
 * @param <PK> The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类
 * @param <E> The Example Class
 */
public interface MyBatisBaseDao<Model, PK extends Serializable, E> {
    long countByExample(E example);

    int deleteByExample(E example);

    int deleteByPrimaryKey(PK id);

    int insert(Model record);

    int insertSelective(Model record);

    List<Model> selectByExample(E example);

    Model selectByPrimaryKey(PK id);

    int updateByExampleSelective(@Param("record") Model record, @Param("example") E example);

    int updateByExample(@Param("record") Model record, @Param("example") E example);

    int updateByPrimaryKeySelective(Model record);

    int updateByPrimaryKey(Model record);
}