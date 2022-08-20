package com.example.kfile.util;

import com.example.kfile.domain.FileInfo;
import com.example.kfile.domain.request.FileListRequest;

import java.util.List;

/**
 * 文件排序,根据请求类中的排序参数，进行文件排序.
 */
public class FileSortUtil {
    public static void sort(List<FileInfo> fileInfos, FileListRequest fileListRequest) {
        FileComparator fileComparator = new FileComparator(fileListRequest.getOrderBy().getValue(), fileListRequest.getOrderDirection().getValue());
        fileInfos.sort(fileComparator);
    }
}