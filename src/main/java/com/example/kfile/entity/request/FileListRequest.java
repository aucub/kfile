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
    //请求路径
    private String directory;

    @Valid
    private OrderByTypeEnum orderBy;

    @Valid
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