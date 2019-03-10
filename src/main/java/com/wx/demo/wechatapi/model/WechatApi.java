/*
 * 微信协议 API
 * 说明 [http://swagger.io](http://swagger.io)  swagger [22222](http://swagger.io/irc/). key `123` to test .
 *
 * OpenAPI spec version: 1.0.0
 * Contact: admin@wxipad.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.wx.demo.wechatapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wx.demo.bean.RedisBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Objects;

/***
 * 1.8.9fixed bug修改
 * @since:swagger-bootstrap-ui 1.0
 * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2019-1-7 16:18:56
 */
@Api(tags = "Wechat Api",position = 21)
@RestController
@RequestMapping(value = "/wxapi")
@ApiModel(description = "微信数据参数")
public class WechatApi implements Serializable{
  Logger logger= LoggerFactory.getLogger(WechatApi.class);
  @JsonProperty("result")
  private Boolean result = null;

  @JsonProperty("cmd")
  private Integer cmd = null;

  @JsonProperty("cmdname")
  private String cmdname = null;

  @JsonProperty("Msg")
  private String msg = null;

  @JsonProperty("code")
  private Integer code = null;

  @JsonProperty("protocolVer")
  private Integer protocolVer = null;

  @JsonProperty("timeStamp")
  private Long timeStamp = null;

  @JsonProperty("wechatApiId")
  private String wechatApiId = null;

  @JsonProperty("account")
  private String account = null;

  @JsonProperty("accountcode")
  private String accountcode = null;

  @JsonProperty("accountAppId")
  private String accountAppId = null;

  @JsonProperty("accountAppKey")
  private String accountAppKey = null;

  @JsonProperty("accountTocken")
  private String accountTocken = null;

  @JsonProperty("userName")
  private String userName = null;

  @JsonProperty("userPassWord")
  private String userPassWord = null;

  @JsonProperty("wxDat")
  private String wxDat = null;

  @JsonProperty("serverIp")
  private String serverIp = null;

  @JsonProperty("serverPort")
  private Integer serverPort = null;

  @JsonProperty("serverId")
  private String serverId = null;

  @JsonProperty("randomId")
  private String randomId = null;

  @JsonProperty("softwareId")
  private String softwareId = null;

  @JsonProperty("autoLogin")
  private Boolean autoLogin = null;

  @JsonProperty("reqUrl")
  private String reqUrl = null;

  @JsonProperty("scene")
  private String scene = null;

  @JsonProperty("username")
  private String username = null;

  @JsonProperty("grpcWechatMsg")
  private byte[] grpcWechatMsg = null;

  @JsonProperty("grpcBaseMsg")
  private byte[] grpcBaseMsg = null;

  @JsonProperty("grpcUser")
  private byte[] grpcUser = null;

  @JsonProperty("grpcPayLoads")
  private byte[] grpcPayLoads = null;

  public String getReqUrl() {
    return reqUrl;
  }

  public void setReqUrl(String reqUrl) {
    this.reqUrl = reqUrl;
  }

  public String getScene() {
    return scene;
  }

  public void setScene(String scene) {
    this.scene = scene;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public WechatApi result(Boolean result) {
    this.result = result;
    return this;
  }

  /**
   * 执行结果
   * @return result
   **/
  @ApiModelProperty(value = "执行结果")
  public Boolean isResult() {
    return result;
  }

  public void setResult(Boolean result) {
    this.result = result;
  }

  public WechatApi cmd(Integer cmd) {
    this.cmd = cmd;
    return this;
  }

  /**
   * 接口ID
   * @return cmd
   **/
  @ApiModelProperty(required = true, value = "接口ID")
  @NotNull
  public Integer getCmd() {
    return cmd;
  }

  public void setCmd(Integer cmd) {
    this.cmd = cmd;
  }

  public WechatApi cmdname(String cmdname) {
    this.cmdname = cmdname;
    return this;
  }

  /**
   * 接口名
   * @return cmdname
   **/
  @ApiModelProperty(value = "接口名")
  public String getCmdname() {
    return cmdname;
  }

  public void setCmdname(String cmdname) {
    this.cmdname = cmdname;
  }

  public WechatApi msg(String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * 提示信息
   * @return msg
   **/
  @ApiModelProperty(value = "提示信息")
  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public WechatApi code(Integer code) {
    this.code = code;
    return this;
  }

  /**
   * 状态码
   * @return code
   **/
  @ApiModelProperty(value = "状态码")
  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public WechatApi protocolVer(Integer protocolVer) {
    this.protocolVer = protocolVer;
    return this;
  }

  /**
   * 协议类型
   * @return protocolVer
   **/
  @ApiModelProperty(value = "协议类型")
  public Integer getProtocolVer() {
    return protocolVer;
  }

  public void setProtocolVer(Integer protocolVer) {
    this.protocolVer = protocolVer;
  }

  public WechatApi timeStamp(Long timeStamp) {
    this.timeStamp = timeStamp;
    return this;
  }

  /**
   * 时间戳
   * @return timeStamp
   **/
  @ApiModelProperty(value = "时间戳")
  public Long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public WechatApi wechatApiId(String wechatApiId) {
    this.wechatApiId = wechatApiId;
    return this;
  }

  /**
   * 唯一消息ID,用于异步回调,一般为时间戳的md5
   * @return wechatApiId
   **/
  @ApiModelProperty(value = "唯一消息ID,用于异步回调,一般为时间戳的md5")
  public String getWechatApiId() {
    return wechatApiId;
  }

  public void setWechatApiId(String wechatApiId) {
    this.wechatApiId = wechatApiId;
  }

  public WechatApi account(String account) {
    this.account = account;
    return this;
  }

  /**
   * 发起请求账号或微信号拥有者
   * @return account
   **/
  @ApiModelProperty(required = true, value = "发起请求账号或微信号拥有者")
  @NotNull
  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public WechatApi accountcode(String accountcode) {
    this.accountcode = accountcode;
    return this;
  }

  /**
   * 发起请求账号或微信号拥有者的权限密码或者激活码
   * @return accountcode
   **/
  @ApiModelProperty(value = "发起请求账号或微信号拥有者的权限密码或者激活码")
  public String getAccountcode() {
    return accountcode;
  }

  public void setAccountcode(String accountcode) {
    this.accountcode = accountcode;
  }

  public WechatApi accountAppId(String accountAppId) {
    this.accountAppId = accountAppId;
    return this;
  }

  /**
   * 开发者的AppId
   * @return accountAppId
   **/
  @ApiModelProperty(value = "开发者的AppId")
  public String getAccountAppId() {
    return accountAppId;
  }

  public void setAccountAppId(String accountAppId) {
    this.accountAppId = accountAppId;
  }

  public WechatApi accountAppKey(String accountAppKey) {
    this.accountAppKey = accountAppKey;
    return this;
  }

  /**
   * 开发者的AppKey
   * @return accountAppKey
   **/
  @ApiModelProperty(value = "开发者的AppKey")
  public String getAccountAppKey() {
    return accountAppKey;
  }

  public void setAccountAppKey(String accountAppKey) {
    this.accountAppKey = accountAppKey;
  }

  public WechatApi accountTocken(String accountTocken) {
    this.accountTocken = accountTocken;
    return this;
  }

  /**
   * 开发者的AppTocken
   * @return accountTocken
   **/
  @ApiModelProperty(value = "开发者的AppTocken")
  public String getAccountTocken() {
    return accountTocken;
  }

  public void setAccountTocken(String accountTocken) {
    this.accountTocken = accountTocken;
  }

  public WechatApi userName(String userName) {
    this.userName = userName;
    return this;
  }

  /**
   * 微信号,手机号,QQ号,邮箱,wxid
   * @return userName
   **/
  @ApiModelProperty(value = "微信号,手机号,QQ号,邮箱,wxid")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public WechatApi userPassWord(String userPassWord) {
    this.userPassWord = userPassWord;
    return this;
  }

  /**
   * 微信号密码
   * @return userPassWord
   **/
  @ApiModelProperty(value = "微信号密码")
  public String getUserPassWord() {
    return userPassWord;
  }

  public void setUserPassWord(String userPassWord) {
    this.userPassWord = userPassWord;
  }

  public WechatApi wxDat(String wxDat) {
    this.wxDat = wxDat;
    return this;
  }

  /**
   * 微信号数据
   * @return wxDat
   **/
  @ApiModelProperty(value = "微信号数据")
  public String getWxDat() {
    return wxDat;
  }

  public void setWxDat(String wxDat) {
    this.wxDat = wxDat;
  }

  public WechatApi serverIp(String serverIp) {
    this.serverIp = serverIp;
    return this;
  }

  /**
   * 服务器地址
   * @return serverIp
   **/
  @ApiModelProperty(value = "服务器地址")
  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public WechatApi serverPort(Integer serverPort) {
    this.serverPort = serverPort;
    return this;
  }

  /**
   * 服务器端口号
   * @return serverPort
   **/
  @ApiModelProperty(value = "服务器端口号")
  public Integer getServerPort() {
    return serverPort;
  }

  public void setServerPort(Integer serverPort) {
    this.serverPort = serverPort;
  }

  public WechatApi serverId(String serverId) {
    this.serverId = serverId;
    return this;
  }

  /**
   * 服务器ID[127.0.0.1:6666]的Md5
   * @return serverId
   **/
  @ApiModelProperty(required = true, value = "服务器ID[127.0.0.1:6666]的Md5")
  @NotNull
  public String getServerId() {
    return serverId;
  }

  public void setServerId(String serverId) {
    this.serverId = serverId;
  }

  public WechatApi randomId(String randomId) {
    this.randomId = randomId;
    return this;
  }

  /**
   * 操作识别码或微信号uuid
   * @return randomId
   **/
  @ApiModelProperty(value = "操作识别码或微信号uuid")
  public String getRandomId() {
    return randomId;
  }

  public void setRandomId(String randomId) {
    this.randomId = randomId;
  }

  public WechatApi softwareId(String softwareId) {
    this.softwareId = softwareId;
    return this;
  }

  /**
   * 逻辑ID
   * @return softwareId
   **/
  @ApiModelProperty(value = "逻辑ID")
  public String getSoftwareId() {
    return softwareId;
  }

  public void setSoftwareId(String softwareId) {
    this.softwareId = softwareId;
  }

  public WechatApi autoLogin(Boolean autoLogin) {
    this.autoLogin = autoLogin;
    return this;
  }

  /**
   * 是否自动执行
   * @return autoLogin
   **/
  @ApiModelProperty(value = "是否自动执行")
  public Boolean isAutoLogin() {
    return autoLogin;
  }

  public void setAutoLogin(Boolean autoLogin) {
    this.autoLogin = autoLogin;
  }

  public WechatApi grpcWechatMsg(byte[] grpcWechatMsg) {
    this.grpcWechatMsg = grpcWechatMsg;
    return this;
  }

  /**
   * [Grpc]请求[WechatMsg]数据的byte[]
   * @return grpcWechatMsg
   **/
  @ApiModelProperty(value = "[Grpc]请求[WechatMsg]数据的byte[]")
  public byte[] getGrpcWechatMsg() {
    return grpcWechatMsg;
  }

  public void setGrpcWechatMsg(byte[] grpcWechatMsg) {
    this.grpcWechatMsg = grpcWechatMsg;
  }

  public WechatApi grpcBaseMsg(byte[] grpcBaseMsg) {
    this.grpcBaseMsg = grpcBaseMsg;
    return this;
  }

  /**
   * [Grpc]接口请求[Basemsg]数据的byte[]
   * @return grpcBaseMsg
   **/
  @ApiModelProperty(value = "[Grpc]接口请求[Basemsg]数据的byte[]")
  public byte[] getGrpcBaseMsg() {
    return grpcBaseMsg;
  }

  public void setGrpcBaseMsg(byte[] grpcBaseMsg) {
    this.grpcBaseMsg = grpcBaseMsg;
  }

  public WechatApi grpcUser(byte[] grpcUser) {
    this.grpcUser = grpcUser;
    return this;
  }

  /**
   * [Grpc]接口请求[User]数据的byte[]
   * @return grpcUser
   **/
  @ApiModelProperty(value = "[Grpc]接口请求[User]数据的byte[]")
  public byte[] getGrpcUser() {
    return grpcUser;
  }

  public void setGrpcUser(byte[] grpcUser) {
    this.grpcUser = grpcUser;
  }

  public WechatApi grpcPayLoads(byte[] grpcPayLoads) {
    this.grpcPayLoads = grpcPayLoads;
    return this;
  }

  /**
   * [Grpc]接口请求[PayLoads]的byte[]或腾讯返回数据的byte[]
   * @return grpcPayLoads
   **/
  @ApiModelProperty(value = "[Grpc]接口请求[PayLoads]的byte[]或腾讯返回数据的byte[]")
  public byte[] getGrpcPayLoads() {
    return grpcPayLoads;
  }

  public void setGrpcPayLoads(byte[] grpcPayLoads) {
    this.grpcPayLoads = grpcPayLoads;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WechatApi wechatApi = (WechatApi) o;
    return Objects.equals(this.result, wechatApi.result) &&
            Objects.equals(this.cmd, wechatApi.cmd) &&
            Objects.equals(this.cmdname, wechatApi.cmdname) &&
            Objects.equals(this.msg, wechatApi.msg) &&
            Objects.equals(this.code, wechatApi.code) &&
            Objects.equals(this.protocolVer, wechatApi.protocolVer) &&
            Objects.equals(this.timeStamp, wechatApi.timeStamp) &&
            Objects.equals(this.wechatApiId, wechatApi.wechatApiId) &&
            Objects.equals(this.account, wechatApi.account) &&
            Objects.equals(this.accountcode, wechatApi.accountcode) &&
            Objects.equals(this.accountAppId, wechatApi.accountAppId) &&
            Objects.equals(this.accountAppKey, wechatApi.accountAppKey) &&
            Objects.equals(this.accountTocken, wechatApi.accountTocken) &&
            Objects.equals(this.userName, wechatApi.userName) &&
            Objects.equals(this.userPassWord, wechatApi.userPassWord) &&
            Objects.equals(this.wxDat, wechatApi.wxDat) &&
            Objects.equals(this.serverIp, wechatApi.serverIp) &&
            Objects.equals(this.serverPort, wechatApi.serverPort) &&
            Objects.equals(this.serverId, wechatApi.serverId) &&
            Objects.equals(this.randomId, wechatApi.randomId) &&
            Objects.equals(this.softwareId, wechatApi.softwareId) &&
            Objects.equals(this.autoLogin, wechatApi.autoLogin) &&
            Objects.equals(this.grpcWechatMsg, wechatApi.grpcWechatMsg) &&
            Objects.equals(this.grpcBaseMsg, wechatApi.grpcBaseMsg) &&
            Objects.equals(this.grpcUser, wechatApi.grpcUser) &&
            Objects.equals(this.grpcPayLoads, wechatApi.grpcPayLoads);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, cmd, cmdname, msg, code, protocolVer, timeStamp, wechatApiId, account, accountcode, accountAppId, accountAppKey, accountTocken, userName, userPassWord, wxDat, serverIp, serverPort, serverId, randomId, softwareId, autoLogin, grpcWechatMsg, grpcBaseMsg, grpcUser, grpcPayLoads);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WechatApi {\n");

    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    cmd: ").append(toIndentedString(cmd)).append("\n");
    sb.append("    cmdname: ").append(toIndentedString(cmdname)).append("\n");
    sb.append("    msg: ").append(toIndentedString(msg)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    protocolVer: ").append(toIndentedString(protocolVer)).append("\n");
    sb.append("    timeStamp: ").append(toIndentedString(timeStamp)).append("\n");
    sb.append("    wechatApiId: ").append(toIndentedString(wechatApiId)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    accountcode: ").append(toIndentedString(accountcode)).append("\n");
    sb.append("    accountAppId: ").append(toIndentedString(accountAppId)).append("\n");
    sb.append("    accountAppKey: ").append(toIndentedString(accountAppKey)).append("\n");
    sb.append("    accountTocken: ").append(toIndentedString(accountTocken)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    userPassWord: ").append(toIndentedString(userPassWord)).append("\n");
    sb.append("    wxDat: ").append(toIndentedString(wxDat)).append("\n");
    sb.append("    serverIp: ").append(toIndentedString(serverIp)).append("\n");
    sb.append("    serverPort: ").append(toIndentedString(serverPort)).append("\n");
    sb.append("    serverId: ").append(toIndentedString(serverId)).append("\n");
    sb.append("    randomId: ").append(toIndentedString(randomId)).append("\n");
    sb.append("    softwareId: ").append(toIndentedString(softwareId)).append("\n");
    sb.append("    autoLogin: ").append(toIndentedString(autoLogin)).append("\n");
    sb.append("    grpcWechatMsg: ").append(toIndentedString(grpcWechatMsg)).append("\n");
    sb.append("    grpcBaseMsg: ").append(toIndentedString(grpcBaseMsg)).append("\n");
    sb.append("    grpcUser: ").append(toIndentedString(grpcUser)).append("\n");
    sb.append("    grpcPayLoads: ").append(toIndentedString(grpcPayLoads)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  public static WechatApi unserizlize(byte[] byt) {
    ObjectInputStream oii = null;
    ByteArrayInputStream bis = null;
    bis = new ByteArrayInputStream(byt);
    try {
      oii = new ObjectInputStream(bis);
      Object obj = oii.readObject();
      return (WechatApi) obj;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] serialise(Object object) {
    ObjectOutputStream obi = null;
    ByteArrayOutputStream bai = null;
    try {
      bai = new ByteArrayOutputStream();
      obi = new ObjectOutputStream(bai);
      obi.writeObject(object);
      byte[] byt = bai.toByteArray();
      return byt;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}

