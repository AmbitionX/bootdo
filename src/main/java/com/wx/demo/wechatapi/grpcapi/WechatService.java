package com.wx.demo.wechatapi.grpcapi;


import com.alibaba.fastjson.JSONObject;
import com.wx.demo.wechatapi.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
@Api(tags = "[单项式] Api 接口",position = 299)
@RequestMapping(value = "/api")
@RestController
public interface WechatService {

    /**
     * 断线重连
     */
    @ApiVersion("1")
    @ApiOperation(value = "autoLogin",tags = {"[分布式] 接口"})
    @RequestMapping(value = "/autoLogin", method = RequestMethod.POST)
    void getModel( @RequestBody UtilBase UtilBaseinit, @RequestBody UtilUser UtilUserinit, @RequestBody WechatMsg WechatMsginit, @RequestBody WechatApi WechatApiinit);
        //这里只是为了让Swagger model里加载到内容
        //使用swagger的 @ApiModel注解的时候有个坑 就是必须在controller 使用 @RequestBody 注解 否则无法显示models
        //而且不报错，此时swagger就和 spring 耦合了，而且问题难以排查




    void autoLogin();

    /**
     * 获取登录二维码
     */
    void getLoginQrcode();

    /**
     * 检测二维码
     */
    void checkQrcode();

    /**
     * 二维码登录
     * @param userName
     * @param pass
     */
    void qrcodeLogin(String userName, String pass, String headUrl);

    /**
     * 62登录
     * @param user
     * @param pass
     * @param wxdata
     */
    void wxdataLogin(String user, String pass, String wxdata);

    /**
     * 同步通讯录
     */
    void initContact();

    /**
     * 下线
     */
    void logout();

    /**
     * 删除设备
     */
    void deleteDevice();

    /**
     * 上线
     */
    void login(String account, String password, String info);

    /**
     * 获取登录状态
     * @return
     */

    int getLoginStatus();

    /**
     * 修改密码
     * @param oldPass
     * @param newPass
     */
    void resetPassword(String oldPass, String newPass);

    /**
     * 上传通讯录
     * @param mobile
     * @param contactList
     */
    void uploadMobileContact(String mobile, String contactList);

    /**
     * 下载图片消息
     * @param jsonObject
     * @return
     */
    String downloadImg(JSONObject jsonObject);

    /**
     * 下载语音消息
     * @param jsonObject
     * @return
     */
    String downloadVoice(JSONObject jsonObject);

    /**
     * 添加好友
     * @param username 微信号或手机号
     * @param type 0 通过微信号搜索添加好友 1 通过搜索手机号添加好友
     * @param helloContent 打招呼内容
     */
    Map<String, String>  addUser(String username, int type, String helloContent, int at);

    Map<String, String> contactOperate(String encrypUserName, String ticket, String content, int type, int Scene);


    /**
     * 发送普通消息
     * @param content 消息内容
     * @param toUsername 消息发送对象
     */
    void sendMicroMsg(String toUsername, String content);

    /**
     * 发送app消息
     * @param toUsername
     * @param title
     * @param content
     * @param pointUrl
     * @param thumburl
     */
    void sendAppMsg(String toUsername, String title, String content, String pointUrl, String thumburl);

    /**
     * 发送语音消息
     * @param toUsername
     * @param voiceUrl
     */
    void sendVoiceMsg(String toUsername, String voiceUrl, int length);

    /**
     * 发送图片消息
     * @param toUsername
     * @param picUrl
     */
    void sendImageMsg(String toUsername, String picUrl);

    /**
     * 建群
     * @param chatroomName 群昵称
     * @param membernames
     */
    Map createChatRoom(String chatroomName, List<String> membernames);

    /**
     * 修改群名称
     * @param chatRoom
     * @param roomName
     */
    int modChatroomname(String chatRoom, String roomName);

    /**
     * 修改好友备注
     * @param username
     * @param remark
     * @return
     */
    int modUserRemark(String username, String remark);

    /**
     * 删除好友
     * @param username
     * @return
     */
    int delUser(String username);


    /**
     * 修改账号信息
     * @param nickname
     * @param signature
     * @param country
     * @param province
     * @param city
     * @param sex
     */
    void modUserInfo(String nickname, String signature, String country, String province, String city, int sex);


    /**
     * 上传图片
     * @param data
     * @return
     */
    String snsUpload(byte[] data);

    /**
     * 发送朋友圈
     * @param content
     */
    void snsPost(String content);

    /**
     * 查询联系人
     * @param usernameList
     * @param chatroomid 可以为空 不为空则查询群成员详细资料
     * @return
     */
    List<Map> getContact(List<String> usernameList, String chatroomid);

    /**
     * 上传头像
     * @param data
     * @return
     */
    int uploadHeadImg(byte[] data);

    /**
     * 附近人
     * @param longitude
     * @param latitude
     */
    void lbsFind(String longitude, String latitude);


    /**
     * 获取群成员详细细资料
     * @param chatroom 群ID
     */
    Map getChatroomMemberDetail(String chatroom);


    /**
     * 获取群二维码
     * @param chatroom
     * @return
     */
    Map getRoomQrcode(String chatroom);


    /**
     * 删除群成员
     * @param chatroom
     * @param username
     */
    void delChatRoomUser(String chatroom, String username);

    /**
     * 获取朋友圈主页内容
     * @param clientLatestId
     */
    void snsTimeLine(BigInteger clientLatestId);

    /**
     * 获取指定人的朋友圈
     * @param firstPageMd5 首页为空 第二页请附带md5
     * @param username 访问好友朋友圈的wxid
     * @param maxId 首页为0 次页朋友圈数据id 的最小值
     */
    String snsUserPage(String firstPageMd5, String username, BigInteger maxId);

    /**
     * 操作朋友圈
     * @param ids 逗号分隔 要操作的朋友圈内容
     * @param commentId 评论ID
     * @param type 1删除朋友圈2设为隐私3设为公开4删除评论5取消点赞
     */
    void snsObjectOp(String ids, int commentId, int type);

    /**
     * 朋友圈点赞评论
     * @param id
     * @param toUsername
     * @param type 1 点赞 2 评论
     * @param content
     */
    void snsComment(String id, String toUsername, int type, String content);

    /**
     * 识别二维码可实现扫码入群
     * 自动加群
     * 公众号阅读Key的获取
     * @param reqUrl 要获取key的连接 授权登陆时的链接即为转跳链接https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx53c677b55caa45fd&redirect_uri=http%3A%2F%2Fmeidang.cimiworld.com%2Fh5%2Fchourenpin%3Fs%3D77e881961fee12eb65f5497bbff02fac%26from%3Dsinglemessage%26isappinstalled%3D0&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
     * @param scene 2 来源好友或群 必须设置来源的id 3 历史阅读 4 二维码连接 7 来源公众号 必须设置公众号的id
     * @param username 来源 来源设置wxid 来源群id@chatroom 来源公众号gh_e09c57858a0c原始id
     */
    String getA8Key(String reqUrl, int scene, String username);

    /**
     * 阅读使用的a8k 短链接方式获取
     * @param reqUrl 要获取key的连接 授权登陆时的链接即为转跳链接https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx53c677b55caa45fd&redirect_uri=http%3A%2F%2Fmeidang.cimiworld.com%2Fh5%2Fchourenpin%3Fs%3D77e881961fee12eb65f5497bbff02fac%26from%3Dsinglemessage%26isappinstalled%3D0&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
     * @param scene 2 来源好友或群 必须设置来源的id 3 历史阅读 4 二维码连接 7 来源公众号 必须设置公众号的id
     * @param username 来源 来源设置wxid 来源群id@chatroom 来源公众号gh_e09c57858a0c原始id
     */
    String getReadA8KeyAndRead(String reqUrl, int scene, String username);

    /**
     * 扫码进群
     * @param codeUrl
     */
    void joinChatRoomFormCode(String codeUrl, String taskId);

    /**
     * 添加群成员
     * @param chatroom
     * @param username
     */
    void addChatRoomMember(String chatroom, String username);

    /**
     * 设置群公告
     * @param chatroom
     * @param announcement
     */
    void setChatRoomAnnouncement(String chatroom, String announcement);

    /**
     * 摇一摇
     * @param longitude
     * @param latitude
     */
    void shakeGet(String longitude, String latitude);

    /**
     * 群发消息（通过群发助手）
     * @param wxIds
     * @param data
     * @param msgType
     */
    void massMessage(String wxIds, byte[] data, int msgType);

    /**
     * 群发消息（入口）
     * @param data
     */
    void massMessage(String data);

    /**
     * 自动通过好友请求
     * @param status  0 关闭自动通过好友请求 1 开启自动通过好友请求
     */
    void autoAcceptUser(int status);

}
