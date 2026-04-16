package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充切面类
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 设置公共切点
     *
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 自动补充创建时间或者更新时间
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行自动填充");

        //获取当前拦截方法
       MethodSignature signature = (MethodSignature) joinPoint.getSignature();
       //获取当前拦截方法的注释判断是什么操作
       AutoFill autoFill= signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType=autoFill.value();
        //获取被拦截方法的参数

        Object [] args=joinPoint.getArgs();
        if (args==null||args.length==0){
            return;
        }

        //获取第一个参数也就是实体类
        Object entity=args[0];

        //设置要传入的值
        LocalDateTime now=LocalDateTime.now();
        Long currentId= BaseContext.getCurrentId();

        if(operationType==OperationType.INSERT){
            //获取对象的方法
            try {
                Method setCreateTime=entity.getClass().getDeclaredMethod("setCreateTime",LocalDateTime.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
                Method setCreateUser=entity.getClass().getDeclaredMethod("setCreateUser",Long.class);
                Method setUpdateUser=entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);

                //执行方法
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
               e.printStackTrace();
            }
        }else{
            try {
                Method setUpdateTime=entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
                Method setUpdateUser=entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);

                //执行方法
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }


    }
}
