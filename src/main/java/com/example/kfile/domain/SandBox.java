package com.example.kfile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("SandBox")
public class SandBox {

    @Id
    private String fileItemId;
    private String name;
    private String owner;
    private String share;
    private Long usedSpace;
    private String describe;
}
