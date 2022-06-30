package com.example.kfile.entity.result;

import com.example.kfile.entity.FileItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FileEntry extends FileItem {

    private Long size;

    private String sha256sum;
}
