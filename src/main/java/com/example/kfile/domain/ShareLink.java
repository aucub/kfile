package com.example.kfile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 分享链接
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("ShareLink")
public class ShareLink implements Serializable {

    private static final long serialVersionUID = 1L;


    //链接, example = "voldd3"
    @Id
    private String url;


    //文件ID
    private String fileId;


    //创建时间, example = "2021-11-22 10:05"
    private Date createDate;


    //过期时间, example = "2021-11-23 10:05"
    private Date expireDate;


}