package com.example.kfile.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO 代理传输数据(上传/下载) Service
 */
public abstract class AbstractProxyTransferService extends AbstractBaseFileService {

    /**
     * 上传文件
     */
    public abstract void uploadFile(String fileId, InputStream inputStream);


    /**
     * 代理下载指定文件
     */
    public abstract void downloadToStream(String path, String url) throws IOException;

}