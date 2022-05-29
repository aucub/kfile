package com.example.kfile.aspect;

import com.example.kfile.domain.enums.FileOperatorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 校验文件操作权限
 */
@Aspect
@Component
@Slf4j
public class FileOperatorCheckAspect {

    /**
     * 新建文件/文件夹权限校验
     *
     * @param point 连接点
     * @return 方法运行结果
     */
    @Around("execution(public * com.example.kfile.service.AbstractBaseFileService.newFolder(..))")
    public Object newFolderAround(ProceedingJoinPoint point) throws Throwable {
        return check(point, FileOperatorTypeEnum.NEW_FOLDER);
    }

    /**
     * 删除文件/文件夹权限校验
     *
     * @param point 连接点
     * @return 方法运行结果
     */
    @Around("execution(public * com.example.kfile.service.AbstractBaseFileService.delete*(..))")
    public Object deleteAround(ProceedingJoinPoint point) throws Throwable {
        return check(point, FileOperatorTypeEnum.DELETE);
    }

    /**
     * 获取文件上传地址校验
     *
     * @param point 连接点
     * @return 方法运行结果
     */
    @Around("execution(public * com.example.kfile.service.AbstractBaseFileService.getUploadUrl(..))")
    public Object uploadAround(ProceedingJoinPoint point) throws Throwable {
        return check(point, FileOperatorTypeEnum.UPLOAD);
    }

    /**
     * 重命名文件/文件夹权限校验
     *
     * @param point 连接点
     * @return 方法运行结果
     */
    @Around("execution(public * com.example.kfile.service.AbstractBaseFileService.rename*(..))")
    public Object renameAround(ProceedingJoinPoint point) throws Throwable {
        return check(point, FileOperatorTypeEnum.RENAME);
    }

    /**
     * 校验是否有此文件操作的权限
     *
     * @param point            连接点
     * @param fileOperatorType 文件操作类型
     * @return 方法运行结果
     */
    private Object check(ProceedingJoinPoint point, FileOperatorTypeEnum fileOperatorType) throws Throwable {
        return point.proceed();
    }

}
