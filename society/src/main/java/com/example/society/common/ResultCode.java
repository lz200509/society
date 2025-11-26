// ResultCode.java
package com.example.society.common;

/**
 * 返回状态码枚举
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    CREATED(201, "创建成功"),
    ACCEPTED(202, "请求已接受"),
    NO_CONTENT(204, "操作成功，无返回内容"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),

    ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务相关状态码
    LOGIN_SUCCESS(200, "登录成功"),
    LOGIN_FAILED(400, "登录失败"),
    REGISTER_SUCCESS(200, "注册成功"),
    REGISTER_FAILED(400, "注册失败"),
    APPLY_SUCCESS(200, "申请提交成功"),
    APPLY_FAILED(400, "申请提交失败"),
    AUDIT_SUCCESS(200, "审核操作成功"),
    AUDIT_FAILED(400, "审核操作失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}