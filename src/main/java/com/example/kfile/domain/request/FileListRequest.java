package com.example.kfile.domain.request;

import com.example.kfile.domain.enums.OrderByTypeEnum;
import com.example.kfile.domain.enums.OrderDirectionTypeEnum;
import com.example.kfile.validation.StringListValue;
import lombok.Data;

/**
 * 获取文件夹下文件列表请求参数
 */
@Data
public class FileListRequest {
    //请求路径
    private String directory;

    @StringListValue(message = "排序字段参数异常，只能是 name、size、time", vals = {"name", "size", "time"})
    private OrderByTypeEnum orderBy;

    @StringListValue(message = "排序顺序参数异常，只能是 asc 或 desc", vals = {"asc", "desc"})
    private OrderDirectionTypeEnum orderDirection;

    public void handleDefaultValue() {
        if (orderBy.getValue().isEmpty()) {
            orderBy = OrderByTypeEnum.NAME;
        }
        if (orderDirection.getValue().isEmpty()) {
            orderDirection = OrderDirectionTypeEnum.ASC;
        }
    }

}