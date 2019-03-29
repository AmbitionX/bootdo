define("appmsg/appmsgext.js", ["appmsg/log.js", "biz_wap/utils/ajax.js", "rt/appmsg/getappmsgext.rt.js"], function (e) {
    "use strict";

    function t(e) {
        function t(e) {
            for (var t = window.location.href, s = t.indexOf("?"), i = t.substr(s + 1), n = i.split("&"), a = 0; a < n.length; a++) {
                var _ = n[a].split("=");
                if (_[0].toUpperCase() == e.toUpperCase()) return _[1];
            }
            return "";
        }

        var a = {
            biz: "",
            appmsg_type: "",
            mid: "",
            sn: "",
            idx: "",
            scene: "",
            title: "",
            ct: "",
            abtest_cookie: "",
            devicetype: "",
            version: "",
            is_need_ticket: 0,
            is_need_ad: 0,
            comment_id: "",
            is_need_reward: 0,
            both_ad: 0,
            reward_uin_count: 0,
            send_time: "",
            msg_daily_idx: "",
            is_original: 0,
            is_only_read: 0,
            req_id: "",
            pass_ticket: "",
            is_temp_url: 0,
            more_read_type: 0,
            rtId: "",
            rtKey: "",
            appmsg_like_type: 1,
            onSuccess: function () {
            },
            onError: function () {
            }
        };
        for (var _ in e) e.hasOwnProperty(_) && (a[_] = e[_]);
        console.info("[(璇勮銆佺偣璧炪€佽禐璧�) 鍙戦€佽姹俔: ", new Date), i({
            url: "/mp/getappmsgext?f=json&mock=" + t("mock"),
            data: {
                r: Math.random(),
                __biz: a.biz,
                appmsg_type: a.appmsg_type,
                mid: a.mid,
                sn: a.sn,
                idx: a.idx,
                scene: a.scene,
                title: encodeURIComponent(a.title.htmlDecode()),
                ct: a.ct,
                abtest_cookie: a.abtest_cookie,
                devicetype: a.devicetype.htmlDecode(),
                version: a.version.htmlDecode(),
                is_need_ticket: a.is_need_ticket,
                is_need_ad: a.is_need_ad,
                comment_id: a.comment_id,
                is_need_reward: a.is_need_reward,
                both_ad: a.both_ad,
                reward_uin_count: a.is_need_reward ? a.reward_uin_count : 0,
                send_time: a.send_time,
                msg_daily_idx: a.msg_daily_idx,
                is_original: a.is_original,
                is_only_read: a.is_only_read,
                req_id: a.req_id,
                pass_ticket: a.pass_ticket,
                is_temp_url: a.is_temp_url,
                item_show_type: a.item_show_type,
                tmp_version: 1,
                more_read_type: a.more_read_type,
                appmsg_like_type: a.appmsg_like_type
            },
            type: "POST",
            dataType: "json",
            rtId: a.rtId,
            rtKey: a.rtKey,
            rtDesc: n,
            async: !0,
            success: function (e) {
                if (console.info("[(璇勮銆佺偣璧炪€佽禐璧�) 鍝嶅簲璇锋眰]: ", new Date, e), s("[Appmsg] success get async data"),
                "function" == typeof a.onSuccess && a.onSuccess(e), e) try {
                    s("[Appmsg] success get async data, async data is: " + JSON.stringify(e));
                } catch (t) {
                } else s("[Appmsg] success get async data, async data is empty");
            },
            error: function () {
                s("[Appmsg] error get async data, biz=" + a.biz + ", mid=" + a.mid), "function" == typeof a.onError && a.onError();
            }
        });
    }

    var s = e("appmsg/log.js"), i = e("biz_wap/utils/ajax.js"), n = e("rt/appmsg/getappmsgext.rt.js");
    return {
        getData: t
    };
});