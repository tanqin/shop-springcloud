package com.shop.util;

import com.shop.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

/**
 * 文件上传
 * 文件删除
 * 文件下载
 * 文件信息获取
 * Storage 信息获取
 * Tracker 信息获取
 */
public class FastDFSUtil {
    /**
     * 加载 Tracker 链接信息
     */
    static {
        // 获取配置文件路径
        String filePath = new ClassPathResource("fdfs_client.conf").getPath();
        // 加载 Tracker 链接信息
        try {
            ClientGlobal.init(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] upload(FastDFSFile file) throws Exception {
        // 获取文件上传者信息
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("author", file.getAuthor());
        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取StorageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, null);
        //执行文件上传
        String[] uploads = storageClient.upload_file(file.getContent(), file.getExt(), metaList);
        return uploads;
    }
}
