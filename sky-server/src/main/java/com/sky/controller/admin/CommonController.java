package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;

import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传操作
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file.getOriginalFilename());

       try{
           //原始文件名称
           String originalFilename = file.getOriginalFilename();
           //原始文件名称的后缀
           String extension=originalFilename.substring(originalFilename.lastIndexOf("."));

           //UUID设置文件名称
           String fileName= UUID.randomUUID().toString()+extension;

           //进行文件上传操作
           String filePath= aliOssUtil.upload(file.getBytes(),fileName);
           return Result.success(filePath);
       }catch (Exception e){
           log.error("文件上传失败：{}", e.getMessage());
       }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
