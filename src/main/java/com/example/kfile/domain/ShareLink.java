package com.example.kfile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件分享
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("ShareLink")
public class ShareLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    //链接, example = "voldd3"
    @Id
    private String url;


    //文件ID
    private String fileItemId;

    //访问范围, example = "public","aclist","users"
    private String acl;

    private List<String> users;
    //权限,4=access,2=download,1=upload
    private List<Integer> aclist;

    private String password;

    private int allow;


    //创建时间, example = "2021-11-22 10:05"
    private Date createDate;


    //过期时间, example = "2021-11-23 10:05"
    private Date expireDate;


}