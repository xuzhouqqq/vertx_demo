package com.xuzhou.vertx.entity;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = -8614669828681890787L;

    /**
     * 操作成功返回0
     */
    public static final int CODE = 200;
    private static final String MESSAGE = "SUCCESS";

    /**
     * 操作码(取值来自自定义异常的getCode())
     */
    private int code = CODE;

    /**
     * 错误信息(内容来自自定义异常的getMsg())
     */
    private String message = MESSAGE;

    /**
     * 返回的数据
     */
    private T result;

    public Result() {
    }

    public Result(T result) {
        this.code = CODE;
        this.message = MESSAGE;
        this.result = result;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.result = null;
    }

    public Result(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }


    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getResult() {
        return this.result;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
