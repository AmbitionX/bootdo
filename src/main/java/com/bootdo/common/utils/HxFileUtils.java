package com.bootdo.common.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class HxFileUtils {
    private static Logger logger = LoggerFactory.getLogger(HxFileUtils.class);

    /**
     * 读取输入流为一个内存字符串,保持文件原有的换行格式
     *
     * @param in      输入流
     * @param charset 文件字符集编码
     * @return 文件内容的字符串
     */

    public static String file2String(InputStream in, String charset) {
        StringBuffer sb = new StringBuffer();
        try {
            LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(in, charset)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            logger.error("读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + charset, e);
        } catch (IOException e) {
            logger.error("读取文件为一个内存字符串失败，失败原因是读取文件异常！", e);
        }
        return sb.toString();
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static String readFileByBytes(String fileName) {
        String retStr = "";
        File file = new File(fileName);
        InputStream in = null;
//        try {
//            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
//            // 一次读一个字节
//            in = new FileInputStream(file);
//            int tempbyte;
//            while ((tempbyte = in.read()) != -1) {
//                System.out.write(tempbyte);
//            }
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            //System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            retStr = file2String(in, "UTF-8");
            //showAvailableBytes(in);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
//            while ((byteread = in.read(tempbytes)) != -1) {
//                System.out.write(tempbytes, 0, byteread);
//            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        return retStr;
    }

    /**
     * @param fileName
     * @param content
     */
    public static void writer2files(String fileName, String content) {
        try {
            File file = new File(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            osw.write(content);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示输入流中还剩的字节数
     */

    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件的大小，计算出来文件真实的大小返回 KB,MB,GB
     *
     * @param length
     * @return
     */
    public static String getSize(Long length) {
        if (length == null) {
            return "0 KB";
        }
        long lengthKB = length / 1024;
        if (lengthKB < 1024) {
            if (length % 1024 > 0) {
                lengthKB++;
            }
            if (lengthKB == 1024) {
                return "1 MB";
            } else {
                return lengthKB + " KB";
            }
        }
        DecimalFormat format = new DecimalFormat("0.##");
        BigDecimal lengthMB = new BigDecimal(length).divide(new BigDecimal(
                1024 * 1024), 2, RoundingMode.HALF_DOWN);
        if (lengthMB.compareTo(new BigDecimal(1024)) < 0) {
            return format.format(lengthMB) + " MB";
        }
        BigDecimal lengthGB = lengthMB.divide(new BigDecimal(1024), 2,
                RoundingMode.HALF_DOWN);
        return format.format(lengthGB) + " GB";
    }

    /**
     * 生成随机名称
     *
     * @param extension 文件扩展名称
     * @return
     */
    public static String randomName(String extension) {
        StringBuilder name = new StringBuilder();
        name.append(System.currentTimeMillis());
        String random = RandomStringUtils.random(10, '0', 'Z', true, true);
        name.append(random.toLowerCase());
        if (StringUtils.isNotBlank(extension)) {
            name.append(".");
            name.append(extension);
        }
        return name.toString();
    }

    /**
     * Iterates over a base name and returns the first non-existent file.<br />
     * This method extracts a file's base name, iterates over it until the first
     * non-existent appearance with <code>basename(n).ext</code>. Where n is a
     * positive integer starting from one.
     *
     * @param file base file
     * @return first non-existent file
     */
    public static File getUniqueFile(final File file) {
        if (!file.exists())
            return file;

        File tmpFile = new File(file.getAbsolutePath());
        File parentDir = tmpFile.getParentFile();
        int count = 1;
        String extension = FilenameUtils.getExtension(tmpFile.getName());
        String baseName = FilenameUtils.getBaseName(tmpFile.getName());
        do {
            tmpFile = new File(parentDir, baseName + "(" + count++ + ")."
                    + extension);
        } while (tmpFile.exists());
        return tmpFile;
    }
}
