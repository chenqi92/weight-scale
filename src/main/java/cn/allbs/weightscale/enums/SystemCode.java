package cn.allbs.weightscale.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 枚举 SystemCode
 * </p>
 * 自定义异常code及提示信息
 *
 * @author ChenQi
 * @date 2024/3/12
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "自定义异常枚举", name = "异常定义")
public enum SystemCode implements IResultCode {

    /**
     * 权限部分异常
     */
    FORBIDDEN_401(SystemCode.LOGIN_CODE, "未经认证!"),

    TOKEN_NOT_IN_SYSTEM(SystemCode.LOGIN_CODE, "登录过期!"),

    AUTHORIZATION_ERROR(SystemCode.LOGIN_CODE, "权限处理逻辑出现异常!"),

    USERNAME_OR_PASSWORD_ERROR(SystemCode.LOGIN_CODE, "用户名或密码错误"),

    USER_NOT_FOUND_ERROR(SystemCode.LOGIN_CODE, "不存在的用户名"),

    FORBIDDEN_403(SystemCode.AUTHORIZATION_CODE, "缺少资源访问权限!"),
    /**
     * 系统未知异常
     */
    FAILURE(SystemCode.FAILURE_CODE, "系统未知异常"),

    /**
     * 操作成功
     */
    SUCCESS(SystemCode.SUCCESS_CODE, "操作成功"),

    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(SystemCode.PARAM_MISS_CODE, "缺少必要的请求参数"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(SystemCode.PARAM_TYPE_ERROR_CODE, "请求参数类型错误"),

    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(SystemCode.PARAM_BIND_ERROR_CODE, "请求参数绑定错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(SystemCode.PARAM_VALID_ERROR_CODE, "参数校验失败"),

    /**
     * 404 没找到请求
     */
    NOT_FOUND(SystemCode.NOT_FOUND_CODE, "404 没找到请求"),

    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(SystemCode.MSG_NOT_READABLE_CODE, "消息不能读取"),

    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(SystemCode.METHOD_NOT_SUPPORTED_CODE, "不支持当前请求方法"),

    /**
     * 不支持当前媒体类型
     */
    MEDIA_TYPE_NOT_SUPPORTED(SystemCode.MEDIA_TYPE_NOT_SUPPORTED_CODE, "不支持当前媒体类型"),

    /**
     * 不接受的媒体类型
     */
    MEDIA_TYPE_NOT_ACCEPT(SystemCode.MEDIA_TYPE_NOT_ACCEPT_CODE, "不接受的媒体类型"),

    /**
     * 请求被拒绝
     */
    REQ_REJECT(SystemCode.REQ_REJECT_CODE, "请求被拒绝"),

    //-------------------------------------------------------------//
    /**
     * 数据操作相关错误
     */
    DB_ERROR(SystemCode.DB_ERROR_CODE, "数据库操作出错"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(SystemCode.DATA_NOT_EXIST_CODE, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_EXISTED(SystemCode.DATA_EXISTED_CODE, "数据已存在"),

    /**
     * 数据添加失败
     */
    DATA_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "数据添加失败"),

    /**
     * 数据更新失败
     */
    DATA_UPDATE_FAILED(SystemCode.DATA_UPDATE_FAILED_CODE, "数据更新失败"),

    /**
     * 数据删除失败
     */
    DATA_DELETE_FAILED(SystemCode.DATA_DELETE_FAILED_CODE, "数据删除失败"),
    ;

    /**
     * 通用 异常 code
     */
    public static final int FAILURE_CODE = -1;
    public static final int SUCCESS_CODE = 200;
    public static final int LOGIN_CODE = 401;
    public static final int AUTHORIZATION_CODE = 403;
    public static final int PARAM_MISS_CODE = 900;
    public static final int PARAM_TYPE_ERROR_CODE = 901;
    public static final int PARAM_BIND_ERROR_CODE = 902;
    public static final int PARAM_VALID_ERROR_CODE = 903;
    public static final int NOT_FOUND_CODE = 904;
    public static final int MSG_NOT_READABLE_CODE = 905;
    public static final int METHOD_NOT_SUPPORTED_CODE = 906;
    public static final int MEDIA_TYPE_NOT_SUPPORTED_CODE = 907;
    public static final int MEDIA_TYPE_NOT_ACCEPT_CODE = 908;
    public static final int REQ_REJECT_CODE = 909;

    /**
     * 通用数据层 code
     */
    public static final int DB_ERROR_CODE = 900;
    public static final int DATA_NOT_EXIST_CODE = 930;
    public static final int DATA_EXISTED_CODE = 931;
    public static final int DATA_ADD_FAILED_CODE = 932;
    public static final int DATA_UPDATE_FAILED_CODE = 933;
    public static final int DATA_DELETE_FAILED_CODE = 934;

    /**
     * code编码
     */
    private final int code;
    /**
     * 中文信息描述
     */
    private final String msg;
}
