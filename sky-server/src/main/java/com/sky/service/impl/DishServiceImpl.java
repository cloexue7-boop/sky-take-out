package com.sky.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private  DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional
    @Override

    public void saveWithFlavor(DishDTO dishDTO){
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //默认为停售
        dish.setStatus(StatusConstant.DISABLE);

        //保存菜品
        dishMapper.save(dish);

        //得到菜品的id号
        long dishId=dish.getId();

        //插入新的口味
        List<DishFlavor>  flavorList=dishDTO.getFlavors();
        if(flavorList!=null&&flavorList.size()>0){
            for(DishFlavor dishFlavor:flavorList){
                dishFlavor.setDishId(dishId);
            }

            //向口味表中插入多条数据
            dishFlavorMapper.insertBatch(flavorList);
        }

    }

    /**
     * 分页查询
     */


    public PageResult page(DishPageQueryDTO dishPageQueryDTO){
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //返回的值是Page,然后通过getTotal,getResult新建一个PageResult
        Page<DishVO> page=dishMapper.page(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids){
        //1.如果菜品启用中不能删除
        for(Long id:ids){
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus()==StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

        }
        //2.如果菜品关联了套餐，不能删除
        List<Long> setmealIds=setmealDishMapper.getSetmealIdsByDishIds(ids);
        //根据菜品查找出全部关联的套餐
        if(setmealIds!=null&&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //2.需要删除菜品本身还需要删除dish_flavor中的口味
        for(Long id:ids){
            //删除单个菜品
            dishMapper.delete(id);
            //删除菜品的所有口味
            dishFlavorMapper.deleteById(id);
        }

    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id){
        //根据id查询菜品数据
        Dish dish=dishMapper.getById(id);
        //根据菜品查询口味 数据
        List<DishFlavor> dishFlavorList=dishFlavorMapper.getByDishId(id);
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO){
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //1.修改菜品
        dishMapper.update(dish);
        //2.修改口味，删除原有的口味数据，插入新的口味数据

        //根据id删除全部口味数据
        dishFlavorMapper.deleteById(dishDTO.getId());

        //批量插入新的口味数据
       List<DishFlavor> flavorList=dishDTO.getFlavors();
       if(flavorList!=null&&flavorList.size()>0){
           for(DishFlavor dishFlavor:flavorList){
               dishFlavor.setDishId(dishDTO.getId());
           }
           dishFlavorMapper.insertBatch(flavorList);
       }
    }

    /**
     * 根据修改菜品状态
     * @param status
     */
    @Override
    public void updateStatus(Integer status,Long id) {
        dishMapper.updateStatus(status,id);
    }

}
