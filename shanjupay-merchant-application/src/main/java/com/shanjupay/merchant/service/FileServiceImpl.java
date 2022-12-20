package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.QiniuUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleUnresolved;
import java.sql.BatchUpdateException;

/**
 * @Author: admin
 * @Date: 2022/12/20 15:03
 * @Description:
 */

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${oss.qiniu.url}")
    private String qiniuUrl;
    @Value("${oss.qiniu.accessKey}")
    private String accessKey;
    @Value("${oss.qiniu.secretKey}")
    private String secretKey;
    @Value("${oss.qiniu.bucket}")
    private String bucket;

    /**
     * @description: 上传文件
     * @Param bytes:
     * @Param fileName:
     * @return: java.lang.String
     */
    @Override
    public String upload(byte[] bytes, String fileName) throws BatchUpdateException {

        try {
            //调用common下面的工具类
            QiniuUtils.upload2Qiniu(accessKey,secretKey,bucket,bytes,fileName);
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100106);
        }
        //返回文件名称
        return qiniuUrl + fileName;
    }
}
