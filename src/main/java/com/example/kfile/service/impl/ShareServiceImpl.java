package com.example.kfile.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.kfile.entity.Share;
import com.example.kfile.mapper.ShareMapper;
import com.example.kfile.service.IShareService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author aucub
 * @since 2023-11-12
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements IShareService {

}
