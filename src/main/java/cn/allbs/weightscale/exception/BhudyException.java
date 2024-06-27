package cn.allbs.weightscale.exception;

/**
 * 类 BhudyException
 *
 * @author ChenQi
 * @date 2024/6/27
 */
public class BhudyException extends RuntimeException {

    public BhudyException() {
        super("串口收发数据失败!");
    }

    public BhudyException(String msg) {
        super(msg);
    }

    public BhudyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
