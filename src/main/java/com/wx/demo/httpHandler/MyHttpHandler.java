package com.wx.demo.httpHandler;

import com.bootdo.common.redis.shiro.RedisManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.wx.demo.service.BaseService;
import com.wx.demo.service.ServiceManager;
import com.wx.demo.tools.ImageUtil;
import com.wx.demo.tools.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by hupt on 2017/6/29.
 */
public class MyHttpHandler implements HttpHandler {
    private static Logger logger = Logger.getLogger(MyHttpHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String uri = httpExchange.getRequestURI().getPath().split("/")[2];
        HashMap<String, String> parameters = new HashMap<String, String>();
        if (query != null && !query.equals("")) {
            String[] paramArr = query.split("&");
            for (int i = 0; i < paramArr.length; i++) {
                String[] keyValue = paramArr[i].split("=");
                if (keyValue.length == 2) {
                    parameters.put(paramArr[i].split("=")[0], paramArr[i].split("=")[1]);
                }
            }
        }
        HttpResult result = handleMsg(uri, parameters);
        String resultStr = result == null ? "" : result.toString();
        httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, resultStr.getBytes("utf-8").length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(resultStr.getBytes("utf-8"));
        os.flush();
        os.close();
        httpExchange.close();
    }

    public HttpResult handleMsg(String uri, HashMap<String, String> param) {
        try {
            String wxid = param.get("wxid");
            String randomid = null;
            if(!StringUtil.isEmpty(wxid)){
                 randomid = RedisManager.getKeyAsString(wxid);
            }
            String tagetWxId = param.get("tagetWxId");
            String content = param.get("content");
                content = decode(content);
                tagetWxId =decode(tagetWxId);

            boolean isNew = false;
            String account = param.get("account");
            String softwareId = param.get("softwareId");
            boolean autoLogin = param.containsKey("autoLogin") ? Boolean.parseBoolean(param.get("autoLogin")) : false;
            String extraData = param.containsKey("extraData") ? param.get("extraData") : "";
            if (uri.equals("getqrcode")) {
                if (randomid == null) {
                    isNew = true;
                    randomid = UUID.randomUUID().toString();
                }
                if (account == null || randomid.length() == 0) {
                    return new HttpResult(-1, "randomId、account、softwareId 不能为空");
                }
                BaseService baseService = ServiceManager.getInstance().createService(randomid, softwareId, autoLogin, extraData);
                baseService.setSoftwareId(softwareId);
                baseService.setNew(isNew);
                baseService.setCheckQrCode(true);
                baseService.checkQrCode();
                baseService.setAccount(account);
                return new HttpResult(0, randomid);
            }  else if (uri.equals("62code")) {
                if (randomid == null) {
                    randomid = UUID.randomUUID().toString();
                }
                BaseService baseService = ServiceManager.getInstance().createService(randomid, softwareId, autoLogin, extraData);
                baseService.setSoftwareId(softwareId);
                baseService.setNew(isNew);
                baseService.setAccount(account);
                baseService.setAutoLogin(autoLogin);
                baseService.UserLogin("3612010883","lxxd9116","62706C6973743030D4010203040506090A582476657273696F6E58246F626A65637473592461726368697665725424746F7012000186A0A2070855246E756C6C5F102031383138356464333266613136396230353064376363623235333537646164615F100F4E534B657965644172636869766572D10B0C54726F6F74800108111A232D32373A406375787D0000000000000101000000000000000D0000000000000000000000000000007F");
                return new HttpResult(0, baseService.getState().toString());
            }else if (uri.equals("getloginagain")) {
                BaseService baseService = ServiceManager.getInstance().createServiceForReLogin(randomid, softwareId);
                baseService.setSoftwareId(softwareId);
                baseService.setAccount(account);
                return baseService.loginAgain(param);
            } else {
                BaseService service = ServiceManager.getInstance().getServiceByRandomId(randomid);
                if (service == null) {
                    return new HttpResult(-1, "未找到相应数据");
                }
                if (uri.equals("getstate")) {
                    return service.getState();
                } else if (uri.equals("getlogin")) {
                    if (service.getState().code == 2) {
                        service.login();
                        service.getState().code = 6;
                        service.getState().msg = "登录完成.";
                    } else {
                        service.getState().code = 6;
                        service.getState().msg = "已经登录,请勿重复调用接口.";
                    }
                    return service.getState();
                } else if (uri.equals("getlogout")) {
                    logger.info("randomid:" + randomid + " ; 已退出ipad登陆");
                    service.setIsDead(true);
                    return new HttpResult(0, "操作成功");
                } else if (uri.equals("hook")) {
                    String ghid = param.get("ghid");
                    service.contactOperate(ghid,ghid,ghid,1,1);//不要写在这里,写在逻辑类里
                    return new HttpResult(0, "操作成功");
                }else if(uri.equals("sendImageMessage")){
                    String parhUrl = param.get("parhUrl");
                     decode(parhUrl);
                    byte[] bytes = ImageUtil.getImgByteFromUrl(parhUrl);
                    service.sendImage(tagetWxId, bytes);
                    return new HttpResult(0, "操作成功");
                }
                else if(uri.equals("sendTextMessage")){
                    service.sendMessage(tagetWxId, content);
                    return new HttpResult(0, "操作成功");
                } else if(uri.equals("sendCardMessage")){
                    service.sendAppMessage(tagetWxId, content);
                    return new HttpResult(0, "操作成功");
                }else if(uri.equals("inviteUserChatroom")){
                    service.inviteUserToChatRoom(decode(param.get("groupid")), decode(param.get("members")));
                    return new HttpResult(0, "操作成功");
                }
                else if(uri.equals("createChatRoom")){
                    return new HttpResult(0, service.createChatRoom(decode(param.get("members"))));
                }else if(uri.equals("sendSns")){
                    //byte[] bytes = ImageUtil.getImgByteFromUrl("https://ss1.bdstatic.com/5aAHeD3nKgcUp2HgoI7O1ygwehsv/media/ch1000/png/pc215.png");
                 //String context    = (String) WxUtil.getImgMomentXml(wxid,decode(param.get("title")),decode(param.get("pictures")).split(","));
             //String id =   service.sendSns(context);
                return new HttpResult(0, "");
                } else if(uri.equals("getSns")){
                    //byte[] bytes = ImageUtil.getImgByteFromUrl("https://ss1.bdstatic.com/5aAHeD3nKgcUp2HgoI7O1ygwehsv/media/ch1000/png/pc215.png");
                 //   service.get(context);
                    return new HttpResult(0, "操作成功");
                } else if(uri.equals("snsUpload")){
                    byte[] bytes = ImageUtil.getImgByteFromUrl(decode(param.get("imgUrl")));
                    service.snsUploadData(bytes);
                    return new HttpResult(0, "操作成功");
                } else if(uri.equals("snsComment")){
                    service.snsComment(wxid,decode(param.get("snsId")), NumberUtils.toInt(decode(param.get("type"))),decode(param.get("comment")));
                    return new HttpResult(0, "操作成功");
                } else if (uri.equals("hook")) {
                    String ghid = param.get("ghid");
                    service.contactOperate(ghid,ghid,ghid,1,1);
                    return new HttpResult(0, "操作成功");
                }else if (uri.equals("getMessageImage")) {
                    service.getImage(decode(param.get("msgId")),decode(param.get("toUsername")),decode(param.get("startPos")),decode(param.get("totalLen")),decode(param.get("dataLen")));
                    return new HttpResult(0, "操作成功");
                }else if (uri.equals("getMessagevoice")) {
                    service.getVoice(decode(param.get("startpos")),decode(param.get("datalen")),decode(param.get("datatotalength")));
                    return new HttpResult(0, "操作成功");
                }else if (uri.equals("tgfriend")) {
                    service.contactOperate(decode(param.get("encrypUserName")),decode(param.get("ticket")),"",3,1);
                    return new HttpResult(0, "操作成功");
                }else if (uri.equals("setmark")) {
                    //设置备注
                    service.setBackName(decode(param.get("tagetWxId")),decode(param.get("remark")));
                    return new HttpResult(0, "操作成功");
                }else if (uri.equals("getSelfSns")) {
                    //获取自己的朋友圈
                  String result =   service.getOwnerSnsPage();
                    return new HttpResult(0, result);
                }else if (uri.equals("getFriendSns")) {
                    //获取指定人的朋友圈
                    String result =   service.getUserPYQ(decode(param.get("tagetWxId")),decode(param.get("md5")),NumberUtils.toInt(param.get("maxId")));
                    return new HttpResult(0, result);
                }else if (uri.equals("removeChatRoomUser")) {
                    //移除群成员
                    String result =   service.removeUser(decode(param.get("groupid")),decode(param.get("tagetWxId")));
                    return new HttpResult(0, result);
                }else if (uri.equals("changegroup")) {
                    //转让群
                    String result =   service.changegroup(decode(param.get("groupid")),decode(param.get("tagetWxId")));
                    return new HttpResult(0, result);
                }else if (uri.equals("entergroup")) {
                    //同意进群
                    String result =   service.approveAddChat(decode(param.get("ticket")),decode(param.get("inviterusername")),decode(param.get("username")),decode(param.get("groupid")));
                    return new HttpResult(0, result);
                }else if (uri.equals("noticegroup")) {
                    //群公告
                    String result =   service.setChatRoomAnnouncement(decode(param.get("groupid")),decode(param.get("content")));
                    return new HttpResult(0, result);
                }else if (uri.equals("editgroupname")) {
                    //群昵称
                    String result =   service.editgroupname(decode(param.get("groupid")),decode(param.get("groupname")));
                    return new HttpResult(0, result);
                }else if (uri.equals("deletefriend")) {
                    //删除好友
                    String result =   service.delUser(decode(param.get("tagetWxId")));
                    return new HttpResult(0, result);
                }
                else {
                    return service.handleHttpRequest(uri, param);
                }
            }

        } catch (Exception e) {
            logger.info(e);
            return new HttpResult(-2, "未找到相应数据");
        }

    }

    public String decode(String decode){

        try {
            return URLDecoder.decode(decode, "utf-8");
        } catch (Exception e) {

        }
        return null;
    }
}
