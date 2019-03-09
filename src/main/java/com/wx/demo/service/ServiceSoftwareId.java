package com.wx.demo.service;

import com.wx.demo.tools.Settings;

public enum ServiceSoftwareId {
    SOFTWARE_ZF(Settings.getSet().SOFTWARE_ZF),
    SOFTWARE_YY(Settings.getSet().SOFTWARE_YY),
    SOFTWARE_Demo("777"),
    SHQB(Settings.getSet().SHQB),
    SHQB2("12354332"),
    MQ(Settings.getSet().MQID),
    NIUNIU("73405343"),
    SHQB_HELPER("938478032"),
    NIUNIU_HELPER("734053432"),
    QLJSF("78674541");
    public String softwareId;

    ServiceSoftwareId(String softwareId) {
        this.softwareId = softwareId;
    }
}
