package com.bootdo.util;

import com.bootdo.common.utils.JSONUtils;
import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author lgl
 */
public class HxHttpClient {
    private static Logger logger = LoggerFactory.getLogger(HxHttpClient.class);

    /**
     * @param url 请求的全路径 例如：http://localhost:8088/json.html
     * @return 返回json字符串
     */
    public static String get(String url) {
        String returnStr = "";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(url);
            /**
             * setConnectTimeout(20000)：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout(20000) 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
             * setSocketTimeout(20000) 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectionRequestTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);
            /*
             * 判断HTTP的返回状态码，如果正确继续解析返回的数据
			 */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//HttpStatus.SC_OK=200
                returnStr = EntityUtils.toString(response.getEntity());
            } else {
                returnStr = "{\"code\":\"500\",\"msg\":\"请求地址异常("+response.getStatusLine().getStatusCode()+")\",\"content\":\"\",\"extendData\":\"\"}";
                logger.info(">>>>>>>httpClient Get 请求地址异常---》url:" + url);
            }
        } catch (ClientProtocolException e) {
            logger.error(">>>>>>>httpClient Get ClientProtocolException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"601\",\"msg\":\"请求地址异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Get ClientProtocolException 异常---》url:" + url);
        } catch (IOException e) {
            logger.error(">>>>>>>httpClient Get IOException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"602\",\"msg\":\"IO异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Get IOException 异常---》url:" + url);
        }
        return returnStr;
    }

    /**
     * @param url 请求的全路径 例如：http://localhost:8088/json.html
     * @return 返回json字符串
     */
    public static String getByHeader(String url,Map<String,String> map) {
        String returnStr = "";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(url);
            /**
             * setConnectTimeout(20000)：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout(20000) 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
             * setSocketTimeout(20000) 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectionRequestTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpGet);
            /*
             * 判断HTTP的返回状态码，如果正确继续解析返回的数据
             */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//HttpStatus.SC_OK=200
                returnStr = EntityUtils.toString(response.getEntity());
            } else {
                returnStr = "{\"code\":\"500\",\"msg\":\"请求地址异常("+response.getStatusLine().getStatusCode()+")\",\"content\":\"\",\"extendData\":\"\"}";
                logger.info(">>>>>>>httpClient Get 请求地址异常---》url:" + url);
            }
        } catch (ClientProtocolException e) {
            logger.error(">>>>>>>httpClient Get ClientProtocolException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"601\",\"msg\":\"请求地址异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Get ClientProtocolException 异常---》url:" + url);
        } catch (IOException e) {
            logger.error(">>>>>>>httpClient Get IOException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"602\",\"msg\":\"IO异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Get IOException 异常---》url:" + url);
        }
        return returnStr;
    }

    /**
     * 表单方式请求
     * @param url            请求的全路径 例如：http://localhost:8088/json.html
     * @param postParameters 使用post的方式请求的参数，此参数为一个map
     * @return 返回json字符串
     */
    public static String post(String url, Map<String, String> postParameters) {
        String returnStr = "";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost httpPost = new HttpPost(url);
            /**
             * setConnectTimeout(20000)：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout(20000) 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
             * setSocketTimeout(20000) 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectionRequestTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            /*
             * 解析传递过来的map参数，将参数解析为键值对(f=xxx)的格式放入到List
			 */
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> keySet = postParameters.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, postParameters.get(key)));
            }
            // 此处设置请求参数的编码为 utf-8
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            HttpResponse response = httpClient.execute(httpPost);
            /*
             * 判断HTTP的返回状态码，如果正确继续解析返回的数据
			 */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//HttpStatus.SC_OK=200
                returnStr = EntityUtils.toString(response.getEntity());
            } else {
                returnStr = "{\"code\":\"500\",\"msg\":\"请求地址异常("+response.getStatusLine().getStatusCode()+")\",\"content\":\"\",\"extendData\":\"\"}";
                logger.info(">>>>>>>httpClient Post 请求地址异常---》url:" + url+",data:"+JSONUtils.beanToJson(postParameters));
            }
        } catch (ClientProtocolException e) {
            logger.error(">>>>>>>httpClient Post ClientProtocolException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"601\",\"msg\":\"请求地址异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Post ClientProtocolException 异常---》url:" + url+",data:"+JSONUtils.beanToJson(postParameters));
        } catch (IOException e) {
            logger.error(">>>>>>>httpClient Post IOException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"602\",\"msg\":\"IO异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Post IOException 异常---》url:" + url+",data:"+JSONUtils.beanToJson(postParameters));
        }
        return returnStr;
    }

    /**
     * Json方式请求
     * @param url            请求的全路径 例如：http://localhost:8088/json.html
     * @param json  json
     * @return 返回json字符串
     */
    public static String postJson(String url, String json) {
        String returnStr = "";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost httpPost = new HttpPost(url);
            /**
             * setConnectTimeout(20000)：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout(20000) 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
             * setSocketTimeout(20000) 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectionRequestTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/json");


            /*
             * 解析传递过来的Json参数，将参数解析为键值对(f=xxx)的格式放入到List
             */

            StringEntity entity = new StringEntity(json,"utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");


            // 此处设置请求参数的编码为 utf-8
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            /*
             * 判断HTTP的返回状态码，如果正确继续解析返回的数据
             */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//HttpStatus.SC_OK=200
                returnStr = EntityUtils.toString(response.getEntity());
            } else {
                returnStr = "{\"code\":\"500\",\"msg\":\"请求地址异常("+response.getStatusLine().getStatusCode()+")\",\"content\":\"\",\"extendData\":\"\"}";
                logger.info(">>>>>>>httpClient Post 请求地址异常---》url:" + url+",data:"+json);
            }
        } catch (ClientProtocolException e) {
            logger.error(">>>>>>>httpClient Post ClientProtocolException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"601\",\"msg\":\"请求地址异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Post ClientProtocolException 异常---》url:" + url+",data:"+json);
        } catch (IOException e) {
            logger.error(">>>>>>>httpClient Post IOException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"602\",\"msg\":\"IO异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Post IOException 异常---》url:" + url+",data:"+json);
        }
        return returnStr;
    }

    /**
     * Json方式请求
     * @param url            请求的全路径 例如：http://localhost:8088/json.html
     * @param json  json
     * @return 返回json字符串
     */
    public static String postBytes(String url, String json) {
        String returnStr = "";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost httpPost = new HttpPost(url);
            /**
             * setConnectTimeout(20000)：设置连接超时时间，单位毫秒。
             * setConnectionRequestTimeout(20000) 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
             * setSocketTimeout(20000) 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectionRequestTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
//            httpPost.setHeader("Content-Type", "application/json");


            /*
             * 解析传递过来的Json参数，将参数解析为键值对(f=xxx)的格式放入到List
             */

            ByteArrayEntity entity = new ByteArrayEntity(json.getBytes());



            // 此处设置请求参数的编码为 utf-8
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            /*
             * 判断HTTP的返回状态码，如果正确继续解析返回的数据
             */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {//HttpStatus.SC_OK=200
                returnStr = EntityUtils.toString(response.getEntity());
            } else {
                returnStr = "{\"code\":\"500\",\"msg\":\"请求地址异常("+response.getStatusLine().getStatusCode()+")\",\"content\":\"\",\"extendData\":\"\"}";
                logger.info(">>>>>>>httpClient Post 请求地址异常---》url:" + url+",data:"+json);
            }
        } catch (ClientProtocolException e) {
            logger.error(">>>>>>>httpClient Post ClientProtocolException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"601\",\"msg\":\"请求地址异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Post ClientProtocolException 异常---》url:" + url+",data:"+json);
        } catch (IOException e) {
            logger.error(">>>>>>>httpClient Post IOException 异常---》" + e.getMessage());
            returnStr = "{\"code\":\"602\",\"msg\":\"IO异常\",\"content\":\"\",\"extendData\":\"\"}";
            logger.info(">>>>>>>httpClient Post IOException 异常---》url:" + url+",data:"+json);
        }
        return returnStr;
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
        map.put("key", xkey);
        map.put("uin", uin);
        String json = JSONUtils.beanToJson(map);
        //byte方式    这里main方法直接调下面的方法 post 各种方式等下 语音说， 好
        getByHeader(reqUrl, map);
        //form方式
//        post(reqUrl, map);
        //json
//        postJson(reqUrl, json);
        //jsonBytes
//        postBytes(reqUrl, json);
    }

    public static void send(String reqUrl,byte[] data){
        try {
//          byte[] data;
            URL url=new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
//            OutputStream os = con.getOutputStream();
//            os.write(data);
//            os.flush();
            InputStream is = con.getInputStream();
            int x = 0;
            byte[] bys = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((x = is.read(bys)) != -1) {
                bos.write(bys, 0, x);
                bos.flush();
            }
            bos.close();
//            os.close();
            is.close();
            byte[] vxRes = bos.toByteArray();
            String ret = new String(vxRes);
            System.out.println(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
