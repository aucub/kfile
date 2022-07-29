package com.example.kfile.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.example.kfile.exception.StorageSourceFileOperatorException;
import com.example.kfile.util.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 文件操作异常包装切面, 统一将文件操作异常包装为 {@link StorageSourceFileOperatorException} 异常
 */
@Aspect
@Component
@Slf4j
@Order(2)
public class FileOperatorExceptionWrapperAspect {


    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractBaseFileService.newFolder(..))")
    public void newExceptionWrapper(JoinPoint point, Throwable error) {
        String platform = "";
        String path = getArgStr(point, 0);
        String name = getArgStr(point, 1);
        String errMsg = StrUtil.format("新建文件夹失败, 文件路径: {}, 文件名: {}", path, name);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_NEW_FOLDER_FAIL, platform, errMsg, error);
    }

    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractBaseFileService.delete*(..))")
    public void deleteExceptionWrapper(JoinPoint point, Throwable error) {
        String platform = "";
        String path = getArgStr(point, 0);
        String name = getArgStr(point, 1);
        String errMsg = StrUtil.format("删除文件/文件夹失败, 文件路径: {}, 文件名: {}", path, name);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_DELETE_FAIL, platform, errMsg, error);
    }


    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractBaseFileService.rename*(..))")
    public void renameExceptionWrapper(JoinPoint point, Throwable error) {
        String platform = "";
        String path = getArgStr(point, 0);
        String name = getArgStr(point, 1);
        String newName = getArgStr(point, 2);
        String errMsg = StrUtil.format("重命名文件/文件夹失败, 文件路径: {}, 原文件名: {}, 修改为: {}", path, name, newName);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_RENAME_FAIL, platform, errMsg, error);
    }


    //@AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractBaseFileService.getDownloadUrl(..))")
    public void getDownloadUrl(JoinPoint point, Throwable error) {
        String platform = "";
        String pathAndName = getArgStr(point, 0);
        String errMsg = StrUtil.format("获取下载链接失败, 文件路径: {}", pathAndName);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_GET_UPLOAD_FAIL, platform, errMsg, error);
    }

    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractBaseFileService.getUploadUrl(..))")
    public void getUploadUrlExceptionWrapper(JoinPoint point, Throwable error) {
        String platform = "";
        String path = getArgStr(point, 0);
        String name = getArgStr(point, 1);
        String size = getArgStr(point, 2);
        String errMsg = StrUtil.format("获取文件上传链接失败, 文件路径: {}, 文件名: {}, 文件大小: {}", path, name, size);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_GET_UPLOAD_FAIL, platform, errMsg, error);
    }


    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractProxyTransferService.uploadFile(..))")
    public void proxyUploadExceptionWrapper(JoinPoint point, Throwable error) {
        String platform = "";
        String pathAndName = getArgStr(point, 0);
        String errMsg = StrUtil.format("文件代理上传失败, 文件路径: {}", pathAndName);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_PROXY_UPLOAD_FAIL, platform, errMsg, error);
    }


    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractProxyTransferService.downloadToStream(..))")
    public void proxyDownloadExceptionWrapper(JoinPoint point, Throwable error) {
        if (error instanceof ClientAbortException || error.getCause() instanceof ClientAbortException) {
            return;
        }
        String platform = "";
        String pathAndName = getArgStr(point, 0);
        String errMsg = StrUtil.format("文件代理下载失败, 文件路径: {}", pathAndName);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_PROXY_DOWNLOAD_FAIL, platform, errMsg, error);
    }


    @AfterThrowing(throwing = "error", value = "execution(public * com.example.kfile.service.AbstractBaseFileService.getFileItem(..))")
    public void getFileItemExceptionWrapper(JoinPoint point, Throwable error) {
        String platform = "";
        String pathAndName = getArgStr(point, 0);
        String errMsg = StrUtil.format("文件代理下载失败, 文件路径: {}", pathAndName);
        throw new StorageSourceFileOperatorException(CodeMsg.STORAGE_SOURCE_FILE_DISABLE_PROXY_DOWNLOAD, platform, errMsg, error);
    }

    /**
     * 获取切入点方法第 n 个参数
     *
     * @param point 切入点
     * @param index 参数索引
     * @return 参数值
     */
    private Object getArg(JoinPoint point, int index) {
        Object[] args = point.getArgs();
        return ArrayUtil.get(args, index);
    }


    private String getArgStr(JoinPoint point, int index) {
        Object arg = getArg(point, index);
        return Convert.toStr(arg);
    }

}
