define("appmsg/like.js",["biz_common/dom/event.js","biz_common/dom/class.js","biz_wap/utils/ajax.js","appmsg/log.js","complain/tips.js","appmsg/retry_ajax.js","biz_wap/jsapi/core.js","biz_wap/utils/mmversion.js"],function(require,exports,module,alert){
    "use strict";
    function qs(e){
        return document.getElementById(e);
    }
    function initLikeEvent(opt){
        function show(e){
            e.style.display="";
        }
        function hide(e){
            e.style.display="none";
        }
        function vShow(e){
            e.style.visibility="visible";
        }
        function vHide(e){
            e.style.visibility="hidden";
        }
        function clear(e){
            e.value="";
        }
        var scrollTop,el_like=opt.likeAreaDom,el_likeNum=opt.likeNumDom,showType=opt.showType,prompted=opt.prompted,allPage=document.getElementsByTagName("html")[0],el_likeEducate=qs("js_like_educate"),el_likeToast=qs("js_like_toast"),el_likeBtn=qs("js_like_btn"),el_acknowledge=qs("js_acknowledge"),el_toastMsg=qs("js_toast_msg"),el_alikeComment=qs("js_a_like_comment"),el_alikeCommentConfirm=qs("js_a_like_confirm"),el_alikeCommentText=qs("js_a_like_comment_text"),el_acommentLenSpan=qs("like_a_comment_len_span"),el_acommentLen=qs("like_a_comment_len"),el_acommentErrorMsg=qs("js_a_like_comment_msg"),el_acommentCurrentCount=qs("js_a_like_current_cnt"),el_alikeCommentShare=qs("js_a_like_comment_share"),el_bcommentPanel=qs("js_b_comment_panel"),el_blikeConfirm=qs("js_b_like_confirm"),el_blikeCommentTextFirst=qs("js_b_comment_text_first"),el_blikeCommentTextSecond=qs("js_b_comment_text_second"),el_bcommentCancel=qs("js_b_comment_cancel"),el_bcommentConfirm=qs("js_b_comment_confirm"),el_bcommentErrorMsg=qs("js_b_like_comment_msg"),el_bcommentCurrentCount=qs("js_b_like_current_cnt"),el_bcommentPanel2=qs("js_b_comment_final"),haokanLock=!1,startY;
        if(el_like&&el_likeNum){
            var img=new Image;
            window.appmsg_like_type&&2===window.appmsg_like_type?img.src=location.protocol+"//mp.weixin.qq.com/mp/jsmonitor?idkey=114217_0_1":window.appmsg_like_type&&1===window.appmsg_like_type&&(img.src=location.protocol+"//mp.weixin.qq.com/mp/jsmonitor?idkey=114217_1_1"),
                JSAPI.on("menu:haokan",function(e){
                    var t=0===parseInt(e.recommend)?0:1;
                    if(0===t)sendRecommendAjax(t,"",2,clientShowType);else{
                        var n="";
                        n=e.comment,sendRecommendAjax(t,n,5,clientShowType);
                    }
                }),2===showType&&(el_bcommentConfirm.setAttribute("disabled","disabled"),el_bcommentConfirm.innerHTML="鍙戦€�");
            var like_report=function(){
                log("[Appmsg] click like");
                var e=el_like.getAttribute("like"),t=el_likeNum.innerHTML,n=parseInt(e)?parseInt(e):0,o=n?0:1,i=parseInt(t)?parseInt(t):0,l=opt.appmsgid||opt.mid,s=opt.itemidx||opt.idx;
                if(n){
                    if(1!==appmsg_like_type)return void sendRecommendAjax(0);
                    Class.removeClass(el_like,opt.className),el_like.setAttribute("like",0),i>0&&"100000+"!==t&&(el_likeNum.innerHTML=i-1==0?"璧�":i-1);
                }else if(1===appmsg_like_type)el_like.setAttribute("like",1),Class.addClass(el_like,opt.className),
                "100000+"!==t&&(el_likeNum.innerHTML=i+1);else if(2===appmsg_like_type)return void initRecommendPanel();
                RetryAjax({
                    url:"/mp/appmsg_like?__biz="+opt.biz+"&mid="+opt.mid+"&idx="+opt.idx+"&like="+o+"&f=json&appmsgid="+l+"&itemidx="+s,
                    data:{
                        is_temp_url:opt.is_temp_url||0,
                        scene:window.source,
                        subscene:window.subscene,
                        appmsg_like_type:window.appmsg_like_type,
                        item_show_type:window.item_show_type,
                        client_version:window.clientversion,
                        action_type:o?1:2,
                        device_type:window.devicetype
                    },
                    type:"POST"
                });
            },initRecommendPanel=function(){
                if(1!==showType&&2!==showType||1!==prompted)if(1!==showType&&2!==showType||0!==prompted){
                    if(3===showType)if(isShow(el_bcommentPanel)||isShow(el_bcommentPanel2))!isShow(el_bcommentPanel)&&isShow(el_bcommentPanel2)?hide(el_bcommentPanel2):isShow(el_bcommentPanel)&&!isShow(el_bcommentPanel2)&&hide(el_bcommentPanel);else{
                        var e=qs("like3").offsetTop-document.body.scrollTop;
                        show(el_bcommentPanel),qs("js_b_wrp").clientHeight+e+50>document.documentElement.clientHeight?Class.addClass(qs("js_b_wrp"),"like_comment_primary_pos_top"):Class.removeClass(qs("js_b_wrp"),"like_comment_primary_pos_top");
                    }
                }else qs("educate_title").innerHTML="鍙戦€佸埌鐪嬩竴鐪�",show(el_likeEducate),show(qs("educate_btn"));else sendRecommendAjax(1,"",1);
            },isShow=function(e){
                return"none"===e.style.display||"hidden"===e.style.visibility?!1:""===e.style.display||"block"===e.style.display||"visible"===e.style.visibility?!0:void 0;
            },connectWithApp=function(e,t,n){
                var o={
                    origin:"mp",
                    isLike:e?1:0,
                    url:encodeURIComponent(msg_link.html(!1)),
                    content:t?t:""
                };
                JSAPI.invoke("handleHaokanAction",{
                    action:actionString,
                    recommend:e?1:0,
                    server_data:JSON.stringify(o)
                },function(e){
                    console.log("handleHaokanAction",e);
                }),setTimeout(function(){
                    (3===showType&&1===e||n)&&(o={
                        origin:"mp",
                        isLike:e?1:0,
                        url:encodeURIComponent(msg_link.html(!1)),
                        content:""
                    },JSAPI.invoke("handleHaokanAction",{
                        action:actionString,
                        recommend:e?1:0,
                        server_data:JSON.stringify(o)
                    },function(e){
                        console.log("handleHaokanAction",e);
                    }));
                },500),JSAPI.invoke("handleHaokanAction",{
                    action:actionForClient,
                    permission:1,
                    recommend:e?1:0
                },function(e){
                    console.log("handleHaokanAction for client",e);
                });
            },isBeenUnvisible=function(e){
                return e.offsetTop-document.body.scrollTop>=document.documentElement.clientHeight-60?!0:!1;
            };
            DomEvent.on(el_like,"click",function(e){
                return like_report(e),!1;
            }),DomEvent.on(el_blikeConfirm,"click",function(){
                sendRecommendAjax(1,"",1);
            }),DomEvent.on(qs("js_mask_1"),"click",function(){
                hide(el_bcommentPanel);
            }),DomEvent.on(qs("js_mask_2"),"mousedown",function(){
                hide(el_bcommentPanel2),clear(el_blikeCommentTextSecond),vHide(el_bcommentErrorMsg),
                    enableMove();
            }),DomEvent.on(el_blikeCommentTextFirst,"click",function(){
                scrollTop=document.body.scrollTop||document.documentElement.scrollTop||0,hide(el_bcommentPanel),
                    show(el_bcommentPanel2),el_blikeCommentTextSecond.focus(),disableMove();
            }),DomEvent.on(el_bcommentConfirm,"mousedown",function(){
                var e;
                2===showType?e=4:3===showType&&(e=5),validataComment(el_blikeCommentTextSecond,e);
            }),DomEvent.on(el_bcommentCancel,"mousedown",function(){
                hide(el_bcommentPanel2),clear(el_blikeCommentTextSecond),vHide(el_bcommentErrorMsg),
                    enableMove();
            }),DomEvent.on(el_acknowledge,"click",function(){
                hide(el_likeEducate);
            }),DomEvent.on(qs("js_cancel"),"click",function(){
                hide(el_likeEducate);
            }),DomEvent.on(qs("js_fail_inform"),"click",function(){
                hide(qs("js_fail"));
            }),DomEvent.on(qs("js_confirm"),"click",function(){
                sendRecommendAjax(1,"",1);
            }),DomEvent.on(el_alikeCommentShare,"click",function(){
                scrollTop=document.body.scrollTop||document.documentElement.scrollTop,show(el_bcommentPanel2),
                    el_blikeCommentTextSecond.focus(),el_bcommentConfirm.setAttribute("disabled","disabled"),
                    disableMove();
            }),DomEvent.on(el_blikeCommentTextSecond,"focus",function(){}),DomEvent.on(el_blikeCommentTextSecond,"blur",function(){
                window.scrollTo(0,scrollTop);
            }),DomEvent.on(qs("js_unlike_know"),"click",function(){
                hide(qs("js_unlike_educate"));
            });
            var disableMove=function(){
                document.addEventListener("touchmove",preventMove,{
                    passive:!1
                }),el_blikeCommentTextSecond.addEventListener("touchstart",getTouchStart,{
                    passive:!1
                }),el_blikeCommentTextSecond.addEventListener("touchmove",preventText,!1);
            },enableMove=function(){
                document.removeEventListener("touchmove",preventMove,{
                    passive:!1
                }),el_blikeCommentTextSecond.removeEventListener("touchstart",getTouchStart,{
                    passive:!1
                }),el_blikeCommentTextSecond.removeEventListener("touchmove",preventText,!1);
            },preventMove=function(e){
                var t=e.target;
                "TEXTAREA"!==t.tagName&&"BUTTON"!==t.tagName&&(e.preventDefault(),e.stopPropagation());
            },getTouchStart=function(e){
                var t=e.targetTouches||[];
                if(t.length>0){
                    var n=t[0]||{};
                    startY=n.clientY;
                }
            },preventText=function(e){
                var t=!1,n=e.changedTouches,o=this.scrollTop,i=this.offsetHeight,l=this.scrollHeight;
                if(n.length>0){
                    var s=n[0]||{},m=s.clientY;
                    t=m>startY&&0>=o?!1:startY>m&&o+i>=l?!1:!0,t||e.preventDefault();
                }
            },unsetLike2Status=function(e){
                1===e?show(qs("js_unlike_educate")):(el_toastMsg.innerHTML="宸插彇娑�",show(el_likeToast),
                    setTimeout(function(){
                        hide(el_likeToast);
                    },1e3)),2===showType&&isShow(el_alikeComment)&&(hide(el_alikeComment),vHide(el_acommentErrorMsg));
                var t=el_likeNum.innerHTML;
                Class.removeClass(el_likeBtn,opt.className),el_like.setAttribute("like",0),el_alikeComment&&hide(el_alikeComment),
                    realLikeNum-=1,realLikeNum>=0&&"10涓�+"!==t&&(el_likeNum.innerHTML=dealLikeReadShow(realLikeNum));
            },setLike2Status=function(e){
                var t="鍦ㄧ湅";
                switch(showType){
                    case 1:
                        switch(prompted){
                            case 0:
                                hide(el_likeEducate),prompted=1;
                                break;

                            case 1:
                                el_toastMsg.innerHTML=t,show(el_likeToast),setTimeout(function(){
                                    hide(el_likeToast);
                                },1e3);
                        }
                        setBtnLike();
                        break;

                    case 2:
                        switch(hide(el_bcommentPanel2),clear(el_blikeCommentTextSecond),prompted){
                            case 0:
                                hide(el_likeEducate),prompted=1;
                                break;

                            case 1:
                                el_toastMsg.innerHTML=4===e?"宸插彂閫�":t,(4===e||5===e)&&(show(el_likeToast),setTimeout(function(){
                                    hide(el_likeToast);
                                },1e3));
                        }
                        5!==e&&(4===e?hide(el_alikeComment):show(el_alikeComment),isBeenUnvisible(el_alikeComment)&&scrollToShow(el_alikeComment)),
                        4!==e&&setBtnLike();
                        break;

                    case 3:
                        switch(hide(el_bcommentPanel2),hide(el_bcommentPanel),clear(el_blikeCommentTextSecond),
                            prompted){
                            case 0:
                                qs("educate_title").innerHTML="宸插彂閫佸埌鐪嬩竴鐪�",show(el_likeEducate),show(educate_btn2),
                                    prompted=1;
                                break;

                            case 1:
                                el_toastMsg.innerHTML=t,show(el_likeToast),setTimeout(function(){
                                    hide(el_likeToast);
                                },1e3);
                        }
                        setBtnLike();
                }
                enableMove();
            },setBtnLike=function(){
                el_like.setAttribute("like",1),Class.addClass(el_likeBtn,opt.className),realLikeNum+=1;
                var e=el_likeNum.innerHTML;
                "10涓�+"!==e&&(el_likeNum.innerHTML=dealLikeReadShow(realLikeNum));
            },scrollToShow=function(e){
                window.scrollTo(0,e.offsetHeight+window.scrollY);
            };
            DomEvent.on(el_blikeCommentTextSecond,"input",function(){
                var e=el_blikeCommentTextSecond.value.replace(/^\s+|\s+$/g,"");
                e.length>200?(el_bcommentCurrentCount.innerHTML=e.length,vShow(el_bcommentErrorMsg)):vHide(el_bcommentErrorMsg),
                    e.length>0&&e.length<=200?el_bcommentConfirm.removeAttribute("disabled"):0===e.length&&3===showType?el_bcommentConfirm.removeAttribute("disabled"):el_bcommentConfirm.setAttribute("disabled","disabled");
            });
            var validataComment=function(e,t){
                var n=e.value.replace(/^\s+|\s+$/g,"");
                sendRecommendAjax(1,n,t);
            },sendRecommendAjax=function sendRecommendAjax(like,comment,type,clientType){
                if(!haokanLock){
                    show(qs("js_loading"));
                    var appmsgid=opt.appmsgid||opt.mid,itemidx=opt.itemidx||opt.idx;
                    haokanLock=!0;
                    var action_type;
                    action_type=like?type:2,ajax({
                        url:"/mp/appmsg_like?__biz="+opt.biz+"&mid="+opt.mid+"&idx="+opt.idx+"&like="+like+"&f=json&appmsgid="+appmsgid+"&itemidx="+itemidx,
                        data:{
                            is_temp_url:opt.is_temp_url||0,
                            scene:window.source,
                            subscene:window.subscene,
                            appmsg_like_type:window.appmsg_like_type,
                            item_show_type:window.item_show_type,
                            client_version:window.clientversion,
                            comment:comment?comment:"",
                            prompted:1,
                            style:clientType||showType,
                            action_type:action_type,
                            passparam:window.passparam,
                            request_id:(new Date).getTime(),
                            device_type:window.devicetype
                        },
                        type:"POST",
                        success:function success(res){
                            haokanLock=!1;
                            var data=eval("("+res+")");
                            hide(qs("js_loading")),0==data.base_resp.ret?(like?setLike2Status(type):unsetLike2Status(data.has_comment),
                                connectWithApp(like,comment,clientType)):show(qs("js_fail"));
                        },
                        error:function(){
                            hide(qs("js_loading")),show(qs("js_fail")),haokanLock=!1;
                        }
                    });
                }
            };
        }
    }
    function showLikeNum(e){
        var t=e||{};
        if(t.show){
            var n=t.likeAreaDom,o=t.likeNumDom,i=document.getElementById("js_like_btn");
            n&&(n.style.display=t.likeAreaDisplayValue,t.liked&&(1===appmsg_like_type?Class.addClass(n,t.className):Class.addClass(i,t.className)),
                n.setAttribute("like",t.liked?"1":"0"));
            var l=1===appmsg_like_type?"璧�":"";
            realLikeNum=t.likeNum||l,1===appmsg_like_type?(parseInt(realLikeNum)>1e5?realLikeNum="100000+":"",
            o&&(o.innerHTML=realLikeNum)):2===appmsg_like_type&&(o.innerHTML=dealLikeReadShow(realLikeNum));
        }
    }
    function dealLikeReadShow(e){
        var t="";
        if(parseInt(e)>1e5)t="10涓�+";else if(parseInt(e)>1e4&&parseInt(e)<=1e5){
            var n=""+parseInt(e)/1e4,o=n.indexOf(".");
            t=-1===o?n+"涓�":n.substr(0,o)+"."+n.charAt(o+1)+"涓�";
        }else t=0===parseInt(e)?"":e;
        return t;
    }
    function showReadNum(e){
        var t=e||{};
        if(t.show){
            var n=t.readAreaDom,o=t.readNumDom;
            n&&(n.style.display=t.readAreaDisplayValue);
            var i=t.readNum||1;
            1===appmsg_like_type?(parseInt(i)>1e5?i="100000+":"",o&&(o.innerHTML=i)):2===appmsg_like_type&&(o.innerHTML=dealLikeReadShow(i));
        }
    }
    var DomEvent=require("biz_common/dom/event.js"),Class=require("biz_common/dom/class.js"),ajax=require("biz_wap/utils/ajax.js"),log=require("appmsg/log.js"),Tips=require("complain/tips.js"),RetryAjax=require("appmsg/retry_ajax.js"),JSAPI=require("biz_wap/jsapi/core.js"),actionString="submitMsgToTL",actionForClient="update_recommend_status",mmversion=require("biz_wap/utils/mmversion.js"),realLikeNum,clientShowType=5;
    return{
        initLikeEvent:initLikeEvent,
        showLikeNum:showLikeNum,
        showReadNum:showReadNum
    };
});