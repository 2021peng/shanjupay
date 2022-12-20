package com.shanjupay.common.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @Author: admin
 * @Date: 2022/12/17 17:09
 * @Description:七牛云测试工具类
 */
public class QiniuUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(QiniuUtils.class);
    /**
     * @description:提供上传工具的方法
     * @Param accessKey:
     * @Param secretKey:
     * @Param bucket:
     * @Param bytes:
     * @Param fileName:七牛云上的文件名和此保持一致
     * @return: void
     */
    public static void upload2Qiniu(String accessKey, String secretKey,
                                    String bucket, byte[] bytes,
                                    String fileName)throws RuntimeException{
        //构造一个带指定 Region 对象的配置类,指定存储区域
        Configuration cfg = new Configuration(Region.huadong());
//        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        try {

            //认证
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                //上传文件
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                LOGGER.error("上传文件到七牛云：{}",ex.getMessage());
                try {
                    LOGGER.error(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
                throw new RuntimeException(r.bodyString());
            }
        } catch (Exception ex) {
            //ignore
            LOGGER.error("上传文件到七牛云：{}",ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    //测试上传文件
    private static void testUpload() throws IOException {
        //构造一个带指定 Region 对象的配置类,指定存储区域
        Configuration cfg = new Configuration(Region.huadong());
//        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "0UlpPjCg2JeDCojPfEuSG8eT1agL6KP7hWpas1ut";
        String secretKey = "FwRKLfKv8ARuiutW0iRUPLqIWHmINv8jLgEyz-FY";
        String bucket = "shanjupay-pxy2";

//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = UUID.randomUUID().toString() + ".png";
        FileInputStream fileInputStream = null;
        try {
            //得到本地文件的字节数组
            String filePath = "E:\\cache\\404-error.png";
            fileInputStream = new FileInputStream(new File(filePath));
            byte[] bytes = IOUtils.toByteArray(fileInputStream);

//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            //认证
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                //上传文件
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //ignore
        }

    }

    public static void getDownload() throws UnsupportedEncodingException {
        String fileName = "89b67f09-65f1-4250-876b-f1b2b11760a6.png";
        String domainOfBucket = "http://rn16vr4tk.hd-bkt.clouddn.com/";//域名
        String encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);

//        String accessKey = "your access key";
//        String secretKey = "your secret key";
        String accessKey = "0UlpPjCg2JeDCojPfEuSG8eT1agL6KP7hWpas1ut";
        String secretKey = "FwRKLfKv8ARuiutW0iRUPLqIWHmINv8jLgEyz-FY";
//        String bucket = "shanjupay-pxy2";
        Auth auth = Auth.create(accessKey, secretKey);
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        System.out.println(finalUrl);

    }

    public static void main(String[] args) throws IOException {
        //测试上传
        QiniuUtils.testUpload();

        //测试下载
        QiniuUtils.getDownload();
    }
}
