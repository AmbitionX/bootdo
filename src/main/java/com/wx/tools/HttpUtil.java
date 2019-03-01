package com.wx.tools;


import com.bootdo.common.utils.JSONUtils;
import com.google.common.collect.Maps;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
public class HttpUtil {
    private static Logger logger = Logger.getLogger(HttpUtil.class);
    private static OkHttpClient client;
    private static HttpUtil util = new HttpUtil();
    private HttpUtil() {
        client = new OkHttpClient.Builder().followRedirects(false).readTimeout(15, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).connectTimeout(1, TimeUnit.SECONDS).build();
    }
    public static byte[] get(String url) throws IOException {
//        logger.info(url);
        Request.Builder req = new Request.Builder().url(url).
                header("Accept", "*/*").
                header("Content-Type", "application/octet-stream").
//                header("Connection", "keep-alive").
        header("User-Agent", "MicroMessenger Client").get();
        Call call2 = client.newCall(req.build());
        Response arg1 = call2.execute();
        byte[] bytes = arg1.body().bytes();
        return bytes;
    }
    public byte[] get(String url, long timeStamp, String sign) throws IOException {
//        logger.info(url);
        Request.Builder req = new Request.Builder().url(url).
                header("Accept", "*/*").
                header("Content-Type", "application/octet-stream").
//                header("Connection", "keep-alive").
        header("assistant-sign", sign).
                        header("assistant-expireTime", String.valueOf(timeStamp)).
                        header("User-Agent", "MicroMessenger Client").get();
        Call call2 = client.newCall(req.build());
        Response arg1 = call2.execute();
        byte[] bytes = arg1.body().bytes();
        return bytes;
    }
    public static HttpUtil getUtil() {
        return util;
    }
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("content-type", "application/octet-stream");
            conn.setRequestProperty("user-agent", "MicroMessenger Client");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Language", "zh-cn");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param.getBytes());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            logger.info(e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String sendPostRead(String url, Map<String,String> map) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("content-type", "application/octet-stream");
            conn.setRequestProperty("user-agent", "MicroMessenger Client");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Language", "zh-cn");
            conn.setRequestProperty("X-WECHAT-KEY", map.get("key"));
            conn.setRequestProperty("X-WECHAT-UIN", map.get("uin"));

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
//            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
//            out.print(param.getBytes());
            // flush输出流的缓冲
//            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            logger.info(e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private static int[] byte2Int(byte[] bys) {
        int[] res = new int[bys.length];
        for (int x = 0; x < bys.length; x++) {
            res[x] = bys[x] & 0xff;
        }
        return res;
    }

    public static void main(String[] args) {
        String xkey = "bd28fb68c380f98a3c23b934be97014acc236c95d62bccc1c24e2db9e8a34dd5cd1a531bce3118fa119b7d0041dcf13ca1eb5d84d5f49f5754154aa52aba6aee9c8862d8ae4c8b479a0a054093eac594";
        String uin = "ODk4OTc0NDAx";
        String reqUrl = "https://mp.weixin.qq.com/s?__biz=MzUyNTkyMDQ3MQ==&mid=2247484903&idx=2&sn=201b9dde5bb63fab8ef59e75f5e012ad&ascene=7&devicetype=iPad+iPhone+OS9.3.3&version=16060520&nettype=WIFI&lang=zh_CN&fontScale=100&pass_ticket=WgV8VQCjZKqXTCzseC4FkkVertV7rZ7un%2Fsc6JK27ReoT1XaD4o0orTMN1TvZLFB&wx_header=1" ;
//                "&X-WECHAT-KEY=" +xkey+
//                "&X-WECHAT-UIN="+uin;
        Map<String,String> map= Maps.newHashMap();
        map.put("X-WECHAT-KEY", xkey);
        map.put("X-WECHAT-UIN", uin);
        String json = JSONUtils.beanToJson(map);
        System.out.println(json);
        //byte方式    这里main方法直接调下面的方法 post 各种方式等下 语音说， 好
//        System.out.println(sendPost(reqUrl, ConfigService.strTo16(JSONUtils.beanToJson(json))));
//        System.out.println(sendPost(reqUrl, JSONUtils.beanToJson(json)));
//        System.out.println(sendPost(reqUrl, json));
        System.out.println(sendPostRead(reqUrl, map));
        //form方式
//        post(reqUrl, map);
        //json
//        postJson(reqUrl, json);
        //jsonBytes
//        postBytes(reqUrl, json);
    }
}
