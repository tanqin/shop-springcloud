package com.shop.util;

import com.shop.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String[] upload(FastDFSFile file) throws Exception {
        // 获取文件上传者信息
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("author", file.getAuthor());
        // 获取 TrackerServer 信息
        TrackerServer trackerServer = getTrackerServer();
        // 获取 StorageClient 对象
        StorageClient storageClient = getStorageClient(trackerServer);
        //执行文件上传
        String[] uploads = storageClient.upload_file(file.getContent(), file.getExt(), metaList);
        return uploads;
    }

    /**
     * 获取文件信息
     *
     * @param groupName      文件组名
     * @param remoteFileName 文件存储路径名
     * @return
     * @throws Exception
     */
    public static FileInfo getFile(String groupName, String remoteFileName) throws Exception {
        // 获取 TrackerServer 信息
        TrackerServer trackerServer = getTrackerServer();
        // 获取 StorageClient 对象
        StorageClient storageClient = getStorageClient(trackerServer);
        // 获取文件信息
        return storageClient.get_file_info(groupName, remoteFileName);
    }

    /**
     * 文件下载
     *
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) throws Exception {
        // 获取 TrackerServer 信息
        TrackerServer trackerServer = getTrackerServer();
        // 获取 StorageClient 对象
        StorageClient storageClient = getStorageClient(trackerServer);
        // 文件下载
        byte[] buffer = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(buffer);
    }

    /**
     * 文件删除
     *
     * @param groupName
     * @param remoteFileName
     * @throws Exception
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        // 获取 TrackerServer 信息
        TrackerServer trackerServer = getTrackerServer();
        // 获取 StorageClient 对象
        StorageClient storageClient = getStorageClient(trackerServer);
        // 删除文件
        storageClient.delete_file(groupName, remoteFileName);
    }

    /**
     * 获取 Storage 信息
     *
     * @return
     * @throws Exception
     */
    public static StorageServer getStorages() throws Exception {
        // 创建一个 TrackerClient 对象，通过 TrackerClient 访问 TrackerServer 对象
        TrackerClient trackerClient = new TrackerClient();
        // 通过 TrackerClient 获取 TrackerServer 的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        // 获取 storage 信息
        return trackerClient.getStoreStorage(trackerServer);
    }

    /**
     * 获取 Storage 组的 IP 和端口信息
     *
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) throws Exception {
        // 创建一个 TrackerClient 对象，通过 TrackerClient 访问 TrackerServer 对象
        TrackerClient trackerClient = new TrackerClient();
        // 通过 TrackerClient 获取 TrackerServer 的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    /**
     * 获取 Tracker 服务地址
     *
     * @return
     * @throws Exception
     */
    public static String getTrackerInfo() throws Exception {
        // 获取 TrackerServer 信息
        TrackerServer trackerServer = getTrackerServer();
        // 获取 Tracker 的 IP
        String ip = trackerServer.getInetSocketAddress().getHostString();
        // 获取 Tracker 的端口
        int port = ClientGlobal.getG_tracker_http_port();
        return "http://" + ip + ":" + port + "/";
    }

    /**
     * 获取 TrackerServer
     *
     * @return
     * @throws IOException
     */
    public static TrackerServer getTrackerServer() throws IOException {
        // 创建一个 TrackerClient 对象，通过 TrackerClient 访问 TrackerServer 对象
        TrackerClient trackerClient = new TrackerClient();
        // 通过 TrackerClient 获取 TrackerServer 的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }

    /**
     * 获取 storageClient
     *
     * @param trackerServer
     * @return
     */
    public static StorageClient getStorageClient(TrackerServer trackerServer) {
        // 通过 TrackerServer 获取 Storage 对象，创建 StorageClient 对象存储 Storage 信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    public static void main(String[] args) throws Exception {
       /* FileInfo fileInfo = getFile("group1", "M00/00/00/wKjThGRTpEuAQvhHAAGCnk6H7-w659.png");
        System.out.println(fileInfo.getSourceIpAddr());
        System.out.println(fileInfo.getFileSize());*/

        // 文件下载
        /*InputStream is = downloadFile("group1", "M00/00/00/wKjThGRTpEuAQvhHAAGCnk6H7-w659.png");

        // 将文件写入本地磁盘
        FileOutputStream os = new FileOutputStream("D:/1.png");

        // 定义一个缓存区
        byte[] buffer = new byte[1024];
        while (is.read(buffer) != -1) {
            os.write(buffer);
        }
        os.flush();
        os.close();
        is.close();*/

        // 删除下载
//        deleteFile("group1", "M00/00/00/wKjThGRTpEuAQvhHAAGCnk6H7-w659.png");

        // 获取 Storage 信息
      /*  StorageServer storageServer = getStorages();
        System.out.println(storageServer.getStorePathIndex()); // 0
        System.out.println(storageServer.getInetSocketAddress().getHostString()); // 192.168.211.132*/

        // 获取 Storage 组的 IP 和端口信息
       /* ServerInfo[] groups = getServerInfo("group1", "M00/00/00/wKjThGRTpmCAGF3RAAGCnk6H7-w934.png");

        for (ServerInfo group : groups) {
            System.out.println(group.getIpAddr()); // 192.168.211.132
            System.out.println(group.getPort()); // 23000
        }*/

        // 获取 Tracker 服务地址
        System.out.println(getTrackerInfo()); // http://192.168.211.132:8080/
    }

}
