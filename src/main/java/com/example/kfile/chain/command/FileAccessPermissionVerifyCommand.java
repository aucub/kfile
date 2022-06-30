package com.example.kfile.chain.command;

import cn.hutool.core.util.StrUtil;
import com.example.kfile.chain.FileContext;
import com.example.kfile.domain.request.FileListRequest;
import com.example.kfile.exception.StorageSourceException;
import com.example.kfile.service.FilterConfigService;
import com.example.kfile.util.CodeMsg;
import jakarta.annotation.Resource;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.stereotype.Service;

/**
 * TODO 目录访问权限责任链 command 命令
 *      检查请求的目录是否有访问权限
 */
@Service
public class FileAccessPermissionVerifyCommand implements Command {

    @Resource
    private FilterConfigService filterConfigService;

    /**
     * 校验是否有权限访问此目录
     *
     * @param context 文件处理责任链上下文
     * @return 是否停止执行责任链, true: 停止执行责任链, false: 继续执行责任链
     */
    @Override
    public boolean execute(Context context) throws Exception {
        FileContext fileContext = (FileContext) context;
        //Integer storageId = fileContext.getStorageId();
        FileListRequest fileListRequest = fileContext.getFileListRequest();

        // 检查文件目录是否是不可访问的, 如果是则抛出异常
        //boolean isInaccessible = filterConfigService.checkFileIsInaccessible(storageId, fileListRequest.getPath());

        if (true) {
            String errorMsg = StrUtil.format("文件目录 [{}] 无访问权限", fileListRequest.getPath());
            throw new StorageSourceException(CodeMsg.STORAGE_SOURCE_FILE_FORBIDDEN, "storageId", errorMsg);
        }

        return false;
    }

}