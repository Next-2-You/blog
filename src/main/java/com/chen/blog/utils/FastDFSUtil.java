package com.chen.blog.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

public class FastDFSUtil {

    private static TrackerServer trackerServer = null;
    private static StorageServer storageServer = null;
    private static TrackerClient trackerClient = null;
    private static StorageClient storageClient = null;


    /**
     * 初始化
     */
    public static void init() {
        try {
            if (trackerClient == null) {
                ClientGlobal.init("fastdfs_client.properties");
                trackerClient = new TrackerClient();
                trackerServer = trackerClient.getConnection();
            }
            if (storageClient == null) {
                storageClient = new StorageClient(trackerServer, storageServer);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 多文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static List<String[]> upload(MultipartFile[] file) {
        init();
        String fileName = null;
        String fileType = null;
        byte[] bytes = null;
        List<String[]> list = new ArrayList<String[]>();
        String[] uploadResults = null;

        try {
            for (MultipartFile multipartFile : file) {
                fileName = multipartFile.getOriginalFilename();
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                bytes = multipartFile.getBytes();
                uploadResults = storageClient.upload_file(bytes, fileType, null);
                list.add(uploadResults);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 下载文件
     *
     * @param fileName：格式：group1/M00/00/00/wKi4eFysxiOAezsUAAACRhVCfG0830.txt
     * @return
     * @throws Exception
     */
    public static byte[] download(String fileName) {
        init();
        byte[] file = null;
        try {
            String group = fileName.substring(0, fileName.indexOf("/"));
            String path = fileName.substring(fileName.indexOf("/") + 1, fileName.length());
            System.out.println(group);
            System.out.println(path);
            if (group != null && group.trim() != "" && path != null && path.trim() != "") {
                file = storageClient.download_file(group, path);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 删除文件
     *
     * @param fileName：格式：group1/M00/00/00/wKi4eFysxiOAezsUAAACRhVCfG0830.txt
     * @return
     */
    public static boolean deleteFile(String fileName) {
        init();
        int i = -1;
        try {
            String group = fileName.substring(0, fileName.indexOf("/"));
            String path = fileName.substring(fileName.indexOf("/") + 1, fileName.length());
            i = storageClient.delete_file(group, path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i == 0 ? true : false;
    }

    /**
     * 字节数组上传
     *
     * @param bytes
     * @return
     */
    public static String[] upload(byte[] bytes,String fileType) {
        init();
        String[] uploadResults = null;
        try {
            uploadResults = storageClient.upload_file(bytes, fileType, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return uploadResults;
    }

}
