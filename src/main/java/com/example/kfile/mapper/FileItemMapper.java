package com.example.kfile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.result.FileEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author aucub
 * @since 2023-11-12
 */
@Mapper
public interface FileItemMapper extends BaseMapper<FileItem> {

    List<FileItem> findFileItemByDirectory(@Param("directory") String directory);

    FileEntry findFileEntryByFileItemId(@Param("fileItemId") String fileItemId);

}
