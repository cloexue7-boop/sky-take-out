package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result saveSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 分页条件查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询套餐：{}", setmealPageQueryDTO);
        PageResult pageResult=setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteSetmeal(@RequestParam List<Long> ids){
        log.info("删除套餐：{}", ids);
        setmealService.deleteSetmeal(ids);
        return Result.success();
    }

    /**
     * 查询回显，根据id查询套餐
     * @param id
     * @return
     */
   @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result getSetmealById(@PathVariable Long id){
        log.info("根据id查询套餐：{}", id);
       SetmealVO setmealVO=setmealService.getById(id);
       return Result.success(setmealVO);
   }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
   @PutMapping
    @ApiOperation("修改套餐")
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}", setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 修改套餐状态
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    public Result updateSetmealStatus(@PathVariable Integer status,Long id){
        log.info("修改套餐状态：{}", status,id);
        setmealService.updateSetmealStatus(status,id);
        return Result.success();
    }
}
