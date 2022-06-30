package com.example.kfile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kfile.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
