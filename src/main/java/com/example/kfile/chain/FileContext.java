package com.example.kfile.chain;

import ch.qos.logback.core.ContextBase;
import com.example.kfile.domain.FileInfo;
import com.example.kfile.domain.request.FileListRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文件处理责任链上下文
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class FileContext extends ContextBase {

    /**
     * 请求
     */
    private FileListRequest fileListRequest;

    /**
     * 文件列表
     */
    private List<FileInfo> fileInfos;

}