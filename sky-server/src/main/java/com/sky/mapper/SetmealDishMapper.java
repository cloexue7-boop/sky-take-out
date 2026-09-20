package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 套餐菜品表
 */

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ids找出关联的全部套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 批量新增套餐和菜品的关系
     * @param setmealDishes
     */

    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 批量删除套餐和菜品的关系,根据套餐id删除菜品
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteSetmealDish(Long id);

    /**
     * 根据套餐id查询菜品列表
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);
}
