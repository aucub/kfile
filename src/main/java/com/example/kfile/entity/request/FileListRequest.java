package com.example.kfile.entity.request;

import com.example.kfile.entity.enums.OrderByTypeEnum;
import com.example.kfile.entity.enums.OrderDirectionTypeEnum;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * 获取文件夹下文件列表请求参数
 */
@Data
public class FileListRequest {
    private String directory="";

    @Valid
    private OrderByTypeEnum orderBy= OrderByTypeEnum.NAME;

    @Valid
    private OrderDirectionTypeEnum orderDirection= OrderDirectionTypeEnum.ASC;

}