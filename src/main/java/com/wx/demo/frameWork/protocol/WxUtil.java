package com.wx.demo.frameWork.protocol;

import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * @Author zjy
 * @Description:
 * @Date 2018/12/14
 */
public class WxUtil {

    /**
     * 获取网络文件 byte数组
     *
     * @param fileUrl 目标文件url
     * @return
     */
    public static byte[] downFileBytes(String fileUrl) {
        File f2 = new File(WxUtil.class.getResource("").getPath());
        String sub = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        String path = f2.getPath() + "/" + sub;
        File file = new File(path);

        if (file.exists()) {
            return getFile(path);
        }

        URL url = null;
        try {
            url = new URL(fileUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();


            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context = output.toByteArray();
            fileOutputStream.write(context);
            dataInputStream.close();
            fileOutputStream.close();
            return context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  获取本地图片byte数组
     * @param path 本地图片路径
     * @return
     */
    public static byte[] getFile(String path) {
        File file = new File(path);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        try{
            FileInputStream fi = new FileInputStream(file);
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            fi.close();
            return buffer;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 生成appMsg xml
     *
     * @param title    标题
     * @param content  内容
     * @param pointUrl 指向url
     * @param thumburl 缩略图url
     * @return
     */
    public static String getAppMsgXml(String title, String content, String pointUrl, String thumburl) {

        StringBuilder sb = new StringBuilder("<appmsg> ");
        sb.append("<title>").append(title).append("</title>");
        sb.append("<des>").append(content).append("</des>");
        sb.append("<action>").append("view").append("</action>");
        sb.append("<type>").append(5).append("</type>");
        sb.append("<showtype>").append(0).append("</showtype>");
        sb.append("<url>").append(pointUrl).append("</url>");
        sb.append("<thumburl>").append(thumburl).append("</thumburl>");
        sb.append("</appmsg>");

        return sb.toString();
    }

    /**
     * 文字朋友圈xml
     * @param content
     * @param userName
     * @return
     */
    public static String getTextMomentXml(String content, String userName) {
        String xml = "<TimelineObject>" +
                "    <id>0</id>" +
                "    <username>" + userName + "</username>" +
                "    <createTime>0</createTime>" +
                "    <contentDesc>" + content + "</contentDesc>" +
                "    <contentDescShowType>0</contentDescShowType>" +
                "    <contentDescScene>3</contentDescScene>" +
                "    <private>0</private>" +
                "    <sightFolded>0</sightFolded>" +
                "    <showFlag>0</showFlag>" +
                "    <appInfo>" +
                "        <id></id>" +
                "        <version></version>" +
                "        <appName></appName>" +
                "        <installUrl></installUrl>" +
                "        <fromUrl></fromUrl>" +
                "        <isForceUpdate>0</isForceUpdate>" +
                "    </appInfo>" +
                "    <sourceUserName></sourceUserName>" +
                "    <sourceNickName></sourceNickName>" +
                "    <statisticsData></statisticsData>" +
                "    <statExtStr></statExtStr>" +
                "    <ContentObject>" +
                "        <contentStyle>2</contentStyle>" +
                "        <title></title>" +
                "        <description></description>" +
                "        <mediaList></mediaList>" +
                "        <contentUrl></contentUrl>" +
                "    </ContentObject>" +
                "    <actionInfo>" +
                "        <appMsg>" +
                "            <messageAction></messageAction>" +
                "        </appMsg>" +
                "    </actionInfo>" +
                "    <location poiClassifyId=\"\" poiName=\"\" poiAddress=\"\" poiClassifyType=\"0\" city=\"\"></location>" +
                "    <publicUserName></publicUserName>" +
                "    <streamvideo>" +
                "        <streamvideourl></streamvideourl>" +
                "        <streamvideothumburl></streamvideothumburl>" +
                "        <streamvideoweburl></streamvideoweburl>" +
                "    </streamvideo>" +
                "</TimelineObject>";
        return xml;
    }

    /**
     *  图文朋友圈xml
     * @param userName
     * @param content
     * @param map
     * @return
     */
    public static String getImgMomentXml(String userName, String content, Map<String, String> map) {
        String xml = "<TimelineObject>" +
                "    <id>0</id>" +
                "    <username>" + userName + "</username>" +
                "    <createTime>0</createTime>" +
                "    <contentDesc>" + content + "</contentDesc>" +
                "    <contentDescShowType>0</contentDescShowType>" +
                "    <contentDescScene>3</contentDescScene>" +
                "    <private>0</private>" +
                "    <sightFolded>0</sightFolded>" +
                "    <showFlag>0</showFlag>" +
                "    <appInfo>" +
                "        <id></id>" +
                "        <version></version>" +
                "        <appName></appName>" +
                "        <installUrl></installUrl>" +
                "        <fromUrl></fromUrl>" +
                "        <isForceUpdate>0</isForceUpdate>" +
                "    </appInfo>" +
                "    <sourceUserName></sourceUserName>" +
                "    <sourceNickName></sourceNickName>" +
                "    <statisticsData></statisticsData>" +
                "    <statExtStr></statExtStr>" +
                "    <ContentObject>" +
                "        <contentStyle>1</contentStyle>" +
                "        <title></title>" +
                "        <description></description>" +
                "        <mediaList>";
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            xml += "            <media>" +
                    "                <id>0</id>" +
                    "                <type>2</type>" +
                    "                <title></title>";
            if (i == 0) {
                xml += "                <description>" + content + "</description>";
            } else {
                xml += "                <description></description>";
            }
            xml += "                <private>0</private>" +
                    "                <userData></userData>" +
                    "                <subType>0</subType>" +
                    "                <videoSize width=\"\" height=\"\"></videoSize>" +
                    "                <url type=\"1\" videomd5=\"\">" + key + "</url>" +
                    "                <thumb type=\"1\">" + value + "</thumb>" +
                    "                <size width=\"\" height=\"\" totalSize=\"0\"></size>" +
                    "            </media>";
            i++;
        }
        xml += "        </mediaList>" +
                "        <contentUrl></contentUrl>" +
                "    </ContentObject>" +
                "    <actionInfo>" +
                "        <appMsg>" +
                "            <messageAction></messageAction>" +
                "        </appMsg>" +
                "    </actionInfo>" +
                "    <location poiClassifyId=\"\" poiName=\"\" poiAddress=\"\" poiClassifyType=\"0\" city=\"\"></location>" +
                "    <publicUserName></publicUserName>" +
                "    <streamvideo>" +
                "        <streamvideourl></streamvideourl>" +
                "        <streamvideothumburl></streamvideothumburl>" +
                "        <streamvideoweburl></streamvideoweburl>" +
                "    </streamvideo>" +
                "</TimelineObject>";

        return xml;
    }


    public static void main(String[] args) {
        String pa = "https://csdnimg.cn/pubfooter/images/csdn-kf.png";
        System.out.println(downFileBytes(pa));
    }


}
