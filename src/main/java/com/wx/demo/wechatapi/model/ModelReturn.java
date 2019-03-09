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
 * 返回数据
 */
@RestController
@Api(value = "Wechat接口请求数据",tags = "Api数据")
@RequestMapping(value = "/api/aci")
@ApiModel(description = "返回数据")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-03-02T22:33:46.371Z")

public class ModelReturn   {
  @JsonProperty("result")
  private Boolean result = null;

  @JsonProperty("code")
  private Integer code = null;

  @JsonProperty("cmd")
  private Integer cmd = null;

  @JsonProperty("account")
  private String account = null;

  @JsonProperty("msg")
  private String msg = null;

  @JsonProperty("timeStamp")
  private Long timeStamp = null;

  @JsonProperty("wechatApiId")
  private String wechatApiId = null;

  @JsonProperty("retdata")
  private String retdata = null;

  public ModelReturn result(Boolean result) {
    this.result = result;
    return this;
  }

  /**
   * 执行结果
   * @return result
  **/
  @ApiModelProperty(required = true, value = "执行结果")
  @NotNull


  public Boolean isResult() {
    return result;
  }

  public void setResult(Boolean result) {
    this.result = result;
  }

  public ModelReturn code(Integer code) {
    this.code = code;
    return this;
  }

  /**
   * 状态码
   * @return code
  **/
  @ApiModelProperty(required = true, value = "状态码")
  @NotNull


  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public ModelReturn cmd(Integer cmd) {
    this.cmd = cmd;
    return this;
  }

  /**
   * 接口ID
   * @return cmd
  **/
  @ApiModelProperty(value = "接口ID")


  public Integer getCmd() {
    return cmd;
  }

  public void setCmd(Integer cmd) {
    this.cmd = cmd;
  }

  public ModelReturn account(String account) {
    this.account = account;
    return this;
  }

  /**
   * 用户名
   * @return account
  **/
  @ApiModelProperty(value = "用户名")


  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public ModelReturn msg(String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * 提示信息
   * @return msg
  **/
  @ApiModelProperty(required = true, value = "提示信息")
  @NotNull


  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public ModelReturn timeStamp(Long timeStamp) {
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

  public ModelReturn wechatApiId(String wechatApiId) {
    this.wechatApiId = wechatApiId;
    return this;
  }

  /**
   * 消息ID
   * @return wechatApiId
  **/
  @ApiModelProperty(value = "消息ID")


  public String getWechatApiId() {
    return wechatApiId;
  }

  public void setWechatApiId(String wechatApiId) {
    this.wechatApiId = wechatApiId;
  }

  public ModelReturn retdata(String retdata) {
    this.retdata = retdata;
    return this;
  }

  /**
   * 内容
   * @return retdata
  **/
  @ApiModelProperty(value = "内容")


  public String getRetdata() {
    return retdata;
  }

  public void setRetdata(String retdata) {
    this.retdata = retdata;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelReturn _return = (ModelReturn) o;
    return Objects.equals(this.result, _return.result) &&
        Objects.equals(this.code, _return.code) &&
        Objects.equals(this.cmd, _return.cmd) &&
        Objects.equals(this.account, _return.account) &&
        Objects.equals(this.msg, _return.msg) &&
        Objects.equals(this.timeStamp, _return.timeStamp) &&
        Objects.equals(this.wechatApiId, _return.wechatApiId) &&
        Objects.equals(this.retdata, _return.retdata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, code, cmd, account, msg, timeStamp, wechatApiId, retdata);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelReturn {\n");

    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    cmd: ").append(toIndentedString(cmd)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    msg: ").append(toIndentedString(msg)).append("\n");
    sb.append("    timeStamp: ").append(toIndentedString(timeStamp)).append("\n");
    sb.append("    wechatApiId: ").append(toIndentedString(wechatApiId)).append("\n");
    sb.append("    retdata: ").append(toIndentedString(retdata)).append("\n");
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

