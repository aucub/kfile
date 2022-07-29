package com.example.kfile.domain.request;

import lombok.Data;

@Data
public class PageQueryRequest {

    private Integer page = 1;

    private Integer limit = 10;

    private String orderBy = "create_date";

    private String orderDirection = "desc";

}