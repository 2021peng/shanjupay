package com.shanjupay.merchant.service;

import org.springframework.stereotype.Service;

import java.sql.BatchUpdateException;

/**
 * @Author: admin
 * @Date: 2022/12/17 19:10
 * @Description:
 */

//@Service
public interface FileService {

    /**
     * 上传文件
     * @param bytes 文件字节
     * @param fileName 文件名称
     * @return 文件下载(返回)路径
     * @throws BatchUpdateException
     */
    public String upload(byte[] bytes,String fileName) throws BatchUpdateException;

}
