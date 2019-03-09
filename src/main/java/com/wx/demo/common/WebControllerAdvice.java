package com.wx.demo.common;

import com.wx.demo.util.MyLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@ControllerAdvice
public class WebControllerAdvice {

    private static final MyLog _log = MyLog.getLog(WebControllerAdvice.class);

    /**
     * 全局异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> errorHandler(Exception ex) {
        _log.error(ex, "");
        String message = ex.getMessage();
        String eMsg = message;
        if (StringUtils.isNotBlank(message) && message.indexOf(":") > 0) {
            eMsg = message.substring(0, message.indexOf(":"));
        }
        if (eMsg.length() > 200) {
            eMsg = eMsg.substring(0, 200);
        }
        BaseResponse baseResponse = new BaseResponse(99999, "系统异常[" + eMsg + "]");
        return ResponseEntity.ok(baseResponse);
    }

    /**
     * 拦截捕捉自定义异常 ServiceException.class
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = WxException.class)
    public ResponseEntity<?> myErrorHandler(WxException ex) {
        return ResponseEntity.ok(BaseResponse.build(ex.getErrCode(), ex.getErrMsg()));
    }

}