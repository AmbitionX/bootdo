package com.bootdo.wx.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class SettleAccountModel implements Serializable {
    private static final long serialVersionUID = 1L;

    //用户id
    private long uid;
    //父级用户id(邀请人id)
    private Long parentid;
    //任务单价
    private String price;
    // 二级用户账户
    private Integer uaccountid;
    // 一级用户账户
    private Integer parentuaccountid;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getUaccountid() {
        return uaccountid;
    }

    public void setUaccountid(Integer uaccountid) {
        this.uaccountid = uaccountid;
    }

    public Integer getParentuaccountid() {
        return parentuaccountid;
    }

    public void setParentuaccountid(Integer parentuaccountid) {
        this.parentuaccountid = parentuaccountid;
    }
}
