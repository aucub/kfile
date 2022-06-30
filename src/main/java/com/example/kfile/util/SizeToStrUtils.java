package com.example.kfile.util;

import cn.hutool.core.util.NumberUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 文件大小或带宽大小转可读单位
 */
public class SizeToStrUtils {

    /**
     * 将文件大小转换为可读单位
     *
     * @param bytes 字节数
     * @return 文件大小可读单位
     */
    public static String bytesToSize(long bytes) {
        if (bytes == 0) {
            return "0";
        }
        double k = 1024;
        return getString(bytes, k);
    }

    @NotNull
    private static String getString(long bytes, double k) {
        String[] sizes = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        double i = Math.floor(Math.log(bytes) / Math.log(k));
        return NumberUtil.round(bytes / Math.pow(k, i), 3) + " " + sizes[(int) i];
    }


    /**
     * 将带宽大小转换为可读单位
     *
     * @param bps 字节数
     * @return 带宽大小可读单位
     */
    public static String bpsToSize(long bps) {
        if (bps == 0) {
            return "0";
        }

        double k = 1000;
        return getString(bps, k);
    }

}