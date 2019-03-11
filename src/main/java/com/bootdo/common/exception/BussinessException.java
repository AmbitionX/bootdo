package com.bootdo.common.exception;

public class BussinessException extends RuntimeException {
    private static final long serialVersionUID = 538922474277376456L;
    /**
     * JOSN形式返回到页面
     */
    public static final int TYPE_JSON = 1;
    /**
     * 关闭窗
     */
    public static final int TYPE_CLOSE = 2;
    /**
     * 跳转url
     */
    protected String url;

    /**
     * 返回类型
     */
    protected int type;

    /**
     * 页面按钮名字
     */
    protected String buttonName;

    /**
     * 页面按钮名字
     */
    protected String errorCode;

    /**
     * 任意对象
     */
    protected Object object;

    public BussinessException(String msg, RuntimeException ex) {
        super(msg, ex);
    }

    public BussinessException() {
        super();
    }

    public BussinessException(String message) {
        super(message);
    }

    public BussinessException(String message, String url) {
        super(message);
        this.url = url;
    }

    public BussinessException(String message, String url, String buttonName) {
        super(message);
        this.url = url;
        this.buttonName = buttonName;
    }

    public BussinessException(String message, int type) {
        super(message);
        this.type = type;
    }

    /**
     * @param message 异常信息
     * @param object  需要处理的对象
     * @author zlhx
     * @date 2015年7月25日 上午10:40:57
     */
    public BussinessException(String message, Object object) {
        super(message);
        this.object = object;
    }

    /**
     * @param message 提示信息
     * @param url     错误跳转url
     * @param type    错误提示类型
     */
    public BussinessException(String message, String url, int type) {
        super(message);
        this.url = url;
        this.type = type;
    }

    public BussinessException(Builder builder) {
        super(builder.message);
        this.url = builder.url;
        this.type = builder.type;
        this.buttonName = builder.buttonName;
        this.errorCode = builder.errorCode;
    }

    public int getType() {
        return type;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取 the object
     *
     * @return
     */
    public Object getObject() {
        return object;
    }

    /**
     * 设置 the object
     *
     * @param
     */
    public void setObject(Object object) {
        this.object = object;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public static class Builder {

        /**
         * 跳转url
         */
        protected String url;

        /**
         * 返回类型
         */
        protected int type;

        /**
         * 页面按钮名字
         */
        protected String buttonName;

        protected String errorCode;

        protected String message;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setButtonName(String buttonName) {
            this.buttonName = buttonName;
            return this;
        }

        public Builder setErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public BussinessException build() {
            return new BussinessException(this);
        }
    }
}
