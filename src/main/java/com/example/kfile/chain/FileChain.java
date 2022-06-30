package com.example.kfile.chain;

import com.example.kfile.chain.command.FileAccessPermissionVerifyCommand;
import com.example.kfile.chain.command.FileSortCommand;
import com.example.kfile.chain.command.FileUrlAddVersionCommand;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.stereotype.Service;

/**
 * 文件处理责任链定义
 */
@Service
@Slf4j
public class FileChain extends ChainBase {

    @Resource
    private FileAccessPermissionVerifyCommand fileAccessPermissionVerifyCommand;

    @Resource
    private FileSortCommand fileSortCommand;

    @Resource
    private FileUrlAddVersionCommand fileUrlAddVersionCommand;

    /**
     * 初始化责任链
     */
    @PostConstruct
    public void init() {
        this.addCommand(fileAccessPermissionVerifyCommand);
        this.addCommand(fileSortCommand);
        this.addCommand(fileUrlAddVersionCommand);
    }

    /**
     * 执行文件处理责任链
     *
     * @param content 文件上下文
     * @return 是否执行成功
     */
    public FileContext execute(FileContext content) throws Exception {
        //super.execute(content);
        return content;
    }

}