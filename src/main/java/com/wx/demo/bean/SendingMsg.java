package com.wx.demo.bean;

public class SendingMsg {
    private String toUserId;
    // 1: 文字消息
    // 2: 分享链接
    // 3: 图片
    private String textMsg;
    private byte[] imgMsg;
    private String linkMsg;
    private String recordType = "";
    public String getRobotWxId() {
        return robotWxId;
    }
    public void setRobotWxId(String robotWxId) {
        this.robotWxId = robotWxId;
    }
    private String robotWxId;
    private String keyWords;
    public String getRecordType() {
        return recordType;
    }
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    public SendingMsg(String toUserId) {
        this.toUserId = toUserId;
    }
    public SendingMsg(String toUserId, String keyWords) {
        this.toUserId = toUserId;
        this.keyWords = keyWords == null ? "" : keyWords;
    }
    public String getToUserId() {
        return toUserId;
    }
    public String getLinkMsg() {
        return linkMsg;
    }
    public SendingMsg setLinkMsg(String linkMsg) {
        this.linkMsg = linkMsg;
        return this;
    }
    public String getTextMsg() {
        return textMsg;
    }
    public SendingMsg setTextMsg(String textMsg) {
        this.textMsg = textMsg;
        return this;
    }
    public String getKeyWords() {
        return keyWords;
    }
    public byte[] getImgMsg() {
        return imgMsg;
    }
    public SendingMsg setImgMsg(byte[] imgMsg) {
        this.imgMsg = imgMsg;
        return this;
    }

}


