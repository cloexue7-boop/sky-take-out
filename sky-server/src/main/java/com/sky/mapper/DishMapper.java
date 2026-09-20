package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into dish (name, category_id, price, image, description, create_time, update_time, create_user, update_user, status) " +
            "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
    void save(Dish dish);

    /**
     * 分页查询，返回List<dishVo></>
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("SELECT * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 删除菜品
     * @param id
     */
    @Delete("delete from dish where id=#{id}")
    void delete(Long id);


    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 修改菜品启动禁用状态
     * @param status
     * @param id
     */
    @AutoFill(OperationType.UPDATE)
    @Update("update dish set status = #{status} where id = #{id}")
    void updateStatus(Integer status, Long id);

    /**
     * 根据分类id查询全部菜品
     * @param dish
     * @return
     */
    List<Dish> getByCategoryId(Dish dish);

    /**
     * 根据setmeal_id套餐id查询全部菜品信息
     * @param id
     * @return
     */
    @Select("select dish.* from dish left join setmeal_dish on dish.id = setmeal_dish.dish_id where setmeal_id = #{id}")
    List<Dish> getBySetmealId(Long id);
}
