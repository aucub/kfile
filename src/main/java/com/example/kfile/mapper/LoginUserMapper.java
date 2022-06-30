package com.example.kfile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kfile.dto.LoginUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
@Mapper
public interface LoginUserMapper extends BaseMapper<LoginUser> {

    void updateLoginDateByUsername(@Param("username") String username);

    void updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

}
