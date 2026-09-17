package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味
     * @param flavorList
     */
    void insertBatch(List<DishFlavor> flavorList);

    /**
     * 删除菜品关联的全部口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteById(Long id);

    /**
     * 根据菜品id查询全部口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(Long id);
}
