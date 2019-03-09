package com.wx.demo.frameWork.protocol;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpService {

    public static String audioUrl;
    public static String baseUrl;

    private static OkHttpClient okHttpClient;


    private static OkHttpClient getHttpClient() {
        if (HttpService.okHttpClient == null) {
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            httpBuilder.connectTimeout(10, TimeUnit.SECONDS);
            HttpService.okHttpClient = httpBuilder.build();
        }

        return HttpService.okHttpClient;
    }


    public static byte[] wechatRequest(String url,byte[] data){
        OkHttpClient httpClient = HttpService.getHttpClient();

        MediaType type = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody requestBody = RequestBody.create(type,data);
        Request request = new Request.Builder().post(requestBody).url(url).build();
        try {
            Response response = httpClient.newCall(request).execute();
            return response.body().bytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void httpRequest(String path, Map<String, String> httpParam) {
        if (path.equalsIgnoreCase("/msg/device/login")) {

        }
        System.out.println(path + "   |   "+ httpParam);
    }

    public static String downLoadFile(String voiceUrl) {
        return "";
    }

    public static void wxHttpRequest(String requestUrl, Map<String, String> requestMap) {

    }

    public static String uploadFile(String s, String valueOf, File file) {
        return "";
    }
}
