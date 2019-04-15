package com.bootdo.common.utils;

import com.wx.demo.tools.Constant;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {

    public static void uploadFile(String filePath, byte[] data) {
        String rootpath = new File(".").getAbsolutePath();
        if (-1 != rootpath.indexOf("bin")) {
            rootpath = rootpath.substring(0, rootpath.indexOf("bin")) + "webapps";
        }
//		filePath = rootpath +File.separator+ ROOT_PATH + filePath;
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        try {
            FileCopyUtils.copy(data, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile1(String filePath, byte[] data) {
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        try {
            FileCopyUtils.copy(data, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param file
     * @param modelName
     * @param realPath
     * @param deleteFlag
     * @return
     * @throws Exception
     */
    public static String uploadFiles(MultipartFile file, String modelName, String realPath, Boolean deleteFlag) throws Exception {
        return uploadFileAndClearOldFile(file, modelName + "_", realPath, deleteFlag);
    }

    /**
     * 确定要保存 把临时文件夹中的临时图片 保存到正式文件夹中
     * dirPath 为临时文件夹路径。默认的临时文件夹和正式文件夹路径区别 如：
     * 临时：/模块名/数据id/字段名_  正式：模块名/数据id/字段名
     *
     * @param dirPath
     * @return
     * @throws Exception
     * @author hy
     */
    public static String copyFileToDir(String dirPath) throws Exception {
        dirPath = dirPath.replace("/", File.separator);
        dirPath = dirPath.replace("\\", File.separator);
        //从所传的临时文件路径中 得到临时文件夹路径 去掉ROOT_PATH
        String ls_dirpath = dirPath.substring(0, dirPath.lastIndexOf(File.separator));
        String filename = dirPath.substring(dirPath.lastIndexOf(File.separator));
        //从临时文件夹路径中 获得正式文件夹路径 即比临时文件夹的字段名上 少了"_"
        String zs_dirpath = ls_dirpath.substring(0, dirPath.lastIndexOf("_"));

        //如果没有正式文件夹 就创建
        File zs_file = new File(zs_dirpath);
        if (!zs_file.exists()) {
            zs_file.mkdirs();
        } else {
            //删除原正式文件中的文件
            deleteDirAndFile(zs_dirpath);
            //创建正式的文件夹
            createDir(zs_dirpath);
        }

        copyFile(dirPath, new File(zs_dirpath + filename));
        String zs_filepath = zs_dirpath + File.separator + filename;
        zs_filepath = zs_filepath.replace("\\", "/");
        return zs_filepath;
    }

    /**
     * @param file
     * @param modelName
     * @param realPath
     * @param deleteFlag 是否删除已经上传过的文件
     * @return
     * @throws Exception
     */
    public static String uploadFileAndClearOldFile(MultipartFile file, String modelName, String realPath, Boolean deleteFlag) throws Exception {
        // 获取了文件整个名称及路径
        String attachName = file.getOriginalFilename();
        // 获取文件类型
        String fileType = attachName
                .substring((attachName.lastIndexOf(".")) + 1);
        String photo = System.currentTimeMillis() + "." + fileType;
//		String dirPath =  File.separator + modelName;  //modelName的文件夹的路径
        String dirPath = realPath + "/upfile/" + modelName;  //modelName的文件夹的路径
        String resPath = "/upfile/" + modelName + File.separator + photo;
        dirPath = dirPath.replace("/", File.separator);
        dirPath = dirPath.replace("\\", File.separator);
        String filePath = dirPath + File.separator + photo;  //modelName的文件夹下要上传文件的路径
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        if (deleteFlag) {
            // 先删除已有附件
            deleteDirAndFile(dirPath);
        }
        // 将文件上传到指定系统目录下
        createDir(dirPath);
        uploadFile(filePath, file.getBytes());
        //返回到前台的路径 要处理一下（把'\'都换成'/'）否则 前台显示不出来
        resPath = resPath.replace("\\", "/");
        return resPath;
    }

    /**
     * 上传文件
     *
     * @param file      文件名称
     * @param modelName 模块名称（分类名称）
     * @param realPath  真实路径（一般为获取tomcat下项目跟目录）
     * @param suffixFileType  规定的文件格式 不限制传Null
     * @return
     * @throws Exception
     */
    public static String uploadFile(MultipartFile file, String modelName, String realPath,String suffixFileType) throws Exception {
        // 获取了文件整个名称及路径
        String attachName = file.getOriginalFilename();
        // 获取文件类型
        String fileType = attachName
                .substring((attachName.lastIndexOf(".")) + 1);

        //校验文件格式
        if (StringUtils.isNotBlank(suffixFileType)&&StringUtils.isNotBlank(fileType)) {
            int existFitType = suffixFileType.indexOf(fileType);
            if (existFitType == -1) {
                return null;
            }
        }

        String fileName = System.currentTimeMillis() + "." + fileType;//创建文件名
        String dirPath = realPath + "/upfile/" + modelName;  //modelName的文件夹的路径
        String resPath = "/upfile/" + modelName + File.separator + fileName;
        dirPath = dirPath.replace("/", File.separator);
        dirPath = dirPath.replace("\\", File.separator);
        String filePath = dirPath + File.separator + fileName;  //modelName的文件夹下要上传文件的路径
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        // 将文件上传到指定系统目录下
        createDir(dirPath);
        uploadFile(filePath, file.getBytes());
        //返回到前台的路径 要处理一下（把'\'都换成'/'）否则 前台显示不出来
        resPath = resPath.replace("\\", "/");
        return resPath;
    }

    /**
     * 上传图片并且把图片按照比例缩下
     *
     * @param file      文件名称
     * @param modelName 模块名称（分类名称）
     * @param realPath  真实路径（一般为获取tomcat下项目跟目录）
     * @return
     * @throws Exception
     */
    public static Map<String, String> uploadImgFileAndScale(MultipartFile file, String modelName, String realPath) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        // 获取了文件整个名称及路径
        String attachName = file.getOriginalFilename();
        // 获取文件类型
        String fileType = attachName
                .substring((attachName.lastIndexOf(".")) + 1);
        String fileName = System.currentTimeMillis() + "." + fileType;//创建文件名
        //原始图片
        String dirPath = realPath + "/upfile/" + modelName;  //modelName的文件夹的路径
        createDir(dirPath);//判断文件夹是否存在，不存在就创建
        String filePath = dirPath + File.separator + fileName;  //modelName的文件夹下要上传文件的路径
        //缩小中等大小，缩放比例为50%
        String scaleMiddlePath = dirPath + "/middle/";
        createDir(scaleMiddlePath);//判断文件夹是否存在，不存在就创建
        String fileScaleMiddlePath = dirPath + "/middle/" + fileName;
        //缩小到最下，缩放比例为10%
        String scaleSmallPath = dirPath + "/small/";
        createDir(scaleSmallPath);//判断文件夹是否存在，不存在就创建
        String fileScaleSmallPath = dirPath + "/small/" + fileName;

        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        // 将文件上传到指定系统目录
        uploadFile(filePath, file.getBytes());
        //上传完成以后进行图片缩小
//        ImageUtil.scaleForProportion(filePath, fileScaleMiddlePath, 0.5f);//按照50%比例缩小
//        //上传完成以后进行图片缩小
//        ImageUtil.scaleForProportion(filePath, fileScaleSmallPath, 0.1f);//按照10%比例缩小

        //返回到前台的路径 要处理一下（把'\'都换成'/'）否则 前台显示不出来
        String resPath = "/upfile/" + modelName + File.separator + fileName;
        String resMiddlePath = "/upfile/" + modelName + "/middle/" + fileName;
        String resSmallPath = "/upfile/" + modelName + "/small/" + fileName;
        resPath = resPath.replace("\\", "/");
        resMiddlePath = resMiddlePath.replace("\\", "/");
        resSmallPath = resSmallPath.replace("\\", "/");
        map.put("resPath", resPath);
        map.put("resMiddlePath", resMiddlePath);
        map.put("resSmallPath", resSmallPath);
        return map;
    }

    /**
     * 下载文件
     *
     * @param filePath
     * @return
     */
    public static byte[] downloadFile(String filePath) {
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        File returnFile = new File(filePath);
        byte[] data = new byte[0];
        try {
            data = FileCopyUtils.copyToByteArray(returnFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * 下载文件
     *
     * @param filePath
     * @return
     */
    public static byte[] downloadFile_NoRootPath(String filePath) {
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        String path = filePath;
        File returnFile = new File(path);
        byte[] data = new byte[0];
        try {
            data = FileCopyUtils.copyToByteArray(returnFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void main(String[] args) {
        int s = "txt_html_jps".indexOf("txt1");
        System.out.println(s);

//        byte[] b=downloadFile_NoRootPath("/upfile/62data/1552191252754.txt");
//
//        if (b.length==0) {
//            System.out.println("xxxxxxxxxxxxxxxxxxxxxx");
//        }
//        InputStream in = new ByteArrayInputStream(b);
//        String a=HxFileUtils.file2String(in,Constant.DEFAULT_DECODE);
//        System.out.println(a);
//        BufferedReader rdr = new BufferedReader(new StringReader(a));
//        List<String> lines = new ArrayList<String>();
//        try {
//            for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
//                lines.add(line);
//            }
//            rdr.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(JSONUtils.beanToJson(lines));
    }

    /**
     * 根据文件路径删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        File returnFile = new File(filePath);
        returnFile.delete();
    }

    /**
     * 创建文件夹
     *
     * @author hy
     */
    public static void createDir(String dirPath) {
        dirPath.replace("\\", File.separator);
        dirPath.replace("/", File.separator);
        File channelDir = new File(dirPath);
        if (!channelDir.exists())
            channelDir.mkdirs();
    }

    /**
     * 删除目录
     *
     * @param dirPath
     */
    public static void deleteDir(String dirPath) {
        dirPath.replace("\\", File.separator);
        dirPath.replace("/", File.separator);
        File channelDir = new File(dirPath);
        if (channelDir.exists() && channelDir.isDirectory())
            channelDir.delete();
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dirPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirAndFile(String dirPath) {
        boolean flag = false;
        String rootpath = new File(".").getAbsolutePath();  //文件流的启动位置在tomcat的bin下
        if (-1 != rootpath.indexOf("bin")) {
            rootpath = rootpath.substring(0, rootpath.indexOf("bin")) + "webapps";
        }
//		dirPath = rootpath+File.separator+ROOT_PATH + dirPath;
        dirPath.replace("\\", File.separator);
        dirPath.replace("/", File.separator);
        // 如果dirPath不以文件分隔符结尾，自动添加文件分隔符
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        File dirFile = new File(dirPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                file = new File(file.getAbsolutePath());
                // 路径为文件且不为空则进行删除
                if (file.isFile() && file.exists()) {
                    file.delete();
                    flag = true;
                }
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirAndFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        return dirFile.delete();
    }

    public static void copyFile(String filePath, File f2) throws IOException {
        filePath = filePath.replace("/", File.separator);
        filePath = filePath.replace("\\", File.separator);
        File f1 = new File(filePath);
        FileCopyUtils.copy(f1, f2);
    }
}
