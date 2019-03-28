define("biz_wap/utils/ajax.js", ["biz_common/utils/string/html.js", "biz_common/utils/url/parse.js", "biz_common/utils/respTypes.js", "biz_wap/utils/ajax_wx.js"], function (require, exports, module, alert) {
    "use strict";

    function logClientLog(e) {
        try {
            var t;
            /(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent) ? t = "writeLog" : /(Android)/i.test(navigator.userAgent) && (t = "log"),
            t && doLog(t, e);
        } catch (o) {
            throw console.error(o), o;
        }
    }

    function doLog(e, t) {
        var o, r, n = {};
        o = top != window ? top.window : window;
        try {
            r = o.WeixinJSBridge, n = o.document;
        } catch (a) {
        }
        e && r && r.invoke ? r.invoke(e, {
            level: "info",
            msg: "[WechatFe][ajax]" + t
        }) : setTimeout(function () {
            n.addEventListener ? n.addEventListener("WeixinJSBridgeReady", function () {
                doLog(e, t);
            }, !1) : n.attachEvent && (n.attachEvent("WeixinJSBridgeReady", function () {
                doLog(e, t);
            }), n.attachEvent("onWeixinJSBridgeReady", function () {
                doLog(e, t);
            }));
        }, 0);
    }

    function joinUrl(e) {
        var t = {};
        return "undefined" != typeof uin && (t.uin = uin), "undefined" != typeof key && (t.key = key),
        "undefined" != typeof pass_ticket && (t.pass_ticket = pass_ticket), "undefined" != typeof wxtoken && (t.wxtoken = wxtoken),
        "undefined" != typeof window.devicetype && (t.devicetype = window.devicetype), "undefined" != typeof window.clientversion && (t.clientversion = window.clientversion),
            "undefined" != typeof appmsg_token ? t.appmsg_token = appmsg_token : e.indexOf("advertisement_report") > -1 && ((new Image).src = location.protocol + "//mp.weixin.qq.com/mp/jsmonitor?idkey=68064_13_1&r=" + Math.random()),
            t.x5 = isx5 ? "1" : "0", t.f = "json", Url.join(e, t);
    }

    function reportRt(e, t, o) {
        var r = "";
        if (o && o.length) {
            var n = 1e3, a = o.length, i = Math.ceil(a / n);
            r = ["&lc=" + i];
            for (var s = 0; i > s; ++s) r.push("&log" + s + "=[rtCheckError][" + s + "]" + encodeURIComponent(o.substr(s * n, n)));
            r = r.join("");
        }
        var c, d = "idkey=" + e + "_" + t + "_1" + r + "&r=" + Math.random();
        if (window.ActiveXObject) try {
            c = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (p) {
            try {
                c = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (u) {
                c = !1;
            }
        } else window.XMLHttpRequest && (c = new XMLHttpRequest);
        c && (c.open("POST", location.protocol + "//mp.weixin.qq.com/mp/jsmonitor?", !0), c.setRequestHeader("cache-control", "no-cache"),
            c.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            c.setRequestHeader("X-Requested-With", "XMLHttpRequest"), c.send(d));
    }

    function reportAjaxLength(e, t, o) {
        var r = "";
        if (o && o.length) {
            var n = 1e3, a = o.length, i = Math.ceil(a / n);
            r = ["&lc=" + i];
            for (var s = 0; i > s; ++s) r.push("&log" + s + "=[Ajax Length Limit][" + s + "]" + encodeURIComponent(o.substr(s * n, n)));
            r = r.join("");
        }
        var c, d = "idkey=" + e + "_" + t + "_1" + r + "&r=" + Math.random();
        if (window.ActiveXObject) try {
            c = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (p) {
            try {
                c = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (u) {
                c = !1;
            }
        } else window.XMLHttpRequest && (c = new XMLHttpRequest);
        c && (c.open("POST", location.protocol + "//mp.weixin.qq.com/mp/jsmonitor?", !0), c.setRequestHeader("cache-control", "no-cache"),
            c.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
            c.setRequestHeader("X-Requested-With", "XMLHttpRequest"), c.send(d), (new Image).src = "/mp/jsmonitor?idkey=" + e + "_" + t + "_1" + r + "&r=" + Math.random());
    }

    function Ajax(obj) {
        var type = (obj.type || "GET").toUpperCase(), url;
        url = obj.notJoinUrl ? obj.url : joinUrl(obj.url), "html" == obj.f && (url = url.replace("&f=json", ""));
        var mayAbort = !!obj.mayAbort, async = "undefined" == typeof obj.async ? !0 : obj.async,
            xhr = new XMLHttpRequest, timer = null, data = null;
        if ("object" == typeof obj.data) {
            var d = obj.data;
            data = [];
            for (var k in d) d.hasOwnProperty(k) && data.push(k + "=" + encodeURIComponent(d[k]));
            data = data.join("&");
        } else data = "string" == typeof obj.data ? obj.data : null;
        xhr.open(type, url, async);
        var _onreadystatechange = xhr.onreadystatechange;
        try {
            url && url.length > LENGTH_LIMIT && reportAjaxLength(27613, 17, "ajax get limit[length: " + url.length + "]" + url.substring(0, 1024));
        } catch (e) {
        }
        xhr.onreadystatechange = function () {
            if ("function" == typeof _onreadystatechange && _onreadystatechange.apply(xhr), 3 == xhr.readyState && obj.received && obj.received(xhr),
            4 == xhr.readyState) {
                xhr.onreadystatechange = null;
                var status = xhr.status;
                if (status >= 200 && 400 > status) try {
                    var responseText = xhr.responseText, resp = responseText;
                    if ("json" == obj.dataType) try {
                        resp = eval("(" + resp + ")");
                        var rtId = obj.rtId, rtKey = obj.rtKey || 0, rtDesc = obj.rtDesc, checkRet = !0;
                        rtId && rtDesc && RespTypes && !RespTypes.check(resp, rtDesc) && reportRt(rtId, rtKey, RespTypes.getMsg() + "[detail]" + responseText + ";" + obj.url);
                    } catch (e) {
                        return void (obj.error && obj.error(xhr));
                    }
                    obj.success && obj.success(resp);
                } catch (e) {
                    throw __moon_report({
                        offset: MOON_AJAX_SUCCESS_OFFSET,
                        e: e
                    }), e;
                } else {
                    try {
                        obj.error && obj.error(xhr);
                    } catch (e) {
                        throw __moon_report({
                            offset: MOON_AJAX_ERROR_OFFSET,
                            e: e
                        }), e;
                    }
                    if (status || !mayAbort) {
                        var __ajaxtest = window.__ajaxtest || "0";
                        __moon_report({
                            offset: MOON_AJAX_NETWORK_OFFSET,
                            log: "ajax_network_error[" + status + "][" + __ajaxtest + "]: " + url + ";host:" + location.host,
                            e: ""
                        });
                    }
                }
                clearTimeout(timer);
                try {
                    obj.complete && obj.complete();
                } catch (e) {
                    throw __moon_report({
                        offset: MOON_AJAX_COMPLETE_OFFSET,
                        e: e
                    }), e;
                }
                obj.complete = null;
            }
        }, "POST" == type && xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
        obj.noXRequestedWidthHeader || xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest"),
        "undefined" != typeof obj.timeout && (timer = setTimeout(function () {
            xhr.abort("timeout");
            try {
                obj.complete && obj.complete();
            } catch (e) {
                throw __moon_report({
                    offset: MOON_AJAX_COMPLETE_OFFSET,
                    e: e
                }), e;
            }
            obj.complete = null, __moon_report({
                offset: MOON_AJAX_TIMEOUT_OFFSET,
                log: "ajax_timeout_error: " + url,
                e: ""
            });
        }, obj.timeout));
        try {
            xhr.send(data);
            try {
                data && data.length > LENGTH_LIMIT && reportAjaxLength(27613, 18, "ajax post limit[length: " + data.length + "]" + data.substring(0, 1024));
            } catch (e) {
            }
        } catch (e) {
            obj.error && obj.error(xhr);
        }
        return xhr;
    }

    require("biz_common/utils/string/html.js");
    var Url = require("biz_common/utils/url/parse.js"), RespTypes = require("biz_common/utils/respTypes.js"),
        Ajax_wx = require("biz_wap/utils/ajax_wx.js"), isx5 = -1 != navigator.userAgent.indexOf("TBS/"),
        __moon_report = window.__moon_report || function () {
        }, MOON_AJAX_SUCCESS_OFFSET = 3, MOON_AJAX_NETWORK_OFFSET = 4, MOON_AJAX_ERROR_OFFSET = 5,
        MOON_AJAX_TIMEOUT_OFFSET = 6, MOON_AJAX_COMPLETE_OFFSET = 7, MOON_AJAX_GET_LIMIT_4K = 17,
        MOON_AJAX_POST_LIMIT_4K = 18, LENGTH_LIMIT = 4096, doc = {}, isAcrossOrigin = !1;
    try {
        doc = top.window.document;
    } catch (e) {
        isAcrossOrigin = !0;
    }
    return window.__second_open__ || !isAcrossOrigin && top.window.__second_open__ ? Ajax_wx : Ajax;
});