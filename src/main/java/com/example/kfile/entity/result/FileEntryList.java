package com.example.kfile.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 文件列表信息结果类
 */
@Data
@AllArgsConstructor
public class FileEntryList {

    //文件列表
    private List<FileEntry> files;

}