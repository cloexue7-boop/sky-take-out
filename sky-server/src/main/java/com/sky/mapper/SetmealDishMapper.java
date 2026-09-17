package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐菜品表
 */

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据ids找出关联的全部套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
