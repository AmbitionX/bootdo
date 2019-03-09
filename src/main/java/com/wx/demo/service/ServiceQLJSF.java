package com.wx.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wx.demo.DB.DBUtil;
import com.wx.demo.bean.Message;
import com.wx.demo.tools.ImageUtil;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 清理僵尸粉
 */
public class ServiceQLJSF extends BaseService {
    private static final String exceptId = "filehelper,newsapp,fmessage,weibo,qqmail,tmessage,qmessage,qqsync,weixin,floatbottle";

    private ArrayList<String> checkedWxId = new ArrayList<>();
    private static ScheduledExecutorService exitService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
    //org.apache.commons.lang3.concurrent.BasicThreadFactory

    public ServiceQLJSF(String randomids) {
        super(randomids);
    }

    @Override
    protected void begin() {
        super.begin();
        executorService.submit(() -> {
            QLJSF();
        });
    }

    private void QLJSF() {
        int taskId = Integer.parseInt(extraData);
        String sql = "select * from ims_hg_fission_message WHERE id =" + taskId;
        JSONArray tasks = DBUtil.executeQuery(sql);
        if (tasks.size() == 0) {
            sendMessage(wxId, "获取二维码时传的参数不对");
            return;
        }
        JSONObject task = tasks.getJSONObject(0);
        int toType = task.getInteger("to_type");
        //给所有的好友发送文字+图片，去探测是否是僵死粉
        if (toType == 4) {
            String text = task.getString("text");
            String photo = task.getString("photo");
            sendTextPhoneToFriend(taskId, text, photo);
        } else if (toType == 5) {

        }
        exitService.schedule(() -> {
            hasDead = true;
        }, 30, TimeUnit.SECONDS);
    }

    private void sendTextPhoneToFriend(int taskId, String text, String photoUrl) {
        int continueFlag = 1;
        int wxContactSeq = 0;
        int chatRoomContactSeq = 0;
        int totalSendNum = 0;
        String logQuery = String.format("SELECT * from send_cmd_log WHERE account = '%s' and wxid='%s' and `status` = 0 ORDER BY create_time DESC limit 1", account, wxId);
        JSONArray jsonArray = DBUtil.executeQuery(logQuery);
        if (jsonArray.size() == 0) {
            String insertLog = String.format("insert into send_cmd_log (taskId,wxid,account,create_time) VALUES (%d,'%s','%s',%d)", taskId, wxId, account, System.currentTimeMillis() / 1000);
            DBUtil.executeUpdate(insertLog);
        } else {
            wxContactSeq = jsonArray.getJSONObject(0).getInteger("contact_flag");
            totalSendNum = jsonArray.getJSONObject(0).getInteger("total_send_num");
        }

        while (continueFlag != 0) {
            String contact = longUtil.getAllContact(wxContactSeq, chatRoomContactSeq);
            JSONObject contactObj = JSONObject.parseObject(contact);
            continueFlag = contactObj.getInteger("ContinueFlag");
            wxContactSeq = contactObj.getInteger("CurrentWxcontactSeq");
            chatRoomContactSeq = contactObj.getInteger("CurrentChatRoomContactSeq");
            JSONArray userNames = contactObj.getJSONArray("UsernameLists");
            for (int i = 0; i < userNames.size(); i++) {
                String userName = userNames.getJSONObject(i).getString("Username");
                if (!exceptId.contains(userName) && !userName.startsWith("gh_") && !userName.contains("@")) {
                    sendMessage(userName, text);
                    byte[] bytes = ImageUtil.getImgByteFromUrl(photoUrl);
                    longUtil.sendImage(userName, bytes);
                    totalSendNum++;
                }
            }
            String updateSql = String.format("UPDATE send_cmd_log SET total_send_num = %d, contact_flag =%d WHERE account = '%s' and wxid='%s' and `status` = 0 ORDER BY create_time DESC limit 1", totalSendNum, wxContactSeq, account, wxId);
            sendMessage(wxId, "已检测" + totalSendNum + "人");
            DBUtil.executeUpdate(updateSql);
        }
        DBUtil.executeUpdate("update send_cmd_log SET status = 1");
    }

    @Override
    public void parseMsg(Message msg) {
        int type = 0;//判定是否正常，0正常，1被拉黑，2被删除
        if (msg.MsgType == 10000 && msg.Content.contains("消息已发出，但被对方拒收")) {
            type = 1;
        } else if (msg.MsgType == 10000 && msg.Content.contains("请先发送朋友验证请求，对方验证通过后，才能聊天")) {
            type = 2;
        }
        if (type != 0 && !checkedWxId.contains(msg.FromUserName)) {
            checkedWxId.add(msg.FromUserName);
            longUtil.setBackName(msg.FromUserName, "AAAA-" + (type == 1 ? "被拉黑" : "被删除"));
            int finalType = type;
            longUtil.getUserOrGroupInfo(data -> {
                JSONArray jsonArray = JSONArray.parseArray(new String(data));
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String nickName = jsonObject.getString("NickName");
                String alias = jsonObject.getString("Alias");
                String province = jsonObject.getString("Province");
                String city = jsonObject.getString("City");
                String sign = jsonObject.getString("Signature");
                String sex = jsonObject.getString("Sex");
                String tmp = "<msg username=\"_wxid\" nickname=\"_nickName\" fullpy=\"pinyin\" shortpy=\"\" alias=\"_alias\" imagestatus=\"3\" scene=\"17\" province=\"_province\" city=\"_city\" sign=\"_sign\" sex=\"_sex\" certflag=\"0\" certinfo=\"\" brandIconUrl=\"\" brandHomeUrl=\"\" brandSubscriptConfigUrl=\"\" brandFlags=\"0\" regionCode=\"CN_Henan_Zhengzhou\"></msg>";
                tmp = tmp.replace("_wxid", msg.FromUserName).replace("_nickName", "对方已把你" + (finalType == 1 ? "拉黑" : "删除")).replace("_alias", alias).
                        replace("_province", province).replace("_city", city).replace("_sign", sign).replace("_sex", sex);
                longUtil.sendCardMsg(wxId, tmp);
            }, msg.FromUserName);
        }
    }
}
