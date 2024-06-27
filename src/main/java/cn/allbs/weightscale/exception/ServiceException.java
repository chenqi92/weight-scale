package cn.allbs.weightscale.exception;

import cn.allbs.weightscale.config.R;
import cn.allbs.weightscale.enums.IResultCode;
import org.springframework.lang.Nullable;

/**
 * 类 ServiceException
 *
 * @author ChenQi
 * @date 2024/4/1
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 2359767895161832954L;

    @Nullable
    private final R<?> result;

    public ServiceException(R<?> result) {
        super(result.getMsg());
        this.result = result;
    }

    public ServiceException(IResultCode rCode) {
        this(rCode, rCode.getMsg());
    }

    public ServiceException(IResultCode rCode, String message) {
        super(message);
        this.result = R.fail(rCode, message);
    }

    public ServiceException(String message) {
        super(message);
        this.result = null;
    }

    public ServiceException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        doFillInStackTrace();
        this.result = null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> R<T> getResult() {
        return (R<T>) result;
    }

    /**
     * 提高性能
     *
     * @return Throwable
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }
}
