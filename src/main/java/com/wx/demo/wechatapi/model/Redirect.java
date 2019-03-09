package com.wx.demo.wechatapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 重定向数据
 */
@RestController
@Api(value = "Wechat接口请求数据",tags = "Api数据")
@RequestMapping(value = "/api/aci")
@ApiModel(description = "重定向数据")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-02T22:33:46.371Z")

public class Redirect   {
  @JsonProperty("result")
  private Boolean result = null;

  @JsonProperty("code")
  private Integer code = null;

  @JsonProperty("cmd")
  private Integer cmd = null;

  @JsonProperty("timeStamp")
  private Long timeStamp = null;

  @JsonProperty("msg")
  private String msg = null;

  @JsonProperty("account")
  private String account = null;

  @JsonProperty("serverIp")
  private String serverIp = null;

  @JsonProperty("serverId")
  private String serverId = null;

  @JsonProperty("serverPort")
  private Integer serverPort = null;

  public Redirect result(Boolean result) {
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

  public Redirect code(Integer code) {
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

  public Redirect cmd(Integer cmd) {
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

  public Redirect timeStamp(Long timeStamp) {
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

  public Redirect msg(String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * 请根据返回ip加端口号,重新连接!
   * @return msg
  **/
  @ApiModelProperty(value = "请根据返回ip加端口号,重新连接!")


  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Redirect account(String account) {
    this.account = account;
    return this;
  }

  /**
   * 用户名
   * @return account
  **/
  @ApiModelProperty(required = true, value = "用户名")
  @NotNull


  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public Redirect serverIp(String serverIp) {
    this.serverIp = serverIp;
    return this;
  }

  /**
   * 服务器IP
   * @return serverIp
  **/
  @ApiModelProperty(required = true, value = "服务器IP")
  @NotNull


  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public Redirect serverId(String serverId) {
    this.serverId = serverId;
    return this;
  }

  /**
   * 服务器ID
   * @return serverId
  **/
  @ApiModelProperty(required = true, value = "服务器ID")
  @NotNull


  public String getServerId() {
    return serverId;
  }

  public void setServerId(String serverId) {
    this.serverId = serverId;
  }

  public Redirect serverPort(Integer serverPort) {
    this.serverPort = serverPort;
    return this;
  }

  /**
   * 服务器端口号
   * @return serverPort
  **/
  @ApiModelProperty(required = true, value = "服务器端口号")
  @NotNull


  public Integer getServerPort() {
    return serverPort;
  }

  public void setServerPort(Integer serverPort) {
    this.serverPort = serverPort;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Redirect redirect = (Redirect) o;
    return Objects.equals(this.result, redirect.result) &&
        Objects.equals(this.code, redirect.code) &&
        Objects.equals(this.cmd, redirect.cmd) &&
        Objects.equals(this.timeStamp, redirect.timeStamp) &&
        Objects.equals(this.msg, redirect.msg) &&
        Objects.equals(this.account, redirect.account) &&
        Objects.equals(this.serverIp, redirect.serverIp) &&
        Objects.equals(this.serverId, redirect.serverId) &&
        Objects.equals(this.serverPort, redirect.serverPort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, code, cmd, timeStamp, msg, account, serverIp, serverId, serverPort);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Redirect {\n");

    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    cmd: ").append(toIndentedString(cmd)).append("\n");
    sb.append("    timeStamp: ").append(toIndentedString(timeStamp)).append("\n");
    sb.append("    msg: ").append(toIndentedString(msg)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    serverIp: ").append(toIndentedString(serverIp)).append("\n");
    sb.append("    serverId: ").append(toIndentedString(serverId)).append("\n");
    sb.append("    serverPort: ").append(toIndentedString(serverPort)).append("\n");
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
}

