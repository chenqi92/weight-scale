package cn.allbs.weightscale.config;

import cn.allbs.weightscale.enums.IResultCode;
import cn.allbs.weightscale.enums.SystemCode;
import cn.allbs.weightscale.exception.ServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

import static cn.allbs.weightscale.constants.CommonConstants.SUCCESS;
import static cn.allbs.weightscale.constants.CommonConstants.SUCCESS_MSG;

/**
 * 类 R 通用返回封装
 *
 * @author ChenQi
 * @date 2024/2/26
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    @Schema(description = "返回码")
    private int code;

    /**
     * 返回提示信息
     */
    @Schema(description = "提示信息")
    private String msg;

    /**
     * 返回数据
     */
    @Schema(description = "返回对象")
    private T data;

    private R(int code, String msg) {
        this(code, msg, null);
    }

    private R(IResultCode resultCode) {
        this(resultCode, resultCode.getMsg(), null);
    }

    private R(IResultCode resultCode, String msg) {
        this(resultCode, msg, null);
    }

    private R(IResultCode resultCode, T data) {
        this(resultCode, resultCode.getMsg(), data);
    }

    private R(IResultCode resultCode, String msg, T data) {
        this.code = resultCode.getCode();
        this.msg = msg;
        this.data = data;
    }

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<T> ok(T data, String msg, int code) {
        return restResult(data, code, msg);
    }

    /**
     * 返回失败信息，用于 web
     *
     * @param msg 失败信息
     * @param <T> 泛型标记
     * @return {Result}
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(SystemCode.FAILURE, msg);
    }

    /**
     * 返回失败信息，用于 web
     *
     * @param msg 失败信息
     * @param <T> 泛型标记
     * @return {Result}
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    /**
     * 返回失败信息
     *
     * @param rCode 异常枚举
     * @param <T>   泛型标记
     * @return {Result}
     */
    public static <T> R<T> fail(IResultCode rCode) {
        return new R<>(rCode);
    }

    /**
     * 返回失败信息
     *
     * @param rCode 异常枚举
     * @param msg   失败信息
     * @param <T>   泛型标记
     * @return {Result}
     */
    public static <T> R<T> fail(IResultCode rCode, String msg) {
        return new R<>(rCode, msg);
    }

    /**
     * 直接抛出失败异常，抛出 code 码
     *
     * @param rCode IResultCode
     */
    public static void throwFail(IResultCode rCode) {
        throw new ServiceException(rCode);
    }

    /**
     * 直接抛出失败异常，抛出 code 码
     *
     * @param rCode   IResultCode
     * @param message 自定义消息
     */
    public static void throwFail(IResultCode rCode, String message) {
        throw new ServiceException(rCode, message);
    }

    /**
     * 直接抛出失败异常，抛出 code 码
     *
     * @param message 自定义消息
     */
    public static void throwFail(String message) {
        throwFail(SystemCode.FAILURE, message);
    }


    static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public boolean isOk() {
        return this.code == SUCCESS;
    }
}
