package com.example.kfile.todo;

import cn.hutool.core.util.StrUtil;
import com.example.kfile.repository.StorageSourceRepository;
import com.example.kfile.util.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * TODO 存储源基本信息 Service
 */
@Slf4j
@Service
public class StorageSourceService {

    private StorageSourceRepository storageSourceRepository;

    @Autowired
    public void setStorageSourceRepository(StorageSourceRepository storageSourceRepository) {
        this.storageSourceRepository = storageSourceRepository;
    }

    /**
     * 获取所有存储源列表
     *
     * @return 存储源列表
     */
    public List<StorageSource> findAll() {
        return storageSourceRepository.findAll();
    }


    /**
     * 获取所有启用的存储源列表
     *
     * @return 已启用的存储源列表
     */
    public List<StorageSource> findAllEnable() {
        return storageSourceRepository.findByEnableStorageTrue();
    }


    /**
     * 获取指定存储源设置
     *
     * @param platform 存储源
     * @return 存储源设置
     */
    public StorageSource findById(String platform) throws FileNotFoundException {
        if (storageSourceRepository.findById(platform).isEmpty())
            throw new FileNotFoundException();
        return storageSourceRepository.findById(platform).get();
    }


    /**
     * 删除指定存储源
     *
     * @param platform 存储源
     */
    @Transactional(rollbackFor = Exception.class)
    public StorageSource deleteById(String platform) {
        if (storageSourceRepository.findById(platform).isEmpty()) {
            String msg = StrUtil.format("删除存储源时检测到 id 为 {} 的存储源不存在", platform);
            throw new StorageSourceException(CodeMsg.STORAGE_SOURCE_NOT_FOUND, platform, msg);
        }
        return storageSourceRepository.findById(platform).get();
    }


    public StorageSource save(StorageSource entity) {
        return storageSourceRepository.save(entity);
    }

}