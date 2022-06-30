package com.example.kfile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * 存储源过滤规则 Service
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "filterConfig")
public class FilterConfigService {


    /**
     * TODO 判断访问的路径是否是不允许访问的
     *
     * @param   storageId
     *          存储源 ID
     *
     * @param   path
     *          请求路径
     *
     */
    /*public boolean checkFileIsInaccessible(Integer storageId, String path) {
        List<FilterConfig> filterConfigList = filterConfigService.findByStorageIdAndInaccessible(storageId);
        return testPattern(storageId, filterConfigList, path);
    }*/

    /**TODO 判断文件是否禁止下载
     * 指定存储源下的文件名称, 根据过滤表达式判断文件名和所在路径是否禁止下载, 如果符合任意一条表达式, 则返回 true, 反之则返回 false.
     *
     * @param   storageId
     *          存储源 ID
     *
     * @param   fileName
     *          文件名
     *
     * @return 是否显示
     */
    /*public boolean checkFileIsDisableDownload(Integer storageId, String fileName) {
        List<FilterConfig> filterConfigList = filterConfigService.findByStorageIdAndDisableDownload(storageId);
        String filePath = FileUtil.getParent(fileName, 1);
        if (StrUtil.isEmpty(filePath)) {
            return testPattern(storageId, filterConfigList, fileName);
        } else {
            return testPattern(storageId, filterConfigList, fileName) || testPattern(storageId, filterConfigList, filePath);
        }
    }
    */

}