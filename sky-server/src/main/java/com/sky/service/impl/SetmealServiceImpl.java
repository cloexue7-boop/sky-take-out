package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /*
    新增套餐
     */
    @Override
    @Transactional
    public void saveSetmeal(SetmealDTO setmealDTO) {
        //1.新增套餐基础信息到setmeal中，返回套餐主键
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);//默认停售
        setmealMapper.saveSetmeal(setmeal);
        Long setmealId=setmeal.getId();
        //2.新增套餐菜品到setmeal_dish中
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealId);

        }
        setmealDishMapper.insertBatch(setmealDishes);

    }

    /**
     * 套餐分页条件查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteSetmeal(List<Long> ids){
        //1.判断套餐是否在售，如果在手不能删除
        ids.forEach(id->{
            Setmeal setmeal=setmealMapper.getById(id);
            if(setmeal.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        //2.删除套餐以及套餐中关联的菜品关系数据
        ids.forEach(id->{
            setmealMapper.deleteSetmeal(id);
            setmealDishMapper.deleteSetmealDish(id);
        });
    }

    /**
     * 根据id查询套餐和菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id){
    //1.查询出套餐信息
        Setmeal setmeal=setmealMapper.getById(id);
        //2.查询出套餐和菜品关系的列表
        List<SetmealDish> setmealDishList=setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishList);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //1.修改套餐基本信息
        setmealMapper.updateSetmeal(setmeal);
        //2.批量删除全部套餐菜品
        setmealDishMapper.deleteSetmealDish(setmeal.getId());
        //3.批量新增全部套餐菜品
        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        setmealDishList.forEach(setmealDish->setmealDish.setSetmealId(setmealDTO.getId()));
        setmealDishMapper.insertBatch(setmealDishList);

    }


    /**
     * 修改套餐起售停售状态
     * @param status
     */
    @Override
    public void updateSetmealStatus(Integer status, Long id) {
        //1.如果是起售操作
        if(status.equals(StatusConstant.ENABLE)){
            //2.需要判断所有菜品是否停售
            List<Dish> dishes=dishMapper.getBySetmealId(id);
            dishes.forEach(dish -> {
                if(dish.getStatus().equals(StatusConstant.DISABLE)){
                   throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }

        //修改套餐状态
        Setmeal setmeal=Setmeal.builder()
                .id(id)
                .status(
                        status
                )
                .build();
        setmealMapper.updateSetmeal(setmeal);
    }
}
