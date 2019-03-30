package com.wx.demo.tools;


import com.bootdo.util.HxHttpClient;
import com.google.common.collect.Maps;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
            conn.setRequestProperty("content-type", "");
            conn.setRequestProperty("user-agent", "MicroMessenger Client");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
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

    /**
     * POST请求获取数据
     */
    public static String postDownloadJson(String path,String post){
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            printWriter.write(post);//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while((len=bis.read(arr))!= -1){
                bos.write(arr,0,len);
                bos.flush();
            }
            bos.close();
            return bos.toString("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String post(String urlString, String param) {
        String result = null;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //post请求需要设置DoOutput为true
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            //设置参数

            urlConnection.getOutputStream().write(param.getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.setConnectTimeout(5 * 1000);
            urlConnection.setReadTimeout(5 * 1000);
            //连接服务器
            urlConnection.connect();
            StringBuilder stringBuilder = new StringBuilder();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = urlConnection.getInputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    stringBuilder.append(new String(buffer, 0, len));
                }
                result = stringBuilder.toString();
                is.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is);
        }
        return result;
    }


    public static String getSource(String urlString) {
        String result = null;
        InputStream in = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //post请求需要设置DoOutput为true
            urlConnection.setRequestMethod("GET");

            //连接服务器
            urlConnection.connect();
            StringBuilder stringBuilder = new StringBuilder();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                in = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(in));
                int len = 0;
                byte[] buffer = new byte[1024];
                String line = bufferedReader.readLine();
                StringBuffer temp = new StringBuffer();
                while (line != null) {
                    temp.append(line).append("\r\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                result = new String(temp.toString().getBytes(), "utf-8");


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }

        return result;
    }
    public static String doJsonPost(String urlPath, String Json) {
        // HttpClient 6.0被抛弃了
        String result = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
            conn.setRequestProperty("accept","application/json");
            // 往服务器里面发送数据
            if (Json != null && !StringUtil.isEmpty(Json)) {
                byte[] writebytes = Json.getBytes();
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
            }
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static String gets(String urlString) {
        String result = null;
        InputStream in = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //post请求需要设置DoOutput为true
            urlConnection.setRequestMethod("GET");
            //连接服务器
            urlConnection.connect();
            StringBuilder stringBuilder = new StringBuilder();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                in = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(in));
                int len = 0;
                byte[] buffer = new byte[1024];
                String line = bufferedReader.readLine();
                StringBuffer temp = new StringBuffer();
                while (line != null) {
                    temp.append(line).append("\r\n");
                    line = bufferedReader.readLine();
                }

                result = new String(temp.toString().getBytes(), "utf-8");
                bufferedReader.close();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
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
            conn.setRequestProperty("Accept-language", "zh-cn");
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


    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String sendPostReadTest(String url, Map<String,String> map,String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat QBCore/3.43.1021.400 QQBrowser/9.0.2524.400");

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

    public static void main(String[] args) {
//        String url = "https://mp.weixin.qq.com/mp/getappmsgext?uin=MjE5Mzg1ODIyMQ%253D%253D&key=0962614294a774607aca62e6412d65b132dfaabac7ad1020d2d587396aec52bf3c307cda606300d0af01023f46770ecc17b34d6c42d74ed52e2f93f4aa7274cd49c6ea5804b0c4c2a430ca2260acaff4";
//        String url = "https://mp.weixin.qq.com/mp/getappmsgext?uin=ODk4OTc0NDAx&key=81b6aa47e73a826f66e0ececf627f60c1f86074a2e4aa4832df4a752060c6248fa7bcf4772ea9ff495cadb6d1c6053d668d0d582537bec66c13db4bdc23918870cea27c34718ade1dd2726db47e08abe";
        String url = "https://mp.weixin.qq.com/mp/getappmsgext?uin=ODk4OTc0NDAx&key=d79f64025a3eaf7a7e4e7783636c71b79324f80b94fa9d6d883df5bc06e22eb362781541f7495821aae04818a1cd01922c631066a82ac60c182ef71cd895283ff1e332c7aee856a26ada057328ec0d54";

//        String param = "__biz=Mzg4OTAxNTY0NA%3D%3D&mid=2247484156&sn=0583f39be085fa1df5ae8de24e8e52b5&idx=3&is_only_read=1";
//        String param = "__biz=Mzg4OTAxNTY0NA%3D%3D&mid=2247484156&sn=a03526317e50b47617747ad68eb5627c&idx=5&is_only_read=1";
        String param = "__biz=Mzg4OTAxNTY0NA==&mid=2247484156&idx=5&sn=a03526317e50b47617747ad68eb5627c&ascene=7&devicetype=iPad+iPhone+OS9.3.3&version=16060520&nettype=WIFI&lang=zh-cn&fontScale=100&pass_ticket=SSsrwXWTZoshrST%2BDiyEIPuU49wzHBRXQfQ5mSgvKy7UatWl%2BeqnJCta65LN9cbd&wx_header=1&is_only_read=1";


        int paramDataIndex = url.indexOf("?");
        String paramData = url.substring(paramDataIndex + 1);
        System.out.println("------------------>"+paramData);
        String[] params=param.split("&");
        Map map=Maps.newHashMap();
        for (String str : params) {
            int eqindex = str.indexOf("=");
            if (eqindex!=-1) {
                String key = str.substring(0, eqindex);
                String value = str.substring(eqindex+1);
                map.put(key, value);
            }
        }
        System.out.println("--------------->>>>>>>>>>>>"+HxHttpClient.post(url,map));
//        System.out.println(JSONUtils.beanToJson(map));
        //System.out.println(sendPostReadTest(url,map, JSONUtils.beanToJson(map)));
    }




}
